const API_BASE = '/api';

function showToast(message, type = 'success') {
    const toast = document.getElementById('toast');
    toast.textContent = message;
    toast.className = 'toast ' + type + ' show';
    setTimeout(() => {
        toast.classList.remove('show');
    }, 3000);
}

function setLoading(loading) {
    const btn = document.querySelector('.login-btn');
    if (loading) {
        btn.classList.add('loading');
        btn.disabled = true;
    } else {
        btn.classList.remove('loading');
        btn.disabled = false;
    }
}

function setCookie(name, value, days) {
    let expires = "";
    if (days) {
        const date = new Date();
        date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
        expires = "; expires=" + date.toUTCString();
    }
    document.cookie = name + "=" + (value || "") + expires + "; path=/";
}

document.getElementById('loginForm').addEventListener('submit', async function(e) {
    e.preventDefault();
    
    const username = document.getElementById('username').value.trim();
    const password = document.getElementById('password').value;
    
    if (!username) {
        showToast('请输入用户名', 'error');
        return;
    }
    
    if (!password) {
        showToast('请输入密码', 'error');
        return;
    }
    
    setLoading(true);
    
    try {
        const response = await fetch(API_BASE + '/admin/auth/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username, password })
        });
        
        const result = await response.json();
        
        if (result.code === 200) {
            localStorage.setItem('admin_token', result.data.token);
            localStorage.setItem('admin_info', JSON.stringify(result.data.admin));
            setCookie('admin_token', result.data.token, 7);
            showToast('登录成功，正在跳转...', 'success');
            setTimeout(() => {
                window.location.href = '/api/admin/page/index';
            }, 1000);
        } else {
            showToast(result.msg || '登录失败', 'error');
        }
    } catch (error) {
        console.error('Login error:', error);
        showToast('网络错误，请稍后重试', 'error');
    } finally {
        setLoading(false);
    }
});

const savedUsername = localStorage.getItem('admin_username');
if (savedUsername) {
    document.getElementById('username').value = savedUsername;
    document.getElementById('remember').checked = true;
}

document.getElementById('remember').addEventListener('change', function() {
    if (this.checked) {
        localStorage.setItem('admin_username', document.getElementById('username').value);
    } else {
        localStorage.removeItem('admin_username');
    }
});
