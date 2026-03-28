const app = getApp()
const { get, getImageUrl } = require('../../../utils/request')

const tagMap = {
  'red tourism': '红色旅游',
  '4a': '4A级景区',
  '4a scenic spot': '4A级景区',
  'natural scenery': '自然景观',
  'canyon': '峡谷',
  'local specialty': '地方特产',
  'recommended': '推荐',
  'traditional food': '传统美食',
  'national ecotourism': '国家生态旅游',
  'patriotic education base': '爱国主义教育基地',
  'fruit': '水果',
  'national geographical indication product': '国家地理标志产品',
  'tiankeng': '天坑',
  'world geopark': '世界地质公园',
  '1': '红色景点',
  '2': '自然景观',
  '3': '人文景观'
};

const reverseTagMap = {};
Object.keys(tagMap).forEach(key => {
  reverseTagMap[tagMap[key]] = key;
});

function getTagsByLang(tags, lang) {
  if (!tags || !Array.isArray(tags)) return [];
  return tags.map(tag => {
    const tagStr = typeof tag === 'number' ? tag.toString() : tag;
    const normalizedTag = tagStr.toLowerCase().trim();
    if (lang === 'zh') {
      return tagMap[normalizedTag] || tagStr;
    } else {
      return reverseTagMap[tagStr] || tagStr;
    }
  });
}

function ensureArray(data) {
  if (!data) return [];
  if (Array.isArray(data)) return data;
  return [data];
}

