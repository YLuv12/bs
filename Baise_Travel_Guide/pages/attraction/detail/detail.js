const app = getApp()
const { get, getImageUrl } = require('../../../utils/request')

Page({
  data: {
    lang: 'zh',
    id: null,
    detail: null,
    loading: true,
    isFavorite: false,
    isPunched: false,
    isLiked: false,
    userRating: 0,
    comments: [],
    showShare: false,
    showBackTop: false
  },

  onLoad(options) {
    const { id } = options
    this.setData({ id, lang: app.globalData.lang || 'zh' })
    this.loadDetail()
  },

  onShow() {
    this.setData({ lang: app.globalData.lang || 'zh' })
  },

  async loadDetail() {
    this.setData({ loading: true })
    try {
      const { id } = this.data
      const detailData = await get(`/scenic-spot/detail/${id}`)
      
      const images = detailData.imgs ? detailData.imgs.split(',').map(img => getImageUrl(img)) : [getImageUrl(detailData.cover_img)]
      
      const detail = {
        ...detailData,
        cover_img: getImageUrl(detailData.cover_img),
        images: images.filter(Boolean)
      }
      
      this.setData({ detail, loading: false })
    } catch (error) {
      console.error('加载景点详情失败:', error)
      this.setData({ loading: false })
      wx.showToast({ title: this.data.lang === 'zh' ? '加载失败' : 'Load failed', icon: 'none' })
    }
  },

  onPageScroll(e) {
    const showBackTop = e.scrollTop > 300
    if (showBackTop !== this.data.showBackTop) {
      this.setData({ showBackTop })
    }
  },

  goToTop() {
    wx.pageScrollTo({ scrollTop: 0, duration: 300 })
  },

  toggleFavorite() {
    const userInfo = wx.getStorageSync('userInfo')
    if (!userInfo) {
      wx.showToast({ title: this.data.lang === 'zh' ? '请先登录' : 'Please login first', icon: 'none' })
      return
    }
    const isFavorite = !this.data.isFavorite
    this.setData({ isFavorite })
    wx.showToast({ title: isFavorite ? (this.data.lang === 'zh' ? '收藏成功' : 'Added to favorites') : (this.data.lang === 'zh' ? '已取消收藏' : 'Removed from favorites'), icon: 'none' })
  },

  togglePunch() {
    const userInfo = wx.getStorageSync('userInfo')
    if (!userInfo) {
      wx.showToast({ title: this.data.lang === 'zh' ? '请先登录' : 'Please login first', icon: 'none' })
      return
    }
    if (this.data.isPunched) return
    this.setData({ isPunched: true })
    wx.showToast({ title: this.data.lang === 'zh' ? '打卡成功' : 'Checked in successfully', icon: 'success' })
  },

  toggleLike() {
    const isLiked = !this.data.isLiked
    this.setData({ isLiked })
  },

  setRating(e) {
    const { rating } = e.currentTarget.dataset
    const userInfo = wx.getStorageSync('userInfo')
    if (!userInfo) {
      wx.showToast({ title: this.data.lang === 'zh' ? '请先登录' : 'Please login first', icon: 'none' })
      return
    }
    this.setData({ userRating: parseInt(rating) })
    wx.showToast({ title: this.data.lang === 'zh' ? '评分成功' : 'Rating submitted', icon: 'success' })
  },

  goToComment() {
    const userInfo = wx.getStorageSync('userInfo')
    if (!userInfo) {
      wx.showToast({ title: this.data.lang === 'zh' ? '请先登录' : 'Please login first', icon: 'none' })
      return
    }
    wx.navigateTo({ url: `/pages/comment/edit/edit?type=attraction&id=${this.data.id}` })
  },

  openNavigation() {
    const { detail, lang } = this.data
    if (!detail) return
    wx.openLocation({
      latitude: parseFloat(detail.latitude),
      longitude: parseFloat(detail.longitude),
      name: lang === 'zh' ? detail.name_cn : detail.name_en,
      address: lang === 'zh' ? detail.address_cn : detail.address_en
    })
  },

  copyAddress() {
    const { detail, lang } = this.data
    wx.setClipboardData({
      data: lang === 'zh' ? detail.address_cn : detail.address_en,
      success: () => wx.showToast({ title: this.data.lang === 'zh' ? '复制成功' : 'Copied', icon: 'success' })
    })
  },

  callPhone() {
    if (!this.data.detail.phone) return
    wx.makePhoneCall({ phoneNumber: this.data.detail.phone })
  },

  toggleShare() {
    this.setData({ showShare: !this.data.showShare })
  },

  onShareAppMessage() {
    const { detail, lang } = this.data
    return {
      title: lang === 'zh' ? detail.name_cn : detail.name_en,
      path: `/pages/attraction/detail/detail?id=${detail.id}`,
      imageUrl: detail.cover_img
    }
  },

  previewImage(e) {
    const { url } = e.currentTarget.dataset
    wx.previewImage({
      current: url,
      urls: this.data.detail.images
    })
  }
})
