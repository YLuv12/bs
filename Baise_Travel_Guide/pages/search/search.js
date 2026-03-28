const app = getApp()
const { get, getImageUrl } = require('../../utils/request')

Page({
  data: {
    lang: 'zh',
    keyword: '',
    historyList: [],
    scenicList: [],
    foodList: [],
    loading: false,
    searched: false
  },

  onLoad() {
    const lang = app.globalData.lang || 'zh'
    this.setData({ lang })
    this.loadHistory()
  },

  onShow() {
    this.setData({ lang: app.globalData.lang || 'zh' })
  },

  loadHistory() {
    const history = wx.getStorageSync('searchHistory') || []
    this.setData({ historyList: history.slice(0, 10) })
  },

  onInput(e) {
    this.setData({ keyword: e.detail.value })
  },

  clearInput() {
    this.setData({ keyword: '', scenicList: [], foodList: [], searched: false })
  },

  doSearch() {
    const { keyword } = this.data
    if (!keyword || !keyword.trim()) {
      wx.showToast({ title: this.data.lang === 'zh' ? '请输入搜索内容' : 'Please enter keyword', icon: 'none' })
      return
    }

    this.saveHistory(keyword.trim())
    this.searchData(keyword.trim())
  },

  saveHistory(keyword) {
    let history = wx.getStorageSync('searchHistory') || []
    history = history.filter(item => item !== keyword)
    history.unshift(keyword)
    history = history.slice(0, 10)
    wx.setStorageSync('searchHistory', history)
    this.setData({ historyList: history })
  },

  clearHistory() {
    wx.removeStorageSync('searchHistory')
    this.setData({ historyList: [] })
  },

  async searchData(keyword) {
    this.setData({ loading: true, searched: true })

    try {
      const [scenicRes, foodRes] = await Promise.all([
        get('/scenic-spot/list', { page: 1, size: 20, keyword }),
        get('/food/list', { page: 1, size: 20, keyword })
      ])

      const lang = this.data.lang

      const scenicList = (scenicRes.records || []).map(item => ({
        id: item.id,
        name: lang === 'zh' ? (item.name_cn || '暂无名称') : (item.name_en || 'No Name'),
        image: getImageUrl(item.cover_img) || '/images/banner/banner1.jpg',
        rating: item.avg_score || 0,
        address: lang === 'zh' ? (item.address_cn || '') : (item.address_en || ''),
        type: 'scenic'
      }))

      const foodList = (foodRes.records || []).map(item => ({
        id: item.id,
        name: lang === 'zh' ? (item.name_cn || '暂无名称') : (item.name_en || 'No Name'),
        image: getImageUrl(item.cover_img) || '/images/banner/banner2.jpg',
        rating: item.avg_score || 0,
        price: item.per_capita || 0,
        address: lang === 'zh' ? (item.address_cn || '') : (item.address_en || ''),
        type: 'food'
      }))

      this.setData({ scenicList, foodList, loading: false })

    } catch (error) {
      console.error('搜索失败:', error)
      this.setData({ loading: false })
      wx.showToast({ title: this.data.lang === 'zh' ? '搜索失败' : 'Search failed', icon: 'none' })
    }
  },

  onHistoryTap(e) {
    const { keyword } = e.currentTarget.dataset
    this.setData({ keyword })
    this.searchData(keyword)
  },

  goToDetail(e) {
    const { id, type } = e.currentTarget.dataset
    if (type === 'scenic') {
      wx.navigateTo({ url: `/pages/attraction/detail/detail?id=${id}` })
    } else {
      wx.navigateTo({ url: `/pages/food/detail/detail?id=${id}` })
    }
  }
})
