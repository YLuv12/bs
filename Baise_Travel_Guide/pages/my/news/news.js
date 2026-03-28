const app = getApp()

Page({
  data: {
    lang: 'zh',
    list: [],
    loading: true,
    page: 1,
    pageSize: 10,
    hasMore: true
  },

  onLoad() {
    this.setData({ lang: app.globalData.lang || 'zh' })
    this.loadData()
  },

  onShow() {
    this.setData({ lang: app.globalData.lang || 'zh' })
    this.refreshData()
  },

  async refreshData() {
    this.setData({ page: 1, hasMore: true })
    await this.loadData(true)
  },

  async loadData(refresh) {
    const token = wx.getStorageSync('token')
    if (!token) {
      wx.showToast({ title: this.data.lang === 'zh' ? '请先登录' : 'Please login first', icon: 'none' })
      setTimeout(() => wx.navigateTo({ url: '/pages/login/index' }), 1000)
      return
    }

    if (refresh) {
      this.setData({ loading: true })
    }

    try {
      const res = await new Promise((resolve, reject) => {
        wx.request({
          url: 'http://localhost:8081/api/news/my-list',
          method: 'GET',
          data: {
            page: this.data.page,
            pageSize: this.data.pageSize
          },
          header: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
          },
          success: (response) => {
            if (response.data && response.data.code === 200) {
              resolve(response.data)
            } else {
              reject(new Error((response.data && response.data.message) || 'load failed'))
            }
          },
          fail: () => reject(new Error('network error'))
        })
      })

      const records = (res.records || res.data || []).map(item => ({
        id: item.id,
        title: this.data.lang === 'zh' ? (item.title_cn || item.title_en) : (item.title_en || item.title_cn),
        coverImg: item.cover_img,
        authorName: item.author_name,
        tags: item.tags || '',
        readCount: item.read_count || 0,
        createTime: item.create_time,
        status: item.status
      }))

      const newList = refresh ? records : [...this.data.list, ...records]
      const hasMore = records.length >= this.data.pageSize

      this.setData({ 
        list: newList, 
        loading: false, 
        hasMore,
        page: this.data.page + 1
      })

    } catch (error) {
      console.error('加载资讯列表失败:', error)
      this.setData({ loading: false })
      wx.showToast({ 
        title: this.data.lang === 'zh' ? '加载失败' : 'Load failed', 
        icon: 'none' 
      })
    }
  },

  onReachBottom() {
    if (this.data.hasMore && !this.data.loading) {
      this.loadData()
    }
  },

  goToDetail(e) {
    const { id } = e.currentTarget.dataset
    wx.navigateTo({ url: `/pages/info/news/detail?id=${id}` })
  },

  deleteNews(e) {
    const { id } = e.currentTarget.dataset
    const self = this
    wx.showModal({
      title: this.data.lang === 'zh' ? '提示' : 'Tip',
      content: this.data.lang === 'zh' ? '确定删除该资讯吗？' : 'Delete this news?',
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
          url: `http://localhost:8081/api/news/${id}`,
          method: 'DELETE',
          header: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
          },
          success: (response) => {
            if (response.data && response.data.code === 200) {
              resolve(response.data)
            } else {
              reject(new Error((response.data && response.data.message) || 'delete failed'))
            }
          },
          fail: () => reject(new Error('network error'))
        })
      })

      this.setData({ 
        list: this.data.list.filter(item => item.id !== id) 
      })
      wx.showToast({ 
        title: this.data.lang === 'zh' ? '删除成功' : 'Deleted', 
        icon: 'success' 
      })

    } catch (error) {
      wx.showToast({ 
        title: this.data.lang === 'zh' ? '删除失败' : 'Delete failed', 
        icon: 'none' 
      })
    }
  },

  goToPublish() {
    wx.navigateTo({ url: '/pages/info/news/publish' })
  }
})