Page({
  data: {
    lang: 'zh',
    list: [],
    loading: true,
    page: 1,
    pageSize: 10,
    hasMore: true,
    noMore: false,
    keyword: '',
    category: '',
    region: '',
    sortType: 'hot',
    categories: [],
    regions: [],
    showFilter: false,
    scrollTop: 0,
    showBackTop: false
  },

  onLoad() {
    this.initLang()
    this.loadCategories()
    this.loadData()
  },

  onShow() {
    this.initLang()
    if (typeof this.getTabBar === 'function' && this.getTabBar()) {
      this.getTabBar().setData({ selected: 1, lang: app.globalData.lang })
    }
  },

  initLang() {
    const lang = app.globalData.lang || 'zh'
    this.setData({ lang })
    wx.setNavigationBarTitle({ title: lang === 'zh' ? '景点探索' : 'Attractions' })
    
    if (this.data.list.length > 0) {
      this.loadData(true)
    }
  },

  t(key) {
    return app.t('attractionList', key)
  },

  async loadCategories() {
    const lang = this.data.lang
    this.setData({
      categories: [
        { value: '', label: lang === 'zh' ? '全部' : 'All' },
        { value: 'red', label: lang === 'zh' ? '红色景点' : 'Red Tourism Site' },
        { value: 'nature', label: lang === 'zh' ? '自然风光' : 'Natural Scenery' },
        { value: 'ethnic', label: lang === 'zh' ? '民族村寨' : 'Ethnic Village' },
        { value: 'other', label: lang === 'zh' ? '其他' : 'Other' }
      ],
      regions: [
        { value: '', label: lang === 'zh' ? '全部地区' : 'All Regions' },
        { value: 'youjiang', label: lang === 'zh' ? '右江区' : 'Youjiang District' },
        { value: 'tianyang', label: lang === 'zh' ? '田阳区' : 'Tianyang District' },
        { value: 'tiandong', label: lang === 'zh' ? '田东县' : 'Tiandong County' },
        { value: 'tianlin', label: lang === 'zh' ? '田林县' : 'Tianlin County' },
        { value: 'xilin', label: lang === 'zh' ? '西林县' : 'Xilin County' },
        { value: 'longlin', label: lang === 'zh' ? '隆林各族自治县' : 'Longlin County' },
        { value: 'jingxi', label: lang === 'zh' ? '靖西市' : 'Jingxi City' },
        { value: 'napo', label: lang === 'zh' ? '那坡县' : 'Napo County' },
        { value: 'debao', label: lang === 'zh' ? '德保县' : 'Debao County' },
        { value: 'leye', label: lang === 'zh' ? '乐业县' : 'Leye County' },
        { value: 'lingyun', label: lang === 'zh' ? '凌云县' : 'Lingyun County' },
        { value: 'pingguo', label: lang === 'zh' ? '平果市' : 'Pingguo City' }
      ]
    })
  },

  async loadData(refresh = false) {
    if (refresh) {
      this.setData({ page: 1, hasMore: true, noMore: false, list: [] })
    }
    
    this.setData({ loading: true })
    
       try {
      const { page, pageSize, category, region, sortType, keyword } = this.data;
      
      let sortBy = 'random';
      if (sortType === 'hot') {
        sortBy = 'visit';
      } else if (sortType === 'rating') {
        sortBy = 'score';
      } else if (sortType === 'distance') {
        sortBy = 'distance';
      }
      
      const params = {
        page,
        size: pageSize,
        region,
        type: category,
        sortBy
      };
      
      if (keyword && keyword.trim()) {
        params.keyword = keyword.trim();
      }
      
      const response = await get('/scenic-spot/list', params);
      
      const records = response.records ? ensureArray(response.records) : [];
      
      let newList = records.map(item => {
        const lang = this.data.lang;
        const ticketInfo = lang === 'zh' ? (item.ticket_info_cn || '免费') : (item.ticket_info_en || 'Free');
        const isFree = !ticketInfo || ticketInfo === '免费' || ticketInfo === 'Free' || ticketInfo.includes('免费');
        return {
          id: item.id,
          name: lang === 'zh' ? (item.name_cn || '暂无名称') : (item.name_en || 'No Name'),
          nameEn: item.name_en || '',
          image: getImageUrl(item.cover_img) || getImageUrl(item.imgs ? item.imgs.split(',')[0] : null) || '/images/banner/banner1.jpg',
          rating: item.avg_score || 0,
          ratingCount: item.score_count || 0,
          region: lang === 'zh' ? (item.address_cn || '') : (item.address_en || ''),
          tags: [lang === 'zh' ? (item.type_cn || '') : (item.type_en || '')].filter(Boolean),
          viewCountText: item.visit_count ? (item.visit_count / 1000).toFixed(1) + 'k' : '0k',
          distanceText: '',
          distance: null,
          latitude: item.latitude,
          longitude: item.longitude,
          category: item.type,
          address: lang === 'zh' ? (item.address_cn || '') : (item.address_en || ''),
          openTime: lang === 'zh' ? (item.open_time_cn || '') : (item.open_time_en || ''),
          ticket: ticketInfo,
          isFree: isFree
        };
      });
      
      if (app.globalData.location) {
        newList = await app.calculateDistancesForList(newList)
        newList.sort((a, b) => (a.distance || 9999) - (b.distance || 9999))
      }
      
      const list = refresh ? newList : [...this.data.list, ...newList];
      const totalPages = response.pages || 1;
      const hasMore = page < totalPages;
      const noMore = !hasMore && list.length > 0;
      
      this.setData({ 
        list, 
        loading: false, 
        hasMore,
        noMore
      });
    } catch (e) {
      console.error('加载景点列表失败:', e);
      this.setData({ loading: false });
      app.showToast(app.t('global', 'networkError'));
    }
  },

  onPullDownRefresh() {
    this.loadData(true).then(() => {
      wx.stopPullDownRefresh()
      wx.showToast({
        title: this.data.lang === 'zh' ? '已刷新' : 'Refreshed',
        icon: 'none',
        duration: 1500
      })
    })
  },

  onReachBottom() {
    if (this.data.hasMore && !this.data.loading) {
      this.setData({ page: this.data.page + 1 }, () => {
        this.loadData()
      })
    }
  },

  onPageScroll(e) {
    const showBackTop = e.scrollTop > 300
    if (showBackTop !== this.data.showBackTop) {
      this.setData({ showBackTop })
    }
  },

  goToTop() {
    wx.pageScrollTo({ scrollTop: 0, duration: 500 })
  },

  onSearch(e) {
    const keyword = e.detail.value
    this.setData({ keyword })
    if (this.searchTimer) {
      clearTimeout(this.searchTimer)
    }
    this.searchTimer = setTimeout(() => {
      this.loadData(true)
    }, 300)
  },

  doSearch() {
    if (this.searchTimer) {
      clearTimeout(this.searchTimer)
    }
    this.loadData(true)
  },

  clearSearch() {
    this.setData({ keyword: '' })
    this.loadData(true)
  },

  onCategoryChange(e) {
    const { value } = e.currentTarget.dataset
    this.setData({ category: value, showFilter: false })
    this.loadData(true)
  },

  onRegionChange(e) {
    const { value } = e.currentTarget.dataset
    this.setData({ region: value, showFilter: false })
    this.loadData(true)
  },

  onSortChange(e) {
    const { type } = e.currentTarget.dataset
    this.setData({ sortType: type })
    this.loadData(true)
  },

  toggleFilter() {
    this.setData({ showFilter: !this.data.showFilter })
  },

  closeFilter() {
    this.setData({ showFilter: false })
  },

  clearFilter() {
    this.setData({ category: '', region: '', showFilter: false })
    this.loadData(true)
  },

  goToDetail(e) {
    const { id } = e.currentTarget.dataset
    wx.navigateTo({ url: `/pages/attraction/detail/detail?id=${id}` })
  }
})
