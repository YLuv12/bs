const app = getApp()

Page({
  data: {
    lang: 'zh',
    title: '',
    content: '',
    coverImg: '',
    coverImgUrl: '',
    tags: '',
    contentImages: [],
    submitting: false,
    titleMax: 200,
    titleMaxEn: 400
  },

  onLoad() {
    const lang = app.globalData.lang || 'zh'
    this.setData({ lang })
    wx.setNavigationBarTitle({ title: lang === 'zh' ? '发布资讯' : 'Publish News' })
  },

  onTitleInput(e) {
    const title = e.detail.value
    const maxLen = this.data.lang === 'zh' ? this.data.titleMax : this.data.titleMaxEn
    if (title.length > maxLen) {
      wx.showToast({ 
        title: this.data.lang === 'zh' ? `标题不能超过${maxLen}字` : `Title cannot exceed ${maxLen} characters`, 
        icon: 'none' 
      })
      return
    }
    this.setData({ title })
  },

  onContentInput(e) {
    this.setData({ content: e.detail.value })
  },

  onTagsInput(e) {
    this.setData({ tags: e.detail.value })
  },

  chooseCover() {
    const self = this
    wx.chooseMedia({
      count: 1,
      mediaType: ['image'],
      sizeType: ['compressed'],
      success(res) {
        const file = res.tempFiles[0]
        if (file.size > 2 * 1024 * 1024) {
          wx.showToast({ 
            title: self.data.lang === 'zh' ? '图片不能超过2M' : 'Image cannot exceed 2MB', 
            icon: 'none' 
          })
          return
        }
        self.setData({ coverImg: file.tempFilePath })
      }
    })
  },

  removeCover() {
    this.setData({ coverImg: '', coverImgUrl: '' })
  },

  previewCover() {
    wx.previewImage({ urls: [this.data.coverImg] })
  },

  chooseContentImage() {
    const self = this
    wx.chooseMedia({
      count: 9 - this.data.contentImages.length,
      mediaType: ['image'],
      sizeType: ['compressed'],
      success(res) {
        const files = res.tempFiles
        const newImages = []
        for (const file of files) {
          if (file.size > 2 * 1024 * 1024) {
            wx.showToast({ 
              title: self.data.lang === 'zh' ? '图片不能超过2M' : 'Image cannot exceed 2MB', 
              icon: 'none' 
            })
            continue
          }
          newImages.push(file.tempFilePath)
        }
        self.setData({ contentImages: [...self.data.contentImages, ...newImages] })
      }
    })
  },

  removeContentImage(e) {
    const index = e.currentTarget.dataset.index
    const images = [...this.data.contentImages]
    images.splice(index, 1)
    this.setData({ contentImages: images })
  },

  previewContentImage(e) {
    const index = e.currentTarget.dataset.index
    wx.previewImage({ 
      urls: this.data.contentImages, 
      current: this.data.contentImages[index] 
    })
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
          console.log('上传响应:', res.data)
          try {
            const data = JSON.parse(res.data)
            if (data.code === 200) {
              const url = data.data && data.data.url ? data.data.url : data.url
              if (url) {
                resolve(url)
              } else {
                reject(new Error('upload failed: no url'))
              }
            } else {
              reject(new Error(data.message || 'upload failed'))
            }
          } catch (e) {
            console.error('解析上传响应失败:', e)
            reject(new Error('parse response failed'))
          }
        },
        fail(err) {
          console.error('上传请求失败:', err)
          reject(new Error('upload request failed'))
        }
      })
    })
  },

  async uploadWithRetry(filePath, retries) {
    for (let i = 0; i < retries; i++) {
      try {
        return await this.uploadImage(filePath)
      } catch (e) {
        if (i === retries - 1) throw e
        await new Promise(resolve => setTimeout(resolve, 1000))
      }
    }
  },

  async submit() {
    if (this.data.submitting) return
    
    const { lang, title, content, coverImg, tags, contentImages } = this.data
    
    if (!title.trim()) {
      wx.showToast({ title: lang === 'zh' ? '请输入标题' : 'Please enter title', icon: 'none' })
      return
    }
    if (!content.trim() && contentImages.length === 0) {
      wx.showToast({ title: lang === 'zh' ? '请输入内容' : 'Please enter content', icon: 'none' })
      return
    }
    if (!coverImg) {
      wx.showToast({ title: lang === 'zh' ? '请选择封面图片' : 'Please select cover image', icon: 'none' })
      return
    }

    this.setData({ submitting: true })
    wx.showLoading({ title: lang === 'zh' ? '发布中...' : 'Publishing...', mask: true })

    try {
      const coverImgUrl = await this.uploadWithRetry(coverImg, 2)
      
      const uploadedImages = []
      for (const img of contentImages) {
        const url = await this.uploadWithRetry(img, 2)
        uploadedImages.push(url)
      }

      let fullContent = content
      for (const url of uploadedImages) {
        fullContent += `<image src="${url}" />`
      }

      const token = wx.getStorageSync('token')
      const userInfo = wx.getStorageSync('userInfo')
      
      console.log('当前token:', token)
      console.log('当前userInfo:', userInfo)
      
      if (!token) {
        wx.showToast({ title: lang === 'zh' ? '请先登录' : 'Please login first', icon: 'none' })
        setTimeout(() => wx.navigateTo({ url: '/pages/login/index' }), 1000)
        return
      }
      
      const res = await new Promise((resolve, reject) => {
        wx.request({
          url: 'http://localhost:8081/api/news/publish',
          method: 'POST',
          header: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
          },
          data: {
            title_cn: lang === 'zh' ? title : '',
            title_en: lang === 'en' ? title : '',
            content_cn: lang === 'zh' ? fullContent : '',
            content_en: lang === 'en' ? fullContent : '',
            cover_img: coverImgUrl,
            author_id: userInfo.id,
            author_name: userInfo.wx_nickname || userInfo.nickname || (lang === 'zh' ? '用户' : 'User'),
            tags: tags
          },
          success: (response) => {
            if (response.data && response.data.code === 200) {
              resolve(response.data)
            } else {
              reject(new Error((response.data && response.data.message) || 'publish failed'))
            }
          },
          fail: () => reject(new Error('network error'))
        })
      })

      wx.hideLoading()
      wx.showToast({ title: lang === 'zh' ? '发布成功' : 'Published', icon: 'success' })
      setTimeout(() => wx.navigateBack(), 1500)

    } catch (e) {
      wx.hideLoading()
      console.error('发布失败详情:', e)
      wx.showToast({ 
        title: e.message || (lang === 'zh' ? '发布失败，请重试' : 'Failed, please retry'), 
        icon: 'none',
        duration: 3000
      })
    } finally {
      this.setData({ submitting: false })
    }
  }
})
