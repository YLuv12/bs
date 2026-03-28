/*
 *  高德地图微信小程序SDK
 *  Version: 1.0.18
 *  Author: AMap
 *  Update: 2021-03-17
 */
class AMapWX {
  constructor(opts) {
    if (!opts.key) {
      throw 'key不能为空';
    }
    this.key = opts.key;
    this.webApiUrl = opts.webApiUrl || 'https://restapi.amap.com';
  }

  getRegeo(opts) {
    this._request({
      url: '/v3/geocode/regeo',
      data: {
        location: opts.location,
        extensions: opts.extensions || 'base',
        poitype: opts.poitype || '',
        radius: opts.radius || '1000',
        batch: opts.batch || false,
        roadlevel: opts.roadlevel || 0
      },
      method: 'GET',
      header: {},
      dataType: 'json',
      success: opts.success,
      fail: opts.fail,
      complete: opts.complete
    });
  }

  getGeocode(opts) {
    this._request({
      url: '/v3/geocode/geo',
      data: {
        address: opts.address,
        city: opts.city || '',
        batch: opts.batch || false
      },
      method: 'GET',
      header: {},
      dataType: 'json',
      success: opts.success,
      fail: opts.fail,
      complete: opts.complete
    });
  }

  getWeather(opts) {
    this._request({
      url: '/v3/weather/weatherInfo',
      data: {
        city: opts.city,
        extensions: opts.extensions || 'base',
        output: opts.output || 'json',
        key: this.key
      },
      method: 'GET',
      header: {},
      dataType: 'json',
      success: opts.success,
      fail: opts.fail,
      complete: opts.complete
    });
  }

  getInputtips(opts) {
    this._request({
      url: '/v3/assistant/inputtips',
      data: {
        keywords: opts.keywords,
        city: opts.city || '',
        type: opts.type || '',
        datatype: opts.datatype || '',
        location: opts.location || ''
      },
      method: 'GET',
      header: {},
      dataType: 'json',
      success: opts.success,
      fail: opts.fail,
      complete: opts.complete
    });
  }

  getPoiAround(opts) {
    this._request({
      url: '/v3/place/around',
      data: {
        location: opts.location,
        keywords: opts.keyword || '',
        types: opts.type || '',
        radius: opts.radius || '3000',
        offset: opts.pageSize || 20,
        page: opts.pageIndex || 1,
        sortrule: opts.sortrule || 'distance',
        extensions: opts.extensions || 'base'
      },
      method: 'GET',
      header: {},
      dataType: 'json',
      success: opts.success,
      fail: opts.fail,
      complete: opts.complete
    });
  }

  getPoiByID(opts) {
    this._request({
      url: '/v3/place/detail',
      data: {
        id: opts.id,
        extensions: opts.extensions || 'base'
      },
      method: 'GET',
      header: {},
      dataType: 'json',
      success: opts.success,
      fail: opts.fail,
      complete: opts.complete
    });
  }

  getPoiInCity(opts) {
    this._request({
      url: '/v3/place/text',
      data: {
        keywords: opts.keywords || '',
        city: opts.city || '',
        types: opts.types || '',
        children: opts.children || 0,
        offset: opts.offset || 20,
        page: opts.page || 1,
        extensions: opts.extensions || 'base'
      },
      method: 'GET',
      header: {},
      dataType: 'json',
      success: opts.success,
      fail: opts.fail,
      complete: opts.complete
    });
  }

  drivingRoute(opts) {
    this._request({
      url: '/v3/direction/driving',
      data: {
        origin: opts.origin,
        destination: opts.destination,
        waypoints: opts.waypoints || '',
        strategy: opts.strategy || 0,
        extensions: opts.extensions || 'base',
        avoid: opts.avoid || '',
        province: opts.province || '',
        number: opts.number || '',
        plate: opts.plate || ''
      },
      method: 'GET',
      header: {},
      dataType: 'json',
      success: opts.success,
      fail: opts.fail,
      complete: opts.complete
    });
  }

  walkingRoute(opts) {
    this._request({
      url: '/v3/direction/walking',
      data: {
        origin: opts.origin,
        destination: opts.destination
      },
      method: 'GET',
      header: {},
      dataType: 'json',
      success: opts.success,
      fail: opts.fail,
      complete: opts.complete
    });
  }

  ridingRoute(opts) {
    this._request({
      url: '/v3/direction/riding',
      data: {
        origin: opts.origin,
        destination: opts.destination,
        strategy: opts.strategy || 0
      },
      method: 'GET',
      header: {},
      dataType: 'json',
      success: opts.success,
      fail: opts.fail,
      complete: opts.complete
    });
  }

  busRoute(opts) {
    this._request({
      url: '/v3/direction/transit/integrated',
      data: {
        origin: opts.origin,
        destination: opts.destination,
        city: opts.city || '',
        strategy: opts.strategy || 0,
        extensions: opts.extensions || 'base',
        nightflag: opts.nightflag || 0,
        date: opts.date || '',
        time: opts.time || ''
      },
      method: 'GET',
      header: {},
      dataType: 'json',
      success: opts.success,
      fail: opts.fail,
      complete: opts.complete
    });
  }

  getDistrict(opts) {
    this._request({
      url: '/v3/config/district',
      data: {
        keywords: opts.keywords,
        subdistrict: opts.subdistrict || 0,
        extensions: opts.extensions || 'base',
        filter: opts.filter || false
      },
      method: 'GET',
      header: {},
      dataType: 'json',
      success: opts.success,
      fail: opts.fail,
      complete: opts.complete
    });
  }

  _request(opts) {
    opts.data.key = this.key;
    wx.request({
      url: this.webApiUrl + opts.url,
      data: opts.data,
      header: opts.header,
      method: opts.method,
      dataType: opts.dataType,
      success: opts.success,
      fail: opts.fail,
      complete: opts.complete
    });
  }

  static get locationAuthorize() {
    return 'scope.userLocation';
  }

  static get getLocationSuccessCode() {
    return 'SUCCESS';
  }

  static get getLocationFailCode() {
    return 'FAIL';
  }

  static get getLocationNoPermissionCode() {
    return 'NO_PERMISSION';
  }

  static getLocation(options) {
    options = options || {};
    const that = this;
    wx.getSetting({
      success(res) {
        if (res.authSetting['scope.userLocation']) {
          that._onGetLocation(options);
        } else {
          wx.authorize({
            scope: 'scope.userLocation',
            success() {
              that._onGetLocation(options);
            },
            fail() {
              options.fail && options.fail({ code: that.getLocationNoPermissionCode, message: '缺少权限' });
              options.complete && options.complete({ code: that.getLocationNoPermissionCode, message: '缺少权限' });
            }
          });
        }
      }
    });
  }

  static _onGetLocation(options) {
    wx.getLocation({
      type: 'gcj02',
      altitude: options.altitude || false,
      success(res) {
        options.success && options.success({ code: this.getLocationSuccessCode, latitude: res.latitude, longitude: res.longitude, accuracy: res.accuracy, altitude: res.altitude, verticalAccuracy: res.verticalAccuracy, speed: res.speed });
        options.complete && options.complete({ code: this.getLocationSuccessCode, latitude: res.latitude, longitude: res.longitude, accuracy: res.accuracy, altitude: res.altitude, verticalAccuracy: res.verticalAccuracy, speed: res.speed });
      },
      fail(err) {
        options.fail && options.fail({ code: this.getLocationFailCode, message: err.errMsg });
        options.complete && options.complete({ code: this.getLocationFailCode, message: err.errMsg });
      }
    });
  }
}

module.exports = { AMapWX: AMapWX };
