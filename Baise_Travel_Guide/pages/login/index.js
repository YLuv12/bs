const app = getApp()

Page({
  data: {
    lang: 'zh',
    loginType: 'wechat',
    phone: '',
    password: '',
    confirmPassword: '',
    showPassword: false,
    showConfirmPassword: false,
    agreed: false,
    pwdStrength: '',
    pwdStrengthText: '',
    pwdStrengthColor: '',
    loading: false,
    submitting: false,
    isRegister: false,
    phoneError: '',
    passwordError: '',
    confirmError: '',
    showNicknamePicker: false,
    selectedAvatar: '',
    selectedNickname: '',
    tempAvatar: ''
  },

  onLoad(options) {
    const lang = app.globalData.lang || 'zh'
    this.setData({ lang })
    const { type } = options
    if (type === 'account') {
      this.setData({ loginType: 'account' })
    }
  },

  onShow() {
    const lang = app.globalData.lang || 'zh'
    this.setData({ lang })
  },

  switchType(e) {
    const { type } = e.currentTarget.dataset
    this.setData({ 
      loginType: type, 
      isRegister: false,
      phone: '',
      password: '',
      confirmPassword: '',
      phoneError: '',
      passwordError: '',
      confirmError: ''
    })
  },

  toggleRegister() {
    this.setData({ 
      isRegister: !this.data.isRegister,
      phone: '',
      password: '',
      confirmPassword: '',
      phoneError: '',
      passwordError: '',
      confirmError: ''
    })
    wx.setNavigationBarTitle({ 
      title: this.data.isRegister ? '注册账号' : '登录' 
    })
  },

  toggleShowPassword() {
    this.setData({ showPassword: !this.data.showPassword })
  },

  toggleShowConfirmPassword() {
    this.setData({ showConfirmPassword: !this.data.showConfirmPassword })
  },

  toggleAgree() {
    this.setData({ agreed: !this.data.agreed })
  },

  onPhoneInput(e) {
    const phone = e.detail.value
    this.setData({ phone, phoneError: '' })
  },

  onPasswordInput(e) {
    const password = e.detail.value
    this.setData({ password })
    this.checkPwdStrength(password)
    this.validatePasswordRealtime(password)
  },

  checkPwdStrength(pwd) {
    if (!pwd) {
      this.setData({ pwdStrength: '', pwdStrengthText: '', pwdStrengthColor: '' })
      return
    }
    let strength = 0
    if (pwd.length >= 8) strength++
    if (/[a-z]/.test(pwd)) strength++
    if (/[A-Z]/.test(pwd)) strength++
    if (/[0-9]/.test(pwd)) strength++
    
    let level, text, color
    if (strength <= 2) {
      level = 'weak'; text = '弱'; color = '#FF4D4F'
    } else if (strength === 3) {
      level = 'middle'; text = '中'; color = '#FAAD14'
    } else {
      level = 'strong'; text = '强'; color = '#52C41A'
    }
    this.setData({ pwdStrength: level, pwdStrengthText: text, pwdStrengthColor: color })
  },

  validatePasswordRealtime(pwd) {
    if (!pwd) { this.setData({ passwordError: '' }); return }
    let error = ''
    if (pwd.length < 8) error = '密码长度不足8位'
    else if (pwd.length > 16) error = '密码长度超过16位'
    else if (!/[a-z]/.test(pwd)) error = '密码需包含小写字母'
    else if (!/[A-Z]/.test(pwd)) error = '密码需包含大写字母'
    else if (!/[0-9]/.test(pwd)) error = '密码需包含数字'
    this.setData({ passwordError: error })
  },

  onConfirmPasswordInput(e) {
    const confirmPassword = e.detail.value
    this.setData({ confirmPassword })
    if (confirmPassword && confirmPassword !== this.data.password) {
      this.setData({ confirmError: '两次密码输入不一致' })
    } else {
      this.setData({ confirmError: '' })
    }
  },

  validatePassword(pwd) {
    if (pwd.length < 8 || pwd.length > 16) return false
    if (!/[a-z]/.test(pwd)) return false
    if (!/[A-Z]/.test(pwd)) return false
    if (!/[0-9]/.test(pwd)) return false
    return true
  },

  // ========== 新版微信登录（头像昵称填写能力）==========
  
  // 微信登录按钮点击（未同意协议时）
  onWechatLoginTap() {
    if (!this.data.agreed) {
      wx.showToast({ title: '请先同意用户协议和隐私政策', icon: 'none' })
      return
    }
  },
  
  // 选择头像 - 使用新版API
  onChooseAvatar(e) {
    
    const { avatarUrl } = e.detail
    console.log('选择的头像:', avatarUrl)
    
    this.setData({ 
      selectedAvatar: avatarUrl,
      tempAvatar: avatarUrl
    })
    
    // 显示昵称选择弹窗
    this.showNicknamePicker()
  },

  // 显示昵称选择弹窗
  showNicknamePicker() {
    this.setData({ showNicknamePicker: true })
  },

  // 隐藏昵称选择弹窗
  hideNicknamePicker() {
    this.setData({ showNicknamePicker: false })
  },

  // 昵称输入
  onNicknameInput(e) {
    this.setData({ selectedNickname: e.detail.value })
  },

  // 确认昵称并提交登录
  async confirmNickname() {
    if (this.data.submitting) return
    
    const { selectedNickname, selectedAvatar, tempAvatar } = this.data
    
    if (!selectedNickname || selectedNickname.trim() === '') {
      wx.showToast({ title: '请输入昵称', icon: 'none' })
      return
    }
    
    if (!selectedAvatar) {
      wx.showToast({ title: '请先选择头像', icon: 'none' })
      return
    }
    
    this.setData({ loading: true, showNicknamePicker: false, submitting: true })
    
    try {
      let avatarUrl = selectedAvatar
      
      // 如果是本地临时文件，先上传
      if (selectedAvatar.startsWith('http://tmp/') || selectedAvatar.startsWith('wxfile://')) {
        avatarUrl = await this.uploadAvatar(selectedAvatar)
      }
      
      // 获取微信登录code
      const loginRes = await new Promise((resolve, reject) => {
        wx.login({
          success: resolve,
          fail: reject
        })
      })
      
      console.log('提交登录数据:', { 
        code: loginRes.code, 
        nickName: selectedNickname.trim(), 
        avatarUrl: avatarUrl 
      })
      
      // 提交到后端接口
      const res = await new Promise((resolve, reject) => {
        wx.request({
          url: 'http://localhost:8081/api/auth/wechat',
          method: 'POST',
          data: {
            code: loginRes.code,
            nickName: selectedNickname.trim(),
            avatarUrl: avatarUrl
          },
          header: { 'Content-Type': 'application/json' },
          success: (response) => {
            console.log('后端响应:', response.data)
            if (response.data && response.data.code === 200) {
              resolve(response.data)
            } else {
              reject(new Error((response.data && response.data.message) || '登录失败'))
            }
          },
          fail: (err) => reject(new Error('网络请求失败'))
        })
      })
      
      console.log('登录成功:', res)
      
      // 存储token和用户信息到本地
      wx.setStorageSync('token', res.token)
      wx.setStorageSync('userInfo', res.user)
      
      // 同步全局状态
      app.globalData.token = res.token
      app.globalData.userInfo = res.user
      
      wx.showToast({ title: '登录成功', icon: 'success' })
      
      // 跳转首页
      setTimeout(() => {
        wx.switchTab({ url: '/pages/index/index' })
      }, 1000)
    } catch (e) {
      console.error('登录失败:', e)
      wx.showToast({ title: e.message || '登录失败', icon: 'none' })
    } finally {
      this.setData({ loading: false, submitting: false })
    }
  },

  // 上传头像到服务器
  uploadAvatar(filePath) {
    return new Promise((resolve, reject) => {
      wx.uploadFile({
        url: 'http://localhost:8081/api/upload/avatar',
        filePath: filePath,
        name: 'file',
        success(res) {
          try {
            const data = JSON.parse(res.data)
            if (data.code === 200 && data.data && data.data.url) {
              resolve(data.data.url)
            } else {
              reject(new Error(data.message || '上传失败'))
            }
          } catch (e) {
            reject(e)
          }
        },
        fail: reject
      })
    })
  },

  // ========== 账号登录/注册 ==========
  
  async accountLogin() {
    if (this.data.submitting) return
    if (!this.data.agreed) {
      wx.showToast({ title: '请先同意用户协议和隐私政策', icon: 'none' })
      return
    }
    if (!this.data.phone) {
      this.setData({ phoneError: '请输入手机号' })
      return
    }
    if (!/^1[3-9]\d{9}$/.test(this.data.phone)) {
      this.setData({ phoneError: '手机号格式不正确' })
      return
    }
    if (!this.data.password) {
      this.setData({ passwordError: '请输入密码' })
      return
    }
    
    this.setData({ loading: true, submitting: true })
    
    try {
      const res = await new Promise((resolve, reject) => {
        wx.request({
          url: 'http://localhost:8081/api/auth/login',
          method: 'POST',
          data: {
            phone: this.data.phone,
            password: this.data.password
          },
          header: { 'Content-Type': 'application/json' },
          success: (response) => {
            if (response.data && response.data.code === 200) {
              resolve(response.data)
            } else {
              reject(new Error((response.data && response.data.message) || '登录失败'))
            }
          },
          fail: () => reject(new Error('网络请求失败'))
        })
      })
      
      wx.setStorageSync('token', res.token)
      wx.setStorageSync('userInfo', res.user)
      app.globalData.token = res.token
      app.globalData.userInfo = res.user
      
      wx.showToast({ title: '登录成功', icon: 'success' })
      
      setTimeout(() => {
        wx.switchTab({ url: '/pages/index/index' })
      }, 1000)
    } catch (e) {
      wx.showToast({ title: e.message || '登录失败', icon: 'none' })
    } finally {
      this.setData({ loading: false, submitting: false })
    }
  },

  async register() {
    if (this.data.submitting) return
    if (!this.data.agreed) {
      wx.showToast({ title: '请先同意用户协议和隐私政策', icon: 'none' })
      return
    }
    if (!this.data.phone) {
      this.setData({ phoneError: '请输入手机号' })
      return
    }
    if (!/^1[3-9]\d{9}$/.test(this.data.phone)) {
      this.setData({ phoneError: '手机号格式不正确' })
      return
    }
    if (!this.validatePassword(this.data.password)) {
      this.setData({ passwordError: '密码需8-16位，包含大小写字母和数字' })
      return
    }
    if (this.data.password !== this.data.confirmPassword) {
      this.setData({ confirmError: '两次密码输入不一致' })
      return
    }
    
    this.setData({ loading: true, submitting: true })
    
    try {
      const res = await new Promise((resolve, reject) => {
        wx.request({
          url: 'http://localhost:8081/api/auth/register',
          method: 'POST',
          data: {
            phone: this.data.phone,
            password: this.data.password
          },
          header: { 'Content-Type': 'application/json' },
          success: (response) => {
            if (response.data && response.data.code === 200) {
              resolve(response.data)
            } else {
              reject(new Error((response.data && response.data.message) || '注册失败'))
            }
          },
          fail: () => reject(new Error('网络请求失败'))
        })
      })
      
      wx.setStorageSync('token', res.token)
      wx.setStorageSync('userInfo', res.user)
      app.globalData.token = res.token
      app.globalData.userInfo = res.user
      
      wx.showToast({ title: '注册成功', icon: 'success' })
      
      setTimeout(() => {
        wx.switchTab({ url: '/pages/index/index' })
      }, 1000)
    } catch (e) {
      wx.showToast({ title: e.message || '注册失败', icon: 'none' })
    } finally {
      this.setData({ loading: false, submitting: false })
    }
  },

  goToUserProtocol() {
    wx.navigateTo({ url: '/pages/webview/index?type=userProtocol' })
  },

  goToPrivacyPolicy() {
    wx.navigateTo({ url: '/pages/webview/index?type=privacyPolicy' })
  }
})
