const API_BASE = '/api';
let currentPage = 1;
let pageSize = 10;
let totalRecords = 0;

function getToken() {
    return localStorage.getItem('admin_token') || getCookie('admin_token');
}

function getCookie(name) {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) return parts.pop().split(';').shift();
}

function showToast(message, type = 'success') {
    const toast = document.getElementById('toast');
    if (toast) {
        toast.textContent = message;
        toast.className = 'toast ' + type + ' show';
        setTimeout(() => {
            toast.classList.remove('show');
        }, 3000);
    }
}

function showLoading() {
    const loading = document.getElementById('loading');
    if (loading) {
        loading.style.display = 'flex';
    }
}

function hideLoading() {
    const loading = document.getElementById('loading');
    if (loading) {
        loading.style.display = 'none';
    }
}

async function request(url, options = {}) {
    const token = getToken();
    const defaultHeaders = {
        'Content-Type': 'application/json'
    };
    
    if (token) {
        defaultHeaders['Authorization'] = 'Bearer ' + token;
    }
    
    const config = {
        ...options,
        headers: {
            ...defaultHeaders,
            ...options.headers
        }
    };
    
    console.log('Request URL:', API_BASE + url);
    console.log('Request config:', config);
    
    try {
        const response = await fetch(API_BASE + url, config);
        console.log('Response status:', response.status);
        
        if (!response.ok) {
            console.error('Response not ok:', response.status, response.statusText);
            if (response.status === 401) {
                showToast('登录已过期，请重新登录', 'error');
                setTimeout(() => {
                    window.location.href = '/api/admin/page/login';
                }, 1500);
            }
            return null;
        }
        
        const result = await response.json();
        console.log('Response result:', result);
        
        if (result.code === 401) {
            showToast('登录已过期，请重新登录', 'error');
            setTimeout(() => {
                window.location.href = '/api/admin/page/login';
            }, 1500);
            return null;
        }
        
        return result;
    } catch (error) {
        console.error('Request error:', error);
        showToast('网络错误，请稍后重试', 'error');
        return null;
    }
}

function toggleSidebar() {
    const sidebar = document.getElementById('sidebar');
    if (sidebar) {
        sidebar.classList.toggle('collapsed');
    }
}

function toggleDropdown() {
    const dropdown = document.querySelector('.user-dropdown');
    if (dropdown) {
        dropdown.classList.toggle('open');
    }
}

function toggleSubmenu(link) {
    const parent = link.closest('.nav-item');
    if (parent) {
        parent.classList.toggle('open');
    }
}

function refreshPage() {
    window.location.reload();
}

async function logout() {
    if (!confirm('确定要退出登录吗？')) {
        return;
    }
    
    try {
        await request('/admin/auth/logout', { method: 'POST' });
    } catch (e) {
        console.error('Logout error:', e);
    }
    
    localStorage.removeItem('admin_token');
    localStorage.removeItem('admin_info');
    window.location.href = '/api/admin/page/login';
}

function openModal(title, content, footer = '') {
    const modal = document.getElementById('modal');
    const modalTitle = modal.querySelector('.modal-title');
    const modalBody = modal.querySelector('.modal-body');
    const modalFooter = modal.querySelector('.modal-footer');
    
    if (modalTitle) modalTitle.textContent = title;
    if (modalBody) modalBody.innerHTML = content;
    if (modalFooter) modalFooter.innerHTML = footer;
    
    modal.classList.add('show');
    document.body.style.overflow = 'hidden';
    
    return new Promise(resolve => {
        setTimeout(resolve, 10);
    });
}

function closeModal() {
    const modal = document.getElementById('modal');
    modal.classList.remove('show');
    document.body.style.overflow = '';
}

