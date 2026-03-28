const app = getApp()
const { getImageUrl } = require('../../../utils/request')

Page({
  data: { 
    lang: 'zh', 
    list: [], 
    loading: true 
  },

  onLoad() { 
    this.setData({ lang: app.globalData.lang || 'zh' })
    this.loadData()
  },

  onShow() { 
    this.setData({ lang: app.globalData.lang || 'zh' })
    this.loadData()
  },

  async loadData() {
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
          url: 'http://localhost:8081/api/footprint/list',
          method: 'GET',
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
        time: item.browse_time || item.create_time
      }))
      
      this.setData({ list, loading: false })
    } catch (error) {
      console.error('加载浏览历史失败:', error)
      this.setData({ loading: false, list: [] })
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

  deleteHistory(e) {
    const { id } = e.currentTarget.dataset
    const self = this
    wx.showModal({
      title: this.data.lang === 'zh' ? '提示' : 'Tip',
      content: this.data.lang === 'zh' ? '确定删除该记录吗？' : 'Delete this record?',
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
          url: `http://localhost:8081/api/footprint/${id}`,
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
      wx.showToast({ title: this.data.lang === 'zh' ? '删除成功' : 'Deleted', icon: 'success' })
    } catch (error) {
      wx.showToast({ title: error.message || '删除失败', icon: 'none' })
    }
  },

  clearAll() {
    const self = this
    wx.showModal({
      title: this.data.lang === 'zh' ? '提示' : 'Tip',
      content: this.data.lang === 'zh' ? '确定清空所有浏览记录吗？' : 'Clear all history?',
      success(res) {
        if (res.confirm) {
          self.doClearAll()
        }
      }
    })
  },

  async doClearAll() {
    const token = wx.getStorageSync('token')
    try {
      await new Promise((resolve, reject) => {
        wx.request({
          url: 'http://localhost:8081/api/footprint/clear',
          method: 'DELETE',
          header: { 
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
          },
          success: (response) => {
            if (response.data && response.data.code === 200) {
              resolve(response.data)
            } else {
              reject(new Error((response.data && response.data.message) || '清空失败'))
            }
          },
          fail: () => reject(new Error('网络请求失败'))
        })
      })
      
      this.setData({ list: [] })
      wx.showToast({ title: this.data.lang === 'zh' ? '已清空' : 'Cleared', icon: 'success' })
    } catch (error) {
      wx.showToast({ title: error.message || '操作失败', icon: 'none' })
    }
  }
})
