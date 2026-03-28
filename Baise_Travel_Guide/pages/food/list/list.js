const app = getApp()
const { get, getImageUrl } = require('../../../utils/request')

// 标签映射表，用于多语言切换
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
  '1': '小吃',
  '2': '餐厅',
  '3': '特产',
  '4': '甜品'
};

// 反向映射，用于英文模式
const reverseTagMap = {};
Object.keys(tagMap).forEach(key => {
  reverseTagMap[tagMap[key]] = key;
});

// 区域名称映射
const regionMap = {
  'Youjiang': '右江区',
  'Jingxi': '靖西市',
  'Leye': '乐业县',
  'Tiandong': '田东县'
};

// 反向区域名称映射
const reverseRegionMap = {};
Object.keys(regionMap).forEach(key => {
  reverseRegionMap[regionMap[key]] = key;
});

// 根据语言获取标签
function getTagsByLang(tags, lang) {
  if (!tags || !Array.isArray(tags)) return [];
  return tags.map(tag => {
    // 处理数字类型的标签
    const tagStr = typeof tag === 'number' ? tag.toString() : tag;
    const normalizedTag = tagStr.toLowerCase().trim();
    if (lang === 'zh') {
      return tagMap[normalizedTag] || tagStr;
    } else {
      return reverseTagMap[tagStr] || tagStr;
    }
  });
}

// 根据语言获取区域名称
function getRegionByLang(region, lang) {
  if (!region) return '';
  if (lang === 'zh') {
    return regionMap[region] || region;
  } else {
    return reverseRegionMap[region] || region;
  }
}

