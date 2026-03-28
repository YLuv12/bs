const app = getApp()
const { getImageUrl } = require('../../../utils/request')

Page({
  data: {
    lang: 'zh',
    isLoggedIn: false,
    userInfo: null,
    avatarUrl: '',
    loginType: '',
    menuList: []
  },

  onLoad() { this.initData() },
  
  onShow() { 
    this.initData()
    if (typeof this.getTabBar === 'function' && this.getTabBar()) {
      this.getTabBar().setData({ selected: 4, lang: app.globalData.lang || 'zh' })
    }
  },

  initData() {
    const userInfo = wx.getStorageSync('userInfo')
    const token = wx.getStorageSync('token')
    const isLoggedIn = !!(userInfo && token)
    const lang = app.globalData.lang || 'zh'
    
    const gender = (userInfo && userInfo.gender) || 0
    let genderText = ''
    if (lang === 'zh') {
      genderText = gender === 1 ? '男' : (gender === 2 ? '女' : '未知性别')
    } else {
      genderText = gender === 1 ? 'Male' : (gender === 2 ? 'Female' : 'Unknown')
    }
    
    this.setData({
      lang,
      isLoggedIn,
      userInfo,
      avatarUrl: getImageUrl(userInfo && (userInfo.wx_avatar || userInfo.avatar)) || '',
      loginType: (userInfo && userInfo.login_type) || 2,
      genderText,
      menuList: [
        { icon: '⭐', name: '我的收藏', nameEn: 'Favorites', path: '/pages/my/favorite/favorite', type: 'favorite' },
        { icon: '👣', name: '浏览历史', nameEn: 'History', path: '/pages/my/history/history', type: 'history' },
        { icon: '💬', name: '我的评论', nameEn: 'My Comments', path: '/pages/my/comment/comment', type: 'comment' },
        { icon: '📰', name: '我的资讯', nameEn: 'My News', path: '/pages/my/news/news', type: 'news' }
      ]
    })
    
    wx.setNavigationBarTitle({ title: lang === 'zh' ? '我的' : 'Me' })
  },

  goToLogin() {
    wx.navigateTo({ url: '/pages/login/index' })
  },

  goToProfile() {
    if (!this.data.isLoggedIn) { 
      this.goToLogin()
      return 
    }
    wx.navigateTo({ url: '/pages/my/profile/profile' })
  },

  goToMenu(e) {
    const { path } = e.currentTarget.dataset
    if (!this.data.isLoggedIn) { 
      this.goToLogin()
      return 
    }
    wx.navigateTo({ url: path })
  },

  goToChangePassword() {
    wx.navigateTo({ url: '/pages/my/change-password/change-password' })
  },

  goToFeedback() {
    if (!this.data.isLoggedIn) {
      wx.showToast({ title: this.data.lang === 'zh' ? '请先登录' : 'Please login first', icon: 'none' })
      setTimeout(() => wx.navigateTo({ url: '/pages/login/index' }), 1000)
      return
    }
    wx.navigateTo({ url: '/pages/feedback/index/index' })
  },

  goToSetting() {
    wx.navigateTo({ url: '/pages/my/setting/setting' })
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
          this.initData()
          wx.showToast({ title: this.data.lang === 'zh' ? '已退出登录' : 'Logged out', icon: 'success' })
        }
      }
    })
  },

  onShareAppMessage() {
    return { title: '百色景点美食指南', path: '/pages/index/index' }
  }
})
