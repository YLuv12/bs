const lang = require('./utils/lang')

App({
  globalData: {
    userInfo: null,
    token: null,
    lang: 'zh',
    location: null,
    amapKey: 'c33a4d5b17cbe74335a1ab9176b16545'
  },

  onLaunch() {
    this.initStorage()
    this.initLocation()
  },

  initStorage() {
    const token = wx.getStorageSync('token')
    const userInfo = wx.getStorageSync('userInfo')
    const language = wx.getStorageSync('lang') || 'zh'
    if (token) this.globalData.token = token
    if (userInfo) this.globalData.userInfo = userInfo
    this.globalData.lang = language
  },

  initLocation() {
    const self = this
    wx.getSetting({
      success(res) {
        const locationAuth = res.authSetting['scope.userLocation']
        if (locationAuth === false) {
          self.showLocationAuthModal()
        } else if (locationAuth === true) {
          self.doGetLocation()
        } else {
          self.doGetLocation()
        }
      },
      fail() {
        self.doGetLocation()
      }
    })
  },

  doGetLocation() {
    const self = this
    wx.getLocation({
      type: 'gcj02',
      success(res) {
        self.globalData.location = {
          latitude: res.latitude,
          longitude: res.longitude
        }
        console.log('定位成功:', self.globalData.location)
      },
      fail(err) {
        console.error('定位失败:', err)
        if (err.errMsg.indexOf('auth deny') !== -1 || err.errMsg.indexOf('authorize') !== -1) {
          self.showLocationAuthModal()
        } else {
          const lang = self.globalData.lang
          wx.showModal({
            title: lang === 'zh' ? '定位失败' : 'Location Failed',
            content: lang === 'zh' ? '定位失败，请检查设备定位或网络' : 'Location failed, please check device location or network',
            showCancel: false
          })
        }
      }
    })
  },

  showLocationAuthModal() {
    const self = this
    const lang = this.globalData.lang
    wx.showModal({
      title: lang === 'zh' ? '位置权限提示' : 'Location Permission',
      content: lang === 'zh' ? '需要位置权限来计算与周边地点的距离' : 'Location permission is required to calculate distance',
      confirmText: lang === 'zh' ? '去设置' : 'Settings',
      cancelText: lang === 'zh' ? '取消' : 'Cancel',
      success(res) {
        if (res.confirm) {
          wx.openSetting({
            success(settingRes) {
              if (settingRes.authSetting['scope.userLocation']) {
                self.doGetLocation()
              }
            }
          })
        }
      }
    })
  },

  calculateDistance(targetLat, targetLng) {
    return new Promise((resolve, reject) => {
      if (!this.globalData.location) {
        resolve(null)
        return
      }
      const userLat = this.globalData.location.latitude
      const userLng = this.globalData.location.longitude
      const distance = this.getDistanceFromLatLonInKm(userLat, userLng, targetLat, targetLng)
      resolve(distance)
    })
  },

  getDistanceFromLatLonInKm(lat1, lon1, lat2, lon2) {
    const R = 6371
    const dLat = this.deg2rad(lat2 - lat1)
    const dLon = this.deg2rad(lon2 - lon1)
    const a = 
      Math.sin(dLat / 2) * Math.sin(dLat / 2) +
      Math.cos(this.deg2rad(lat1)) * Math.cos(this.deg2rad(lat2)) *
      Math.sin(dLon / 2) * Math.sin(dLon / 2)
    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
    const d = R * c
    return Math.round(d * 10) / 10
  },

  deg2rad(deg) {
    return deg * (Math.PI / 180)
  },

  formatDistance(distance) {
    if (!distance && distance !== 0) return ''
    const lang = this.globalData.lang
    if (distance < 1) {
      const meters = Math.round(distance * 1000)
      return lang === 'zh' ? `${meters}米` : `${meters}m`
    } else {
      return lang === 'zh' ? `${distance}公里` : `${distance}km`
    }
  },

  async calculateDistancesForList(list) {
    if (!this.globalData.location || !list || list.length === 0) {
      return list
    }
    const promises = list.map(async item => {
      if (item.latitude && item.longitude) {
        const distance = await this.calculateDistance(
          parseFloat(item.latitude),
          parseFloat(item.longitude)
        )
        return {
          ...item,
          distance: distance,
          distanceText: distance !== null ? this.formatDistance(distance) : ''
        }
      }
      return item
    })
    return Promise.all(promises)
  },

  setLang(langType) {
    this.globalData.lang = langType
    wx.setStorageSync('lang', langType)
  },

  t(page, key) {
    const pageLang = lang[page] || lang.global
    const langType = this.globalData.lang
    if (pageLang[langType] && pageLang[langType][key]) {
      return pageLang[langType][key]
    }
    if (pageLang['zh'] && pageLang['zh'][key]) {
      return pageLang['zh'][key]
    }
    return key
  },

  isLoggedIn() {
    return !!this.globalData.token
  },

  setUserInfo(userInfo, token) {
    this.globalData.userInfo = userInfo
    this.globalData.token = token
    wx.setStorageSync('userInfo', userInfo)
    wx.setStorageSync('token', token)
  },

  logout() {
    this.globalData.userInfo = null
    this.globalData.token = null
    wx.removeStorageSync('userInfo')
    wx.removeStorageSync('token')
  },

  request(options) {
    const { url, method = 'GET', data = {}, header = {} } = options
    const token = this.globalData.token
    if (token) header['Authorization'] = `Bearer ${token}`
    header['Content-Type'] = 'application/json'

    return new Promise((resolve, reject) => {
      wx.request({
        url: this.getApiUrl(url),
        method,
        data,
        header,
        success: res => {
          if (res.statusCode === 401) {
            this.logout()
            wx.navigateTo({ url: '/pages/login/index' })
            reject(new Error('未登录或登录已过期'))
          } else if (res.statusCode >= 200 && res.statusCode < 300) {
            resolve(res.data)
          } else {
            reject(new Error((res.data && res.data.message) || '请求失败'))
          }
        },
        fail: err => reject(new Error('网络异常'))
      })
    })
  },

  getApiUrl(path) {
    const baseUrl = 'http://localhost:8081/api'
    return baseUrl + path
  },

  checkLogin(callback) {
    if (this.isLoggedIn()) {
      callback && callback()
    } else {
      wx.showModal({
        title: '提示',
        content: '请先登录',
        confirmText: '微信登录',
        cancelText: '账号登录',
        success: res => {
          const url = res.confirm ? '/pages/login/index?type=wechat' : '/pages/login/index?type=account'
          wx.navigateTo({ url })
        }
      })
    }
  },

  showToast(title, icon = 'none', duration = 2000) {
    wx.showToast({ title, icon, duration })
  },

  showLoading(title = '加载中...') {
    wx.showLoading({ title, mask: true })
  },

  hideLoading() {
    wx.hideLoading()
  }
})
