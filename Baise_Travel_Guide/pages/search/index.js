const app = getApp()
const { get, getImageUrl } = require('../../utils/request')

Page({
  data: {
    lang: 'zh',
    keyword: '',
    hotKeywords: ['百色起义纪念馆', '大石围天坑', '烧鸭粉', '靖西峡谷', '芒果'],
    history: [],
    attractions: [],
    foods: [],
    loading: false,
    searched: false,
    activeTab: 'all'
  },

  onLoad() {
    this.setData({ lang: app.globalData.lang, history: wx.getStorageSync('searchHistory') || [] })
  },

  onShow() { this.setData({ lang: app.globalData.lang }) },

  onInput(e) { this.setData({ keyword: e.detail.value }) },

  onSearch() {
    const { keyword } = this.data
    if (!keyword.trim()) {
      app.showToast(app.t('search', 'emptySearchKey'))
      return
    }
    this.saveHistory(keyword)
    this.doSearch()
  },

  saveHistory(keyword) {
    let history = this.data.history.filter(h => h !== keyword)
    history.unshift(keyword)
    if (history.length > 10) history = history.slice(0, 10)
    this.setData({ history })
    wx.setStorageSync('searchHistory', history)
  },

  async doSearch() {
    this.setData({ loading: true, searched: true })
    try {
      const { keyword } = this.data;
      
      // 同时搜索景点和美食
      const [attractionsData, foodsData] = await Promise.all([
        get('/scenic-spot/search', { keyword }),
        get('/food/search', { keyword })
      ]);
      
      // 处理景点数据
      const attractions = attractionsData.map(item => ({
        id: item.id,
        name: this.data.lang === 'zh' ? item.nameZh : item.nameEn,
        nameEn: item.nameEn,
        image: getImageUrl(item.images ? item.images.split(',')[0] : null) || '/images/banner/banner1.jpg',
        rating: item.avgScore || 0,
        region: item.region,
        type: 'attraction'
      }));
      
      const foods = foodsData.map(item => ({
        id: item.id,
        name: this.data.lang === 'zh' ? item.nameZh : item.nameEn,
        nameEn: item.nameEn,
        image: getImageUrl(item.images ? item.images.split(',')[0] : null) || '/images/banner/banner2.jpg',
        rating: item.avgScore || 0,
        price: item.avgPrice || 0,
        type: 'food'
      }));
      
      this.setData({ attractions, foods, loading: false });
    } catch (error) {
      console.error('搜索失败:', error);
      this.setData({ loading: false });
      app.showToast(this.data.lang === 'zh' ? '搜索失败' : 'Search failed');
    }
  },

  onHotTap(e) {
    const { keyword } = e.currentTarget.dataset
    this.setData({ keyword }, () => this.onSearch())
  },

  onHistoryTap(e) {
    const { keyword } = e.currentTarget.dataset
    this.setData({ keyword }, () => this.onSearch())
  },

  clearHistory() {
    wx.showModal({
      title: this.data.lang === 'zh' ? '提示' : 'Tip',
      content: this.data.lang === 'zh' ? '确定清空搜索历史？' : 'Clear search history?',
      success: res => {
        if (res.confirm) {
          this.setData({ history: [] })
          wx.removeStorageSync('searchHistory')
        }
      }
    })
  },

  switchTab(e) {
    this.setData({ activeTab: e.currentTarget.dataset.tab })
  },

  goToAttraction(e) {
    wx.navigateTo({ url: `/pages/attraction/detail/detail?id=${e.currentTarget.dataset.id}` })
  },

  goToFood(e) {
    wx.navigateTo({ url: `/pages/food/detail/detail?id=${e.currentTarget.dataset.id}` })
  }
})
