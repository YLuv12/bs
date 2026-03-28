const app = getApp()

Page({
  data: {
    lang: 'zh',
    type: '',
    id: '',
    rating: 0,
    content: '',
    images: [],
    tags: [],
    availableTags: [],
    submitting: false
  },

  onLoad(options) {
    const { type, id } = options
    this.setData({
      lang: app.globalData.lang,
      type,
      id,
      availableTags: type === 'attraction' ? [
        { value: 'worth', label: app.t('comment', 'tagWorth') },
        { value: 'beautiful', label: app.t('comment', 'tagBeautiful') },
        { value: 'guide', label: app.t('comment', 'tagGuide') }
      ] : [
        { value: 'delicious', label: app.t('comment', 'tagDelicious') },
        { value: 'affordable', label: app.t('comment', 'tagAffordable') },
        { value: 'clean', label: app.t('comment', 'tagClean') }
      ]
    })
    wx.setNavigationBarTitle({ title: app.t('comment', 'title') })
  },

  setRating(e) { this.setData({ rating: e.currentTarget.dataset.rating }) },

  onContentInput(e) { this.setData({ content: e.detail.value }) },

  toggleTag(e) {
    const { value } = e.currentTarget.dataset
    let tags = [...this.data.tags]
    const index = tags.indexOf(value)
    if (index > -1) {
      tags.splice(index, 1)
    } else if (tags.length < 3) {
      tags.push(value)
    }
    this.setData({ tags })
  },

  chooseImage() {
    if (this.data.images.length >= 3) {
      app.showToast(app.t('comment', 'maxImages'))
      return
    }
    wx.chooseMedia({
      count: 3 - this.data.images.length,
      mediaType: ['image'],
      success: res => {
        const newImages = res.tempFiles.map(f => f.tempFilePath)
        this.setData({ images: [...this.data.images, ...newImages] })
      }
    })
  },

  removeImage(e) {
    const images = this.data.images.filter((_, i) => i !== e.currentTarget.dataset.index)
    this.setData({ images })
  },

  async submit() {
    if (this.data.rating === 0) {
      app.showToast(app.t('comment', 'ratingRequired'))
      return
    }
    if (!this.data.content.trim()) {
      app.showToast(app.t('comment', 'contentRequired'))
      return
    }

    this.setData({ submitting: true })
    try {
      await new Promise(resolve => setTimeout(resolve, 1000))
      app.showToast(app.t('comment', 'submitSuccess'))
      setTimeout(() => wx.navigateBack(), 1500)
    } catch (e) {
      app.showToast(app.t('comment', 'submitFailed'))
    } finally {
      this.setData({ submitting: false })
    }
  }
})
