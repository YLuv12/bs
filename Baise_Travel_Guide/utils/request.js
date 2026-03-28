const BASE_URL = 'http://localhost:8081/api';

function getImageUrl(path) {
  if (!path) return '';
  
  if (path.startsWith('http://') || path.startsWith('https://')) {
    return path;
  }
  
  if (path.startsWith('/api/uploads/')) {
    return 'http://localhost:8081' + path;
  }
  
  if (path.startsWith('/uploads/')) {
    return 'http://localhost:8081/api' + path;
  }
  
  if (path.startsWith('uploads/')) {
    return 'http://localhost:8081/api/' + path;
  }
  
  if (path.startsWith('/api/')) {
    return 'http://localhost:8081' + path;
  }
  
  return path;
}

function request(url, options = {}) {
  return new Promise((resolve, reject) => {
    const token = wx.getStorageSync('token');
    const fullUrl = BASE_URL + url;
    
    wx.request({
      url: fullUrl,
      method: options.method || 'GET',
      data: options.data || {},
      header: {
        'Content-Type': 'application/json',
        'Authorization': token ? `Bearer ${token}` : '',
        ...options.header
      },
      success: (res) => {
        if (res.statusCode === 401 || (res.data && res.data.code === 401)) {
          wx.removeStorageSync('token');
          wx.removeStorageSync('userInfo');
          wx.showToast({ title: '登录已过期，请重新登录', icon: 'none' });
          setTimeout(() => {
            wx.navigateTo({ url: '/pages/login/index' });
          }, 1500);
          reject(new Error('登录已过期'));
          return;
        }
        
        if (res.data && res.data.code === 200) {
          resolve(res.data.data);
        } else if (res.data && res.data.message) {
          reject(new Error(res.data.message));
        } else {
          resolve(res.data);
        }
      },
      fail: (err) => {
        reject(new Error('网络请求失败'));
      }
    });
  });
}

function get(url, data = {}) {
  return request(url, { method: 'GET', data });
}

function post(url, data = {}) {
  return request(url, { method: 'POST', data });
}

function put(url, data = {}) {
  return request(url, { method: 'PUT', data });
}

function del(url, data = {}) {
  return request(url, { method: 'DELETE', data });
}

module.exports = { get, post, put, delete: del, request, getImageUrl };
