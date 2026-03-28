const app = getApp()

Page({
  data: {
    lang: 'zh',
    types: [],
    typeIndex: 0,
    content: '',
    contact: '',
    images: [],
    submitting: false
  },

  onLoad() {
    const lang = app.globalData.lang || 'zh'
    this.setData({
      lang,
      types: [
        { value: 1, label: lang === 'zh' ? '景点添加' : 'Add Attraction' },
        { value: 2, label: lang === 'zh' ? '美食添加' : 'Add Food' },
        { value: 3, label: lang === 'zh' ? 'Bug反馈' : 'Bug Report' },
        { value: 4, label: lang === 'zh' ? '投诉' : 'Complaint' },
        { value: 5, label: lang === 'zh' ? '功能建议' : 'Suggestion' },
        { value: 6, label: lang === 'zh' ? '其他' : 'Other' }
      ]
    })
    wx.setNavigationBarTitle({ title: lang === 'zh' ? '意见反馈' : 'Feedback' })
  },

  onShow() { 
    this.setData({ lang: app.globalData.lang || 'zh' }) 
  },

  onTypeChange(e) { 
    this.setData({ typeIndex: e.detail.value }) 
  },

  onContentInput(e) { 
    this.setData({ content: e.detail.value }) 
  },

  onContactInput(e) { 
    this.setData({ contact: e.detail.value }) 
  },

  chooseImage() {
    const { images } = this.data
    if (images.length >= 3) {
      wx.showToast({ title: this.data.lang === 'zh' ? '最多上传3张图片' : 'Max 3 images', icon: 'none' })
      return
    }
    wx.chooseMedia({
      count: 3 - images.length,
      mediaType: ['image'],
      sourceType: ['album', 'camera'],
      success: res => {
        const newImages = res.tempFiles.map(f => f.tempFilePath)
        this.setData({ images: [...images, ...newImages] })
      }
    })
  },

  previewImage(e) {
    const { index } = e.currentTarget.dataset
    wx.previewImage({ urls: this.data.images, current: this.data.images[index] })
  },

  removeImage(e) {
    const { index } = e.currentTarget.dataset
    const images = this.data.images.filter((_, i) => i !== index)
    this.setData({ images })
  },

  async uploadImage(filePath) {
    const token = wx.getStorageSync('token')
    return new Promise((resolve, reject) => {
      wx.uploadFile({
        url: 'http://localhost:8081/api/upload/image',
        filePath: filePath,
        name: 'file',
        header: { 'Authorization': `Bearer ${token}` },
        success(res) {
          try {
            const data = JSON.parse(res.data)
            if (data.code === 200) {
              resolve(data.data && data.data.url ? data.data.url : data.url)
            } else {
              reject(new Error(data.message || 'upload failed'))
            }
          } catch (e) {
            reject(e)
          }
        },
        fail: reject
      })
    })
  },

  async submit() {
    if (this.data.submitting) return
    
    const { lang, types, typeIndex, content, contact, images } = this.data
    
    if (!content.trim()) {
      wx.showToast({ title: lang === 'zh' ? '请输入反馈内容' : 'Please enter content', icon: 'none' })
      return
    }

    if (contact && contact.trim()) {
      const phoneReg = /^1[3-9]\d{9}$/
      const wechatReg = /^[a-zA-Z][a-zA-Z0-9_-]{5,19}$/
      if (!phoneReg.test(contact.trim()) && !wechatReg.test(contact.trim())) {
        wx.showToast({ title: lang === 'zh' ? '请输入正确的联系方式' : 'Invalid contact format', icon: 'none' })
        return
      }
    }

    const token = wx.getStorageSync('token')
    if (!token) {
      wx.showToast({ title: lang === 'zh' ? '请先登录' : 'Please login first', icon: 'none' })
      setTimeout(() => wx.navigateTo({ url: '/pages/login/index' }), 1000)
      return
    }

    this.setData({ submitting: true })
    wx.showLoading({ title: lang === 'zh' ? '提交中...' : 'Submitting...', mask: true })

    try {
      let uploadedUrls = []
      for (const img of images) {
        const url = await this.uploadImage(img)
        uploadedUrls.push(url)
      }

      const userInfo = wx.getStorageSync('userInfo')
      
      await new Promise((resolve, reject) => {
        wx.request({
          url: 'http://localhost:8081/api/feedback/submit',
          method: 'POST',
          header: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
          },
          data: {
            user_id: userInfo.id,
            type: types[typeIndex].value,
            content: content,
            contact: contact,
            imgs: uploadedUrls.join(',')
          },
          success: (response) => {
            if (response.data && response.data.code === 200) {
              resolve(response.data)
            } else {
              reject(new Error((response.data && response.data.message) || 'submit failed'))
            }
          },
          fail: () => reject(new Error('network error'))
        })
      })

      wx.hideLoading()
      wx.showToast({ title: lang === 'zh' ? '提交成功' : 'Submitted', icon: 'success' })
      setTimeout(() => wx.navigateBack(), 1500)

    } catch (e) {
      wx.hideLoading()
      wx.showToast({ title: e.message || (lang === 'zh' ? '提交失败' : 'Failed'), icon: 'none' })
    } finally {
      this.setData({ submitting: false })
    }
  }
})
