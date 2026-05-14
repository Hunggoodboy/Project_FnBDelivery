document.addEventListener('DOMContentLoaded', () => {
    loadAllOrders();
    updateStats();
});

async function loadAllOrders() {
    try {
        const response = await fetch('/api/order/all', { credentials: 'include' });
        const orders = await response.json();
        renderOrders(orders, 'Danh sách Tất cả Đơn hàng');
    } catch (error) {
        console.error('Error fetching all orders:', error);
        showNotification('Không thể tải danh sách đơn hàng', 'error');
    }
}

async function loadTodayOrders() {
    try {
        const response = await fetch('/api/order/today');
        const orders = await response.json();
        renderOrders(orders, 'Danh sách Đơn hàng Hôm nay');
    } catch (error) {
        console.error('Error fetching today orders:', error);
        showNotification('Không thể tải danh sách đơn hàng hôm nay', 'error');
    }
}

async function updateStats() {
    try {
        // Get all orders to calculate stats
        const response = await fetch('/api/order/all');
        const allOrders = await response.json();
        
        // Get today orders
        const todayResponse = await fetch('/api/order/today');
        const todayOrders = await todayResponse.json();

        document.getElementById('count-all').textContent = allOrders.length;
        document.getElementById('count-today').textContent = todayOrders.length;
        
        const pendingCount = allOrders.filter(o => o.status === 'Pending').length;
        document.getElementById('count-pending').textContent = pendingCount;

    } catch (error) {
        console.error('Error updating stats:', error);
    }
}

function renderOrders(orders, title) {
    const tableTitle = document.getElementById('table-title');
    const orderList = document.getElementById('order-list');
    
    tableTitle.textContent = title;
    orderList.innerHTML = '';

    if (orders.length === 0) {
        orderList.innerHTML = '<tr><td colspan="9" style="text-align: center; padding: 2rem;">Không có đơn hàng nào</td></tr>';
        return;
    }

    orders.forEach(order => {
        const row = document.createElement('tr');
        
        const date = new Date(order.createdAt);
        const formattedDate = date.toLocaleString('vi-VN', {
            hour: '2-digit',
            minute: '2-digit',
            day: '2-digit',
            month: '2-digit'
        });

        row.innerHTML = `
            <td>#${order.id}</td>
            <td><strong>${order.customerName}</strong></td>
            <td style="color: #ff4d6d; font-weight: 600;">${order.nameOfFood || 'N/A'}</td>
            <td>${order.quantity || '0'}</td>
            <td>${order.phoneNumber}</td>
            <td><span title="${order.address}">${truncateText(order.address, 20)}</span></td>
            <td>${getPaymentBadge(order.paymentMethod)}</td>
            <td><strong>${formatCurrency(order.totalPrice)}</strong></td>
            <td>${formattedDate}</td>
            <td>${getStatusBadge(order.status)}</td>
            <td>
                <button class="action-btn" title="Xem chi tiết"><i class="fas fa-eye"></i></button>
                <button class="action-btn" title="Cập nhật trạng thái"><i class="fas fa-edit"></i></button>
            </td>
        `;
        orderList.appendChild(row);
    });
}

function getStatusBadge(status) {
    const s = status ? status.toLowerCase() : 'pending';
    return `<span class="status-badge status-${s}">${status}</span>`;
}

function getPaymentBadge(method) {
    // Basic mapping for payment methods
    const methods = {
        'eye': 'Ánh mắt',
        'lip': 'Nụ hôn',
        'cheek': 'Thơm má',
        'forehead': 'Hôn trán',
        'hug': 'Cái ôm'
    };
    return `<span style="font-size: 0.85rem; color: #636e72;">${methods[method] || method}</span>`;
}

function formatCurrency(amount) {
    // In this project, price seems to be in "units of affection" (hugs, kisses)
    return amount + ' đơn vị';
}

function truncateText(text, length) {
    if (!text) return '';
    return text.length > length ? text.substring(0, length) + '...' : text;
}

function showNotification(message, type = 'success') {
    const container = document.getElementById('notification-container');
    const notification = document.createElement('div');
    notification.className = `notification ${type}`;
    notification.innerHTML = `
        <i class="fas ${type === 'success' ? 'fa-check-circle' : 'fa-exclamation-circle'}"></i>
        <span>${message}</span>
    `;
    
    container.appendChild(notification);
    
    setTimeout(() => {
        notification.style.opacity = '0';
        setTimeout(() => notification.remove(), 500);
    }, 3000);
}
