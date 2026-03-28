const app = getApp()
const { getImageUrl } = require('../../../utils/request')

Page({
  data: { 
    lang: 'zh', 
    list: [], 
    loading: true,
    activeTab: 'all'
  },

  onLoad() { 
    this.setData({ lang: app.globalData.lang || 'zh' })
    this.loadData()
  },

  onShow() { 
    this.setData({ lang: app.globalData.lang || 'zh' })
    this.loadData()
  },

  switchTab(e) {
    const tab = e.currentTarget.dataset.tab
    this.setData({ activeTab: tab })
    this.loadData(tab)
  },

  async loadData(type = 'all') {
    const token = wx.getStorageSync('token')
    if (!token) {
      wx.showToast({ title: '请先登录', icon: 'none' })
      setTimeout(() => wx.navigateTo({ url: '/pages/login/index' }), 1000)
      return
    }

    this.setData({ loading: true })
    
    try {
      const res = await new Promise((resolve, reject) => {
        wx.request({
          url: 'http://localhost:8081/api/collect/list',
          method: 'GET',
          data: { type: type === 'all' ? '' : type },
          header: { 
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
          },
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
      
      const list = (res.records || res.data || []).map(item => ({
        id: item.id,
        targetId: item.target_id,
        name: this.data.lang === 'zh' ? item.target_name_cn : item.target_name_en,
        image: getImageUrl(item.target_image) || '/images/banner/banner1.jpg',
        type: item.target_type === 1 ? 'attraction' : 'food',
        typeText: item.target_type === 1 ? (this.data.lang === 'zh' ? '景点' : 'Attraction') : (this.data.lang === 'zh' ? '美食' : 'Food'),
        time: item.create_time
      }))
      
      this.setData({ list, loading: false })
    } catch (error) {
      console.error('加载收藏列表失败:', error)
      this.setData({ loading: false, list: [] })
      wx.showToast({ title: error.message || '加载失败', icon: 'none' })
    }
  },

  goToDetail(e) {
    const { id, type } = e.currentTarget.dataset
    if (type === 'attraction') {
      wx.navigateTo({ url: `/pages/attraction/detail/detail?id=${id}` })
    } else {
      wx.navigateTo({ url: `/pages/food/detail/detail?id=${id}` })
    }
  },

  deleteFavorite(e) {
    const { id } = e.currentTarget.dataset
    const self = this
    wx.showModal({
      title: this.data.lang === 'zh' ? '提示' : 'Tip',
      content: this.data.lang === 'zh' ? '确定取消收藏吗？' : 'Remove from favorites?',
      success(res) {
        if (res.confirm) {
          self.doDelete(id)
        }
      }
    })
  },

  async doDelete(id) {
    const token = wx.getStorageSync('token')
    try {
      await new Promise((resolve, reject) => {
        wx.request({
          url: `http://localhost:8081/api/collect/${id}`,
          method: 'DELETE',
          header: { 
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
          },
          success: (response) => {
            if (response.data && response.data.code === 200) {
              resolve(response.data)
            } else {
              reject(new Error((response.data && response.data.message) || '删除失败'))
            }
          },
          fail: () => reject(new Error('网络请求失败'))
        })
      })
      
      this.setData({ 
        list: this.data.list.filter(item => item.id !== id) 
      })
      wx.showToast({ title: this.data.lang === 'zh' ? '已取消收藏' : 'Removed', icon: 'success' })
    } catch (error) {
      wx.showToast({ title: error.message || '操作失败', icon: 'none' })
    }
  }
})
