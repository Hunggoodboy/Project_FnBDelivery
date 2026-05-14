

document.addEventListener("DOMContentLoaded", function() {
    // 1. Khai báo các biến (DOM Elements)
    const overlay = document.getElementById('cartOverlay');
    const sidebar = document.getElementById('cartSidebar');
    const closeBtn = document.getElementById('closeCartBtn');

    // Tìm tất cả các nút có class là 'btn-open-cart' (để dùng được cho nhiều nút)
    // Bạn nhớ đặt class="btn-open-cart" cho nút Thêm vào giỏ hoặc icon giỏ hàng
    const openCartBtns = document.querySelectorAll('.btn.btn-open-cart, .cart-icon');
    // 2. Hàm mở giỏ hàng
    function openCart() {
        if(sidebar && overlay) {
            sidebar.classList.add('active');
            overlay.classList.add('active');
            document.body.style.overflow = 'hidden';
        }
    }

    // 3. Hàm đóng giỏ hàng
    function closeCart() {
        if(sidebar && overlay) {
            sidebar.classList.remove('active');
            overlay.classList.remove('active');
            document.body.style.overflow = ''; // Mở lại cuộn
        }
    }

    // 4. Gắn sự kiện (Event Listeners)

    // Gắn sự kiện click cho TẤT CẢ các nút mở giỏ hàng
    if (openCartBtns.length > 0) {
        openCartBtns.forEach(btn => {
            btn.addEventListener('click', function(e) {
                e.preventDefault(); // Chặn thẻ a chuyển trang
                openCart();
            });
        });
    }

    // Sự kiện đóng
    if(closeBtn) closeBtn.addEventListener('click', closeCart);
    if(overlay) overlay.addEventListener('click', closeCart);
});