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
  },

  async refreshData() {
    this.setData({ page: 1, hasMore: true, list: [] })
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
      this.setData({ loading: true, page: 1 })
    }

    const currentPage = refresh ? 1 : this.data.page

    try {
      const res = await new Promise((resolve, reject) => {
        wx.request({
          url: 'http://localhost:8081/api/feedback/list',
          method: 'GET',
          data: {
            page: currentPage,
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

      const typeMap = {
        1: this.data.lang === 'zh' ? '景点添加' : 'Add Attraction',
        2: this.data.lang === 'zh' ? '美食添加' : 'Add Food',
        3: this.data.lang === 'zh' ? 'Bug反馈' : 'Bug Report',
        4: this.data.lang === 'zh' ? '投诉' : 'Complaint',
        5: this.data.lang === 'zh' ? '功能建议' : 'Suggestion',
        6: this.data.lang === 'zh' ? '其他' : 'Other'
      }

      const records = (res.records || res.data || []).map(item => ({
        id: item.id,
        type: typeMap[item.type],
        content: item.content,
        contentShort: item.content.length > 50 ? item.content.substring(0, 50) + '...' : item.content,
        imgs: item.imgs ? item.imgs.split(',') : [],
        status: item.status,
        statusText: item.status === 0 ? (this.data.lang === 'zh' ? '待处理' : 'Pending') : (this.data.lang === 'zh' ? '已处理' : 'Processed'),
        handleResult: item.handle_result,
        handleTime: item.handle_time,
        createTime: item.create_time
      }))

      const newList = refresh ? records : [...this.data.list, ...records]
      const hasMore = records.length >= this.data.pageSize

      this.setData({ 
        list: newList, 
        loading: false, 
        hasMore,
        page: currentPage + 1
      })

    } catch (error) {
      console.error('加载反馈记录失败:', error)
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

  goToSubmit() {
    wx.navigateTo({ url: '/pages/feedback/index/index' })
  },

  previewImage(e) {
    const { index, imgs } = e.currentTarget.dataset
    wx.previewImage({ urls: imgs, current: imgs[index] })
  }
})
