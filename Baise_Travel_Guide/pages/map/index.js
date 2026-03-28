const app = getApp()
Page({
  data: {
    lang: 'zh',
    latitude: 23.9067,
    longitude: 106.6185,
    markers: [],
    scale: 13,
    showNearby: false,
    nearbyType: 'food',
    nearbyList: []
  },

  onLoad() {
    this.setData({ lang: app.globalData.lang })
    this.initMarkers()
    this.getLocation()
  },

  getLocation() {
    wx.getLocation({
      type: 'gcj02',
      success: res => {
        this.setData({ latitude: res.latitude, longitude: res.longitude })
      }
    })
  },

  initMarkers() {
    const markers = [
      { id: 1, latitude: 23.9067, longitude: 106.6185, title: '百色起义纪念馆', type: 'attraction', iconPath: '/images/marker/attraction.png', width: 32, height: 32 },
      { id: 2, latitude: 23.8967, longitude: 106.6085, title: '烧鸭粉店', type: 'food', iconPath: '/images/marker/food.png', width: 32, height: 32 }
    ]
    this.setData({ markers })
  },

  onMarkerTap(e) {
    const { markerId } = e.detail
    const marker = this.data.markers.find(m => m.id === markerId)
    if (marker) {
      if (marker.type === 'attraction') {
        wx.navigateTo({ url: `/pages/attraction/detail/detail?id=${marker.id}` })
      } else {
        wx.navigateTo({ url: `/pages/food/detail/detail?id=${marker.id}` })
      }
    }
  },

  moveToLocation() {
    this.mapCtx = wx.createMapContext('map')
    this.mapCtx.moveToLocation()
  },

  toggleNearby() {
    this.setData({ showNearby: !this.data.showNearby })
    if (this.data.showNearby) this.loadNearby()
  },

  switchNearbyType(e) {
    const { type } = e.currentTarget.dataset
    this.setData({ nearbyType: type })
    this.loadNearby()
  },

  loadNearby() {
    const nearbyList = this.data.nearbyType === 'food' ? [
      { id: 1, name: '烧鸭粉店', distance: 500, address: '城东大道', rating: 4.5 },
      { id: 2, name: '老友粉', distance: 800, address: '中山路', rating: 4.3 }
    ] : [
      { id: 1, name: '停车场', distance: 200, address: '纪念馆旁' },
      { id: 2, name: '卫生间', distance: 100, address: '景区内' }
    ]
    this.setData({ nearbyList })
  },

  goToDetail(e) {
    const { id } = e.currentTarget.dataset
    wx.navigateTo({ url: `/pages/food/detail/detail?id=${id}` })
  }
})
