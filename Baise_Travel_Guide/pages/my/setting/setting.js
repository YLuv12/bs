const app = getApp()

Page({
  data: {
    lang: 'zh',
    cacheSize: '0KB',
    isLoggedIn: false
  },

  onLoad() { 
    this.initData()
    this.getCacheSize()
  },
  
  onShow() { 
    this.initData()
  },

  initData() {
    const userInfo = wx.getStorageSync('userInfo')
    const token = wx.getStorageSync('token')
    const lang = app.globalData.lang || 'zh'
    this.setData({ 
      lang,
      isLoggedIn: !!(userInfo && token)
    })
    wx.setNavigationBarTitle({ title: lang === 'zh' ? '设置' : 'Settings' })
  },

  getCacheSize() {
    wx.getStorageInfo({
      success: res => {
        const size = res.currentSize
        this.setData({ cacheSize: size < 1024 ? `${size}KB` : `${(size / 1024).toFixed(2)}MB` })
      }
    })
  },

  switchLang() {
    const newLang = this.data.lang === 'zh' ? 'en' : 'zh'
    app.globalData.lang = newLang
    wx.setStorageSync('lang', newLang)
    this.setData({ lang: newLang })
    wx.setNavigationBarTitle({ title: newLang === 'zh' ? '设置' : 'Settings' })
    wx.showToast({ title: newLang === 'zh' ? '已切换为中文' : 'Switched to English', icon: 'none' })
  },

  goToFeedbackRecord() {
    wx.navigateTo({ url: '/pages/my/feedback/record' })
  },

  clearCache() {
    wx.showModal({
      title: this.data.lang === 'zh' ? '提示' : 'Tip',
      content: this.data.lang === 'zh' ? '确定清除缓存？' : 'Clear cache?',
      confirmText: this.data.lang === 'zh' ? '确定' : 'OK',
      cancelText: this.data.lang === 'zh' ? '取消' : 'Cancel',
      success: res => {
        if (res.confirm) {
          const userInfo = wx.getStorageSync('userInfo')
          const token = wx.getStorageSync('token')
          
          wx.clearStorage({
            success: () => {
              if (userInfo && token) {
                wx.setStorageSync('userInfo', userInfo)
                wx.setStorageSync('token', token)
              }
              wx.setStorageSync('lang', this.data.lang)
              this.getCacheSize()
              wx.showToast({ title: this.data.lang === 'zh' ? '缓存已清空' : 'Cache cleared', icon: 'none' })
            }
          })
        }
      }
    })
  },

  goToUserProtocol() {
    wx.navigateTo({ url: '/pages/webview/index?type=userProtocol' })
  },

  goToPrivacyPolicy() {
    wx.navigateTo({ url: '/pages/webview/index?type=privacyPolicy' })
  },

  logout() {
    wx.showModal({
      title: this.data.lang === 'zh' ? '提示' : 'Tip',
      content: this.data.lang === 'zh' ? '确定退出登录吗？' : 'Confirm logout?',
      confirmText: this.data.lang === 'zh' ? '确定' : 'OK',
      cancelText: this.data.lang === 'zh' ? '取消' : 'Cancel',
      success: res => {
        if (res.confirm) {
          wx.removeStorageSync('token')
          wx.removeStorageSync('userInfo')
          wx.switchTab({ url: '/pages/index/index' })
        }
      }
    })
  }
})
