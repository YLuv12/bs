const { get, getImageUrl } = require('../../utils/request')
const app = getApp()

Page({
  data: {
    lang: 'zh',
    bannerList: [],
    attractionList: [],
    foodList: [],
    newsList: [],
    recommendList: [],
    loading: true,
    showBackTop: false
  },

  onLoad() {
    const lang = app.globalData.lang || 'zh'
    this.setData({ lang })
    wx.setNavigationBarTitle({ title: lang === 'zh' ? '百色旅游' : 'Baise Travel' })
    this.initData()
  },

  onShow() {
    const lang = app.globalData.lang || 'zh'
    this.setData({ lang })
    wx.setNavigationBarTitle({ title: lang === 'zh' ? '百色旅游' : 'Baise Travel' })
    if (typeof this.getTabBar === 'function' && this.getTabBar()) {
      this.getTabBar().setData({ selected: 0, lang })
    }
  },

  async initData() {
    this.setData({ loading: true })
    try {
      await Promise.all([
        this.loadBannerData(),
        this.loadAttractions(),
        this.loadFoods(),
        this.loadNews()
      ])
      this.generateRecommendList()
    } catch (error) {
      console.error('加载数据失败:', error)
    } finally {
      this.setData({ loading: false })
    }
  },

  async loadBannerData() {
    try {
      const [scenicRes, foodRes] = await Promise.all([
        get('/scenic-spot/list', { page: 1, size: 2 }),
        get('/food/list', { page: 1, size: 2 })
      ])
      
      const bannerList = []
      
      if (scenicRes && scenicRes.records) {
        scenicRes.records.forEach(item => {
          bannerList.push({
            id: item.id,
            type: 'scenic',
            name_cn: item.name_cn || '暂无名称',
            name_en: item.name_en || 'No Name',
            cover_img: getImageUrl(item.cover_img) || '/images/banner/banner1.jpg'
          })
        })
      }
      
      if (foodRes && foodRes.records) {
        foodRes.records.forEach(item => {
          bannerList.push({
            id: item.id,
            type: 'food',
            name_cn: item.name_cn || '暂无名称',
            name_en: item.name_en || 'No Name',
            cover_img: getImageUrl(item.cover_img) || '/images/banner/banner2.jpg'
          })
        })
      }
      
      this.setData({ bannerList })
    } catch (error) {
      console.error('加载轮播图数据失败:', error)
    }
  },

  async loadAttractions() {
    try {
      const res = await get('/scenic-spot/list', { page: 1, size: 6, sortBy: 'random' })
      if (res && res.records) {
        let attractionList = res.records.map(item => ({
          id: item.id,
          name_cn: item.name_cn || '暂无名称',
          name_en: item.name_en || 'No Name',
          cover_img: getImageUrl(item.cover_img) || '/images/banner/banner1.jpg',
          avg_score: item.avg_score || 0,
          visit_count: item.visit_count || 0,
          address: item.address_cn || '',
          latitude: item.latitude,
          longitude: item.longitude,
          distance: null,
          distanceText: ''
        }))
        
        if (app.globalData.location) {
          attractionList = await app.calculateDistancesForList(attractionList)
        }
        
        this.setData({ attractionList })
      }
    } catch (error) {
      console.error('加载景点数据失败:', error)
    }
  },

  async loadFoods() {
    try {
      const res = await get('/food/list', { page: 1, size: 6, sortBy: 'random' })
      if (res && res.records) {
        let foodList = res.records.map(item => ({
          id: item.id,
          name_cn: item.name_cn || '暂无名称',
          name_en: item.name_en || 'No Name',
          cover_img: getImageUrl(item.cover_img) || '/images/banner/banner2.jpg',
          avg_score: item.avg_score || 0,
          per_capita: item.per_capita || 0,
          visit_count: item.visit_count || 0,
          latitude: item.latitude,
          longitude: item.longitude,
          distance: null,
          distanceText: ''
        }))
        
        if (app.globalData.location) {
          foodList = await app.calculateDistancesForList(foodList)
        }
        
        this.setData({ foodList })
      }
    } catch (error) {
      console.error('加载美食数据失败:', error)
    }
  },

  async loadNews() {
    try {
      const res = await get('/news/list', { page: 1, size: 5 })
      if (res && res.records) {
        const newsList = res.records.map(item => ({
          id: item.id,
          title_cn: item.title_cn || '暂无标题',
          title_en: item.title_en || 'No Title',
          cover_img: getImageUrl(item.cover_img) || '/images/banner/banner1.jpg',
          read_count: item.read_count || 0,
          create_time: item.create_time || ''
        }))
        this.setData({ newsList })
      }
    } catch (error) {
      console.error('加载新闻数据失败:', error)
    }
  },

  generateRecommendList() {
    const { attractionList, foodList } = this.data
    const recommendList = []
    
    if (attractionList.length > 0) {
      recommendList.push({
        ...attractionList[0],
        type: 'scenic'
      })
    }
    if (foodList.length > 0) {
      recommendList.push({
        ...foodList[0],
        type: 'food'
      })
    }
    if (attractionList.length > 1) {
      recommendList.push({
        ...attractionList[1],
        type: 'scenic'
      })
    }
    if (foodList.length > 1) {
      recommendList.push({
        ...foodList[1],
        type: 'food'
      })
    }
    
    this.setData({ recommendList })
  },

  onBannerTap(e) {
    const { id, type } = e.currentTarget.dataset
    if (type === 'scenic') {
      wx.navigateTo({
        url: `/pages/attraction/detail/detail?id=${id}`
      })
    } else if (type === 'food') {
      wx.navigateTo({
        url: `/pages/food/detail/detail?id=${id}`
      })
    }
  },

  toggleLang() {
    const newLang = this.data.lang === 'zh' ? 'en' : 'zh'
    app.globalData.lang = newLang
    wx.setStorageSync('lang', newLang)
    this.setData({ lang: newLang })
    
    wx.setNavigationBarTitle({ title: newLang === 'zh' ? '百色旅游' : 'Baise Travel' })
    
    if (typeof this.getTabBar === 'function' && this.getTabBar()) {
      this.getTabBar().setData({ lang: newLang })
    }
  },

  goToSearch() {
    wx.navigateTo({ url: '/pages/search/search' })
  },

  goToAttractionList() {
    wx.switchTab({ url: '/pages/attraction/list/list' })
  },

  goToFoodList() {
    wx.switchTab({ url: '/pages/food/list/list' })
  },

  goToAttractionDetail(e) {
    const { id } = e.currentTarget.dataset
    wx.navigateTo({ url: `/pages/attraction/detail/detail?id=${id}` })
  },

  goToFoodDetail(e) {
    const { id } = e.currentTarget.dataset
    wx.navigateTo({ url: `/pages/food/detail/detail?id=${id}` })
  },

  goToNewsDetail(e) {
    const { id } = e.currentTarget.dataset
    wx.navigateTo({ url: `/pages/info/news/detail?id=${id}` })
  },

  goToPublishNews() {
    wx.navigateTo({ url: '/pages/info/news/publish' })
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

  onReachBottom() {
    console.log('触底加载更多')
  },

  onPullDownRefresh() {
    this.initData().then(() => {
      wx.stopPullDownRefresh()
    })
  }
})