function renderPagination(container, current, total, size, onPageChange) {
    const totalPages = Math.ceil(total / size);
    if (totalPages <= 0) {
        container.innerHTML = '';
        return;
    }
    
    let html = '<div class="pagination-info">共 ' + total + ' 条记录，第 ' + current + '/' + totalPages + ' 页</div>';
    html += '<div class="pagination-btns">';
    
    html += '<button class="page-btn" onclick="' + onPageChange + '(1)" ' + (current <= 1 ? 'disabled' : '') + '>首页</button>';
    html += '<button class="page-btn" onclick="' + onPageChange + '(' + (current - 1) + ')" ' + (current <= 1 ? 'disabled' : '') + '>上一页</button>';
    
    let startPage = Math.max(1, current - 2);
    let endPage = Math.min(totalPages, startPage + 4);
    
    if (endPage - startPage < 4) {
        startPage = Math.max(1, endPage - 4);
    }
    
    for (let i = startPage; i <= endPage; i++) {
        html += '<button class="page-btn ' + (i === current ? 'active' : '') + '" onclick="' + onPageChange + '(' + i + ')">' + i + '</button>';
    }
    
    html += '<button class="page-btn" onclick="' + onPageChange + '(' + (current + 1) + ')" ' + (current >= totalPages ? 'disabled' : '') + '>下一页</button>';
    html += '<button class="page-btn" onclick="' + onPageChange + '(' + totalPages + ')" ' + (current >= totalPages ? 'disabled' : '') + '>末页</button>';
    
    html += '</div>';
    
    container.innerHTML = html;
}

function formatDate(dateStr) {
    if (!dateStr) return '-';
    const date = new Date(dateStr);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    const seconds = String(date.getSeconds()).padStart(2, '0');
    return year + '-' + month + '-' + day + ' ' + hours + ':' + minutes + ':' + seconds;
}

function formatShortDate(dateStr) {
    if (!dateStr) return '-';
    const date = new Date(dateStr);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return year + '-' + month + '-' + day;
}

