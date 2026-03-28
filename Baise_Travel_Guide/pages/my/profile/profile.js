const app = getApp()
const { getImageUrl } = require('../../../utils/request')

Page({
  data: {
    lang: 'zh',
    loading: true,
    userInfo: null,
    avatar: '',
    nickname: '',
    gender: 0,
    genderIndex: 0,
    age: '',
    ageIndex: 0,
    phone: '',
    intro: '',
    genderOptions: [
      { value: 0, label: '未知', labelEn: 'Unknown' },
      { value: 1, label: '男', labelEn: 'Male' },
      { value: 2, label: '女', labelEn: 'Female' }
    ],
    ageOptions: []
  },

  onLoad() {
    const lang = app.globalData.lang || 'zh'
    const ageOptions = []
    for (let i = 0; i <= 100; i++) {
      ageOptions.push(i)
    }
    this.setData({ lang, ageOptions })
    this.loadProfile()
  },

  onShow() {
    this.setData({ lang: app.globalData.lang || 'zh' })
  },

  async loadProfile() {
    const userInfo = wx.getStorageSync('userInfo')
    const token = wx.getStorageSync('token')
    
    if (!userInfo || !token) {
      wx.showToast({ title: '请先登录', icon: 'none' })
      setTimeout(() => wx.navigateTo({ url: '/pages/login/index' }), 1000)
      return
    }

    this.setData({ loading: true })

    try {
      const res = await new Promise((resolve, reject) => {
        wx.request({
          url: 'http://localhost:8081/api/user/profile',
          method: 'GET',
          header: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
          },
          success: (response) => {
            if (response.data && response.data.code === 200) {
              resolve(response.data)
            } else {
              reject(new Error((response.data && response.data.message) || '获取资料失败'))
            }
          },
          fail: () => reject(new Error('网络请求失败'))
        })
      })

      const user = res.user || res.data || userInfo
      const genderIndex = user.gender || 0
      const ageValue = user.age || 0
      const ageIndex = Math.min(Math.max(ageValue, 0), 100)
      
      this.setData({
        userInfo: user,
        avatar: getImageUrl(user.wx_avatar || user.avatar) || '',
        nickname: user.wx_nickname || user.nickname || '',
        gender: genderIndex,
        genderIndex: genderIndex,
        age: ageValue,
        ageIndex: ageIndex,
        phone: user.phone || '',
        intro: user.intro || '',
        loading: false
      })

      console.log('加载用户资料:', user)
    } catch (e) {
      console.error('获取资料失败:', e)
      const cachedUser = wx.getStorageSync('userInfo') || {}
      const genderIndex = cachedUser.gender || 0
      const ageValue = cachedUser.age || 0
      const ageIndex = Math.min(Math.max(ageValue, 0), 100)
      
      this.setData({
        userInfo: cachedUser,
        avatar: getImageUrl(cachedUser.wx_avatar) || '',
        nickname: cachedUser.wx_nickname || '',
        gender: genderIndex,
        genderIndex: genderIndex,
        age: ageValue,
        ageIndex: ageIndex,
        phone: cachedUser.phone || '',
        intro: cachedUser.intro || '',
        loading: false
      })
      wx.showToast({ title: '使用本地缓存数据', icon: 'none' })
    }
  },

  chooseAvatar() {
    const self = this
    wx.showActionSheet({
      itemList: ['从相册选择', '拍照'],
      success(res) {
        const sourceType = res.tapIndex === 0 ? ['album'] : ['camera']
        wx.chooseMedia({
          count: 1,
          mediaType: ['image'],
          sourceType: sourceType,
          success(res) {
            self.setData({ avatar: res.tempFiles[0].tempFilePath })
          }
        })
      }
    })
  },

  onNicknameInput(e) {
    this.setData({ nickname: e.detail.value })
  },

  onGenderChange(e) {
    const index = parseInt(e.detail.value)
    this.setData({ 
      genderIndex: index,
      gender: this.data.genderOptions[index].value
    })
  },

  onAgeChange(e) {
    const index = parseInt(e.detail.value)
    this.setData({ 
      ageIndex: index,
      age: this.data.ageOptions[index]
    })
  },

  onPhoneInput(e) {
    this.setData({ phone: e.detail.value })
  },

  onIntroInput(e) {
    this.setData({ intro: e.detail.value })
  },

  validatePhone(phone) {
    if (!phone) return true
    return /^1[3-9]\d{9}$/.test(phone)
  },

  async save() {
    const { nickname, phone, avatar, gender, age, intro } = this.data
    const lang = this.data.lang

    if (!nickname || nickname.trim() === '') {
      wx.showToast({ title: lang === 'zh' ? '请输入昵称' : 'Please enter nickname', icon: 'none' })
      return
    }

    if (phone && !this.validatePhone(phone)) {
      wx.showToast({ title: lang === 'zh' ? '手机号格式不正确' : 'Invalid phone format', icon: 'none' })
      return
    }

    this.setData({ loading: true })

    try {
      let avatarUrl = avatar
      if (avatar && (avatar.startsWith('http://tmp/') || avatar.startsWith('wxfile://'))) {
        avatarUrl = await this.uploadAvatar(avatar)
      }

      const token = wx.getStorageSync('token')
      const res = await new Promise((resolve, reject) => {
        wx.request({
          url: 'http://localhost:8081/api/user/update',
          method: 'POST',
          header: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
          },
          data: {
            wx_avatar: avatarUrl,
            wx_nickname: nickname.trim(),
            gender: gender,
            age: age,
            phone: phone,
            intro: intro
          },
          success: (response) => {
            if (response.data && response.data.code === 200) {
              resolve(response.data)
            } else {
              reject(new Error((response.data && response.data.message) || '保存失败'))
            }
          },
          fail: () => reject(new Error('网络请求失败'))
        })
      })

      const updatedUser = res.user || {
        ...this.data.userInfo,
        wx_avatar: avatarUrl,
        wx_nickname: nickname.trim(),
        gender: gender,
        age: age,
        phone: phone,
        intro: intro
      }

      wx.setStorageSync('userInfo', updatedUser)
      app.globalData.userInfo = updatedUser

      wx.showToast({ title: lang === 'zh' ? '保存成功' : 'Saved', icon: 'success' })

      setTimeout(() => wx.navigateBack(), 1000)
    } catch (e) {
      console.error('保存失败:', e)
      wx.showToast({ title: e.message || '保存失败，请重试', icon: 'none' })
    } finally {
      this.setData({ loading: false })
    }
  },

  uploadAvatar(filePath) {
    const token = wx.getStorageSync('token')
    return new Promise((resolve) => {
      wx.uploadFile({
        url: 'http://localhost:8081/api/upload/avatar',
        filePath: filePath,
        name: 'file',
        header: { 'Authorization': `Bearer ${token}` },
        success(res) {
          try {
            const data = JSON.parse(res.data)
            if (data.code === 200 && data.data && data.data.url) {
              resolve(data.data.url)
            } else {
              resolve(filePath)
            }
          } catch (e) {
            resolve(filePath)
          }
        },
        fail() {
          resolve(filePath)
        }
      })
    })
  }
})
