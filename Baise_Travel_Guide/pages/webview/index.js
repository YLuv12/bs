const app = getApp()
Page({
  data: { lang: 'zh', type: '', content: '' },
  onLoad(options) {
    const { type } = options
    this.setData({ lang: app.globalData.lang, type })
    this.loadContent(type)
    wx.setNavigationBarTitle({ title: type === 'userProtocol' ? (this.data.lang === 'zh' ? '用户协议' : 'User Agreement') : (this.data.lang === 'zh' ? '隐私政策' : 'Privacy Policy') })
  },
  loadContent(type) {
    const content = type === 'userProtocol' 
      ? '用户协议内容...\n\n1. 服务条款\n2. 用户责任\n3. 知识产权\n4. 免责声明'
      : '隐私政策内容...\n\n1. 信息收集\n2. 信息使用\n3. 信息保护\n4. 用户权利'
    this.setData({ content })
  }
})