function escapeHtml(text) {
    if (!text) return '';
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

function getSelectedIds(tableId) {
    const table = document.getElementById(tableId);
    if (!table) return [];
    
    const checkboxes = table.querySelectorAll('tbody .row-checkbox:checked');
    return Array.from(checkboxes).map(cb => cb.value);
}

function toggleSelectAll(tableId, checkbox) {
    const table = document.getElementById(tableId);
    if (!table) return;
    
    const rowCheckboxes = table.querySelectorAll('tbody .row-checkbox');
    rowCheckboxes.forEach(cb => {
        cb.checked = checkbox.checked;
        const row = cb.closest('tr');
        if (row) {
            row.classList.toggle('selected', checkbox.checked);
        }
    });
}

document.addEventListener('click', function(e) {
    const dropdown = document.querySelector('.user-dropdown');
    if (dropdown && !dropdown.contains(e.target)) {
        dropdown.classList.remove('open');
    }
});

document.addEventListener('keydown', function(e) {
    if (e.key === 'Escape') {
        closeModal();
    }
});

document.getElementById('modal')?.addEventListener('click', function(e) {
    if (e.target === this) {
        closeModal();
    }
});

async function uploadImage(file) {
    const formData = new FormData();
    formData.append('file', file);
    
    const token = getToken();
    
    try {
        const response = await fetch(API_BASE + '/admin/upload/image', {
            method: 'POST',
            headers: {
                'Authorization': 'Bearer ' + token
            },
            body: formData
        });
        
        const result = await response.json();
        
        if (result.code === 200) {
            return result.data.url;
        } else {
            showToast(result.msg || '上传失败', 'error');
            return null;
        }
    } catch (error) {
        console.error('Upload error:', error);
        showToast('上传失败', 'error');
        return null;
    }
}

function createImageUploader(inputId, previewId, isMultiple, maxCount) {
    const preview = document.getElementById(previewId);
    
    if (!preview) return;
    
    const limitText = maxCount ? (isMultiple ? '（最多' + maxCount + '张）' : '（仅限1张）') : '';
    
    let html = '<div class="image-upload-wrapper">';
    html += '<label class="upload-placeholder" for="' + inputId + '">';
    html += '<svg viewBox="0 0 24 24"><path fill="currentColor" d="M19,13H13V19H11V13H5V11H11V5H13V11H19V13Z"/></svg>';
    html += '<span>选择图片' + limitText + '</span>';
    html += '</label>';
    html += '<input type="file" id="' + inputId + '" accept="image/*" style="display: none;"' + (isMultiple ? ' multiple' : '') + '>';
    html += '</div>';
    html += '<div class="image-preview-container" id="' + previewId + 'Images"></div>';
    
    preview.innerHTML = html;
    
    const input = document.getElementById(inputId);
    const imagesContainer = document.getElementById(previewId + 'Images');
    
    input.addEventListener('change', async function(e) {
        const files = Array.from(e.target.files);
        if (files.length === 0) return;
        
        const existingItems = imagesContainer.querySelectorAll('.image-preview-item');
        const existingCount = existingItems.length;
        
        if (maxCount) {
            const remaining = maxCount - existingCount;
            if (remaining <= 0) {
                showToast(isMultiple ? '轮播图最多只能上传' + maxCount + '张' : '封面图只能上传1张', 'error');
                input.value = '';
                return;
            }
            if (files.length > remaining) {
                showToast(isMultiple ? '轮播图最多只能上传' + maxCount + '张，当前已上传' + existingCount + '张，还可上传' + remaining + '张' : '封面图只能上传1张', 'warning');
            }
        }
        
        for (const file of files) {
            if (!file.type.startsWith('image/')) {
                showToast('请选择图片文件', 'error');
                continue;
            }
            
            if (file.size > 5 * 1024 * 1024) {
                showToast('图片大小不能超过5MB', 'error');
                continue;
            }
        }
        
        showToast('正在上传...', 'info');
        
        const uploadedUrls = [];
        const filesToUpload = maxCount ? files.slice(0, maxCount - existingCount) : files;
        
        for (const file of filesToUpload) {
            const url = await uploadImage(file);
            if (url) {
                uploadedUrls.push(url);
                addImagePreview(imagesContainer, url, previewId, isMultiple, maxCount);
            }
        }
        
        if (uploadedUrls.length > 0) {
            updateHiddenInput(previewId, isMultiple);
            showToast('上传成功', 'success');
        }
        
        input.value = '';
    });
}

function addImagePreview(container, url, previewId, isMultiple, maxCount) {
    const existingItems = container.querySelectorAll('.image-preview-item');
    if (maxCount && existingItems.length >= maxCount) {
        showToast(isMultiple ? '轮播图最多只能上传' + maxCount + '张' : '封面图只能上传1张', 'error');
        return;
    }
    
    const item = document.createElement('div');
    item.className = 'image-preview-item';
    item.dataset.url = url;
    item.innerHTML = `
        <img src="${url}" alt="预览图">
        <button type="button" class="delete-btn" onclick="removeImagePreview(this, '${previewId}', ${isMultiple}, ${maxCount || 'null'})">×</button>
    `;
    container.appendChild(item);
}

function removeImagePreview(btn, previewId, isMultiple, maxCount) {
    const item = btn.parentElement;
    item.remove();
    updateHiddenInput(previewId, isMultiple);
}

function updateHiddenInput(previewId, isMultiple) {
    const container = document.getElementById(previewId + 'Images');
    const hiddenInput = document.getElementById(previewId + 'Url');
    
    if (!container || !hiddenInput) return;
    
    const items = container.querySelectorAll('.image-preview-item');
    const urls = Array.from(items).map(item => item.dataset.url);
    
    hiddenInput.value = isMultiple ? urls.join(',') : (urls[0] || '');
}

function initImagePreview(previewId, existingUrls, isMultiple, maxCount) {
    const container = document.getElementById(previewId + 'Images');
    const hiddenInput = document.getElementById(previewId + 'Url');
    
    if (!container) return;
    
    container.innerHTML = '';
    
    if (existingUrls) {
        let urls = isMultiple ? existingUrls.split(',').filter(u => u.trim()) : [existingUrls];
        if (maxCount && urls.length > maxCount) {
            urls = urls.slice(0, maxCount);
        }
        urls.forEach(url => {
            if (url.trim()) {
                addImagePreview(container, url.trim(), previewId, isMultiple, maxCount);
            }
        });
    }
    
    if (hiddenInput) {
        hiddenInput.value = existingUrls || '';
    }
}
