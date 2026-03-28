const app = getApp()
const { post } = require('../../../utils/request.js')

Page({
  data: {
    lang: 'zh',
    oldPassword: '',
    newPassword: '',
    confirmPassword: '',
    showOldPassword: false,
    showNewPassword: false,
    showConfirmPassword: false,
    loading: false
  },

  onLoad() {
    const lang = app.globalData.lang || 'zh'
    this.setData({ lang })
    wx.setNavigationBarTitle({ title: lang === 'zh' ? '修改密码' : 'Change Password' })
  },

  onShow() {
    const lang = app.globalData.lang || 'zh'
    this.setData({ lang })
    wx.setNavigationBarTitle({ title: lang === 'zh' ? '修改密码' : 'Change Password' })
  },

  toggleShowOldPassword() {
    this.setData({ showOldPassword: !this.data.showOldPassword })
  },

  toggleShowNewPassword() {
    this.setData({ showNewPassword: !this.data.showNewPassword })
  },

  toggleShowConfirmPassword() {
    this.setData({ showConfirmPassword: !this.data.showConfirmPassword })
  },

  onOldPasswordInput(e) {
    this.setData({ oldPassword: e.detail.value })
  },

  onNewPasswordInput(e) {
    this.setData({ newPassword: e.detail.value })
  },

  onConfirmPasswordInput(e) {
    this.setData({ confirmPassword: e.detail.value })
  },

  async submit() {
    const { lang } = this.data
    
    if (!this.data.oldPassword) {
      wx.showToast({ title: lang === 'zh' ? '请输入原密码' : 'Enter current password', icon: 'none' })
      return
    }
    
    if (!this.data.newPassword) {
      wx.showToast({ title: lang === 'zh' ? '请输入新密码' : 'Enter new password', icon: 'none' })
      return
    }
    
    if (this.data.newPassword !== this.data.confirmPassword) {
      wx.showToast({ title: lang === 'zh' ? '两次密码输入不一致' : 'Passwords do not match', icon: 'none' })
      return
    }
    
    const userInfo = wx.getStorageSync('userInfo')
    if (!userInfo || !userInfo.id) {
      wx.showToast({ title: lang === 'zh' ? '请先登录' : 'Please login first', icon: 'none' })
      return
    }
    
    this.setData({ loading: true })
    
    try {
      await post('/user/change-password', {
        userId: userInfo.id,
        oldPassword: this.data.oldPassword,
        newPassword: this.data.newPassword
      })
      
      wx.showToast({ title: lang === 'zh' ? '修改成功' : 'Success', icon: 'success' })
      
      setTimeout(() => {
        wx.navigateBack()
      }, 1500)
    } catch (e) {
      wx.showToast({ title: e.message || (lang === 'zh' ? '修改失败' : 'Failed'), icon: 'none' })
    } finally {
      this.setData({ loading: false })
    }
  }
})
