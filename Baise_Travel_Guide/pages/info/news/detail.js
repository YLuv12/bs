const app = getApp()
const { getImageUrl } = require('../../../utils/request')

Page({
  data: { 
    lang: 'zh', 
    detail: null, 
    loading: true 
  },

  onLoad(options) {
    this.setData({ lang: app.globalData.lang || 'zh' })
    if (options.id) {
      this.loadDetail(options.id)
    } else {
      wx.showToast({ title: '参数错误', icon: 'none' })
      setTimeout(() => wx.navigateBack(), 1000)
    }
  },

  async loadDetail(id) {
    this.setData({ loading: true })
    
    try {
      const res = await new Promise((resolve, reject) => {
        wx.request({
          url: `http://localhost:8081/api/news/${id}`,
          method: 'GET',
          header: { 'Content-Type': 'application/json' },
          success: (response) => {
            if (response.data && response.data.code === 200) {
              resolve(response.data)
            } else {
              reject(new Error((response.data && response.data.message) || '加载失败'))
            }
          },
          fail: () => reject(new Error('网络请求失败'))
        })
      })

      const newsData = res.data
      const detail = {
        id: newsData.id,
        title: newsData.title_cn || newsData.title_en,
        titleEn: newsData.title_en || newsData.title_cn,
        image: getImageUrl(newsData.cover_img),
        author: newsData.author_name,
        publishTime: newsData.create_time,
        viewCount: newsData.read_count || 0,
        content: this.data.lang === 'zh' ? newsData.content_cn : newsData.content_en,
        tags: newsData.tags || ''
      }

      this.setData({ detail, loading: false })
      wx.setNavigationBarTitle({ 
        title: this.data.lang === 'zh' ? detail.title : detail.titleEn 
      })

    } catch (error) {
      console.error('加载资讯详情失败:', error)
      this.setData({ loading: false })
      wx.showToast({ 
        title: this.data.lang === 'zh' ? '加载失败' : 'Load failed', 
        icon: 'none' 
      })
    }
  },

  onShareAppMessage() {
    const { detail, lang } = this.data
    if (detail) {
      return { 
        title: lang === 'zh' ? detail.title : detail.titleEn, 
        path: `/pages/info/news/detail?id=${detail.id}` 
      }
    }
    return { title: '资讯详情', path: '/pages/index/index' }
  }
})