// 确保返回数据是数组
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
    keyword: '',
    category: '',
    region: '',
    sortType: 'hot',
    categories: [],
    regions: [],
    showFilter: false,
    showBackTop: false,
    reachBottom: false
  },

  onLoad() {
    this.initLang()
    this.loadCategories()
    this.loadData()
  },

  onShow() {
    this.initLang()
    if (typeof this.getTabBar === 'function' && this.getTabBar()) {
      this.getTabBar().setData({ selected: 3, lang: app.globalData.lang })
    }
  },

  initLang() {
    const lang = app.globalData.lang || 'zh'
    this.setData({ lang })
    wx.setNavigationBarTitle({ title: lang === 'zh' ? '美食探索' : 'Food Explore' })
    
    if (this.data.list.length > 0) {
      this.loadData(true)
    }
  },

  loadCategories() {
    const lang = this.data.lang
    this.setData({
      categories: [
        { value: '', label: lang === 'zh' ? '全部' : 'All' },
        { value: '小吃', labelEn: 'Snack' },
        { value: '粉面', labelEn: 'Noodles' },
        { value: '特色菜', labelEn: 'Featured Dish' },
        { value: '火锅', labelEn: 'Hot Pot' },
        { value: '早餐', labelEn: 'Breakfast' },
        { value: '甜品', labelEn: 'Dessert' },
        { value: '特产', labelEn: 'Specialty' },
        { value: '饮品', labelEn: 'Beverage' },
        { value: '茶饮', labelEn: 'Tea' },
        { value: '家常菜', labelEn: 'Home Cooking' }
      ].map(item => ({
        value: item.value,
        label: item.value ? (lang === 'zh' ? item.value : item.labelEn) : item.label
      })),
      regions: [
        { value: '', label: lang === 'zh' ? '全部区域' : 'All Regions' },
        { value: 'youjiang', label: lang === 'zh' ? '右江区' : 'Youjiang' },
        { value: 'jingxi', label: lang === 'zh' ? '靖西市' : 'Jingxi' },
        { value: 'leye', label: lang === 'zh' ? '乐业县' : 'Leye' },
        { value: 'tiandong', label: lang === 'zh' ? '田东县' : 'Tiandong' },
        { value: 'pingguo', label: lang === 'zh' ? '平果市' : 'Pingguo' },
        { value: 'debao', label: lang === 'zh' ? '德保县' : 'Debao' },
        { value: 'napo', label: lang === 'zh' ? '那坡县' : 'Napo' },
        { value: 'lingyun', label: lang === 'zh' ? '凌云县' : 'Lingyun' },
        { value: 'tianlin', label: lang === 'zh' ? '田林县' : 'Tianlin' },
        { value: 'longlin', label: lang === 'zh' ? '隆林县' : 'Longlin' },
        { value: 'xilin', label: lang === 'zh' ? '西林县' : 'Xilin' }
      ]
    })
  },

  async loadData(refresh = false) {
    if (refresh) {
      this.setData({ page: 1, hasMore: true, list: [] })
    }
    this.setData({ loading: true })
    
    try {
      console.log('开始加载美食列表...');
      const { page, pageSize, category, region, sortType, keyword } = this.data;
      const sortBy = {
        'hot': 'visit',
        'score': 'score',
        'price': 'price',
        'new': 'id'
      }[sortType] || 'visit';
      
      const params = {
        page,
        size: pageSize,
        region,
        category,
        sortBy
      };
      
      if (keyword && keyword.trim()) {
        params.keyword = keyword.trim();
      }
      
      console.log('请求参数:', params);
      const response = await get('/food/list', params);
      
      console.log('获取美食列表数据:', response);
      // 处理后端返回的数据格式
      const records = response.records ? ensureArray(response.records) : [];
      
      let newList = records.map(item => {
        const lang = this.data.lang;
        return {
          id: item.id,
          name: lang === 'zh' ? (item.name_cn || '暂无名称') : (item.name_en || 'No Name'),
          nameEn: item.name_en || '',
          image: getImageUrl(item.cover_img) || getImageUrl(item.imgs ? item.imgs.split(',')[0] : null) || '/images/banner/banner2.jpg',
          rating: item.avg_score || 0,
          ratingCount: item.score_count || 0,
          region: lang === 'zh' ? (item.address_cn || '').split(' ')[0] : (item.address_en || '').split(' ')[0],
          price: item.per_capita || 0,
          tags: [lang === 'zh' ? (item.type_cn || '') : (item.type_en || '')].filter(Boolean),
          distanceText: '',
          distance: null,
          latitude: item.latitude,
          longitude: item.longitude,
          openTime: lang === 'zh' ? (item.business_time_cn || '') : (item.business_time_en || ''),
          phone: item.phone || ''
        };
      });
      
      if (app.globalData.location) {
        newList = await app.calculateDistancesForList(newList)
        newList.sort((a, b) => (a.distance || 9999) - (b.distance || 9999))
      }
      
      const list = refresh ? newList : [...this.data.list, ...newList];
      const hasMore = page < (response.pages || 1);
      
      console.log('最终列表:', list, '是否有更多:', hasMore);
      this.setData({ 
        list, 
        loading: false, 
        hasMore
      });
    } catch (e) {
      console.error('加载美食列表失败:', e);
      this.setData({ loading: false });
      app.showToast(this.data.lang === 'zh' ? '加载失败' : 'Load failed');
    }
  },

  onPullDownRefresh() {
    this.setData({ reachBottom: false })
    this.loadData(true).then(() => {
      wx.stopPullDownRefresh()
      wx.showToast({
        title: this.data.lang === 'zh' ? '已刷新' : 'Refreshed',
        icon: 'none',
        duration: 3000,
        mask: false
      })
    })
  },

  onReachBottom() {
    if (this.data.hasMore && !this.data.loading) {
      this.setData({ page: this.data.page + 1 })
      this.loadData()
    } else {
      wx.showToast({
        title: this.data.lang === 'zh' ? '已经到底啦～' : "You've reached the end～",
        icon: 'none',
        duration: 2000,
        mask: false
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
    wx.pageScrollTo({ scrollTop: 0, duration: 500, success: () => {
      this.setData({ showBackTop: false, reachBottom: false })
    } })
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
    this.setData({ category: e.currentTarget.dataset.value, showFilter: false })
    this.loadData(true)
  },

  onRegionChange(e) {
    this.setData({ region: e.currentTarget.dataset.value, showFilter: false })
    this.loadData(true)
  },

  onSortChange(e) {
    this.setData({ sortType: e.currentTarget.dataset.type })
    this.loadData(true)
  },

  toggleFilter() { this.setData({ showFilter: !this.data.showFilter }) },
  closeFilter() { this.setData({ showFilter: false }) },
  clearFilter() { this.setData({ category: '', region: '', showFilter: false }); this.loadData(true) },

  goToDetail(e) {
    wx.navigateTo({ url: `/pages/food/detail/detail?id=${e.currentTarget.dataset.id}` })
  }
})