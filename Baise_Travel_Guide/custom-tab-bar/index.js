const app = getApp()

Component({
  data: {
    lang: 'zh',
    selected: 0,
    color: "#8A8AA3",
    selectedColor: "#E53935",
    list: [
      { pagePath: "/pages/index/index", iconPath: "/images/tabbar/shouye.png", selectedIconPath: "/images/tabbar/shouye_xz.png", text: "首页", textEn: "Home" },
      { pagePath: "/pages/attraction/list/list", iconPath: "/images/tabbar/iconjiudianzhuanqu35.png", selectedIconPath: "/images/tabbar/iconjiudianzhuanqu_xz.png", text: "景点", textEn: "Attractions" },
      { pagePath: "/pages/info/news/publish", center: true },
      { pagePath: "/pages/food/list/list", iconPath: "/images/tabbar/meishi.png", selectedIconPath: "/images/tabbar/meishi_xz.png", text: "美食", textEn: "Food" },
      { pagePath: "/pages/my/index/index", iconPath: "/images/tabbar/yonghu.png", selectedIconPath: "/images/tabbar/yonghu_xz.png", text: "我的", textEn: "Me" }
    ]
  },

  lifetimes: {
    attached() {
      this.setData({ lang: app.globalData.lang || 'zh' })
    }
  },

  pageLifetimes: {
    show() {
      this.setData({ lang: app.globalData.lang || 'zh' })
    }
  },

  methods: {
    switchTab(e) {
      const { index, path } = e.currentTarget.dataset
      const item = this.data.list[index]
      
      if (item.center) {
        wx.navigateTo({ url: path })
      } else {
        this.setData({ selected: index })
        wx.switchTab({ url: path })
      }
    }
  }
})
