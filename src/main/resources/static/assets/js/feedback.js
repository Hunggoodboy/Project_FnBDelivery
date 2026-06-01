// Tích hợp logic xử lý Feedback & Đăng ảnh thực tế lên Cloudinary
document.addEventListener('DOMContentLoaded', () => {
    // Lấy Product ID từ URL
    const params = new URLSearchParams(window.location.search);
    const productId = params.get('id');

    // Hàm hiển thị thông báo ngọt ngào thay thế alert() mặc định
    function showSweetAlert(message, isSuccess = true) {
        return new Promise((resolve) => {
            const existing = document.getElementById('sweet-alert-modal');
            if (existing) existing.remove();

            const modal = document.createElement('div');
            modal.id = 'sweet-alert-modal';
            modal.className = 'sweet-alert-overlay';
            
            const heartIcon = isSuccess ? '💖' : '🥺';
            
            modal.innerHTML = `
                <div class="sweet-alert-content">
                    <div class="sweet-alert-heart">${heartIcon}</div>
                    <p class="sweet-alert-message">${message}</p>
                    <div class="sweet-alert-buttons">
                        <button class="alert-btn btn-yes">Dạ vâng ạ 🌸</button>
                        <button class="alert-btn btn-thanks">Cảm ơn chồng iu ❤️</button>
                    </div>
                </div>
            `;
            
            document.body.appendChild(modal);
            
            // Kích hoạt transition mượt
            setTimeout(() => modal.classList.add('active'), 10);
            
            const closeAlert = () => {
                modal.classList.remove('active');
                setTimeout(() => {
                    modal.remove();
                    resolve();
                }, 300);
            };
            
            modal.querySelector('.btn-yes').onclick = closeAlert;
            modal.querySelector('.btn-thanks').onclick = closeAlert;
        });
    }

    // Hàm hiển thị xác nhận ngọt ngào thay thế confirm() mặc định
    function showSweetConfirm(message) {
        return new Promise((resolve) => {
            const existing = document.getElementById('sweet-confirm-modal');
            if (existing) existing.remove();

            const modal = document.createElement('div');
            modal.id = 'sweet-confirm-modal';
            modal.className = 'sweet-alert-overlay';
            
            modal.innerHTML = `
                <div class="sweet-alert-content">
                    <div class="sweet-alert-heart">🥺</div>
                    <p class="sweet-alert-message">${message}</p>
                    <div class="sweet-alert-buttons">
                        <button class="alert-btn btn-cancel-alert">Thôi khom xóa nữa 🌸</button>
                        <button class="alert-btn btn-confirm-alert">Ngoan, vẫn xóa ạ ❤️</button>
                    </div>
                </div>
            `;
            
            document.body.appendChild(modal);
            
            setTimeout(() => modal.classList.add('active'), 10);
            
            const closeConfirm = (result) => {
                modal.classList.remove('active');
                setTimeout(() => {
                    modal.remove();
                    resolve(result);
                }, 300);
            };
            
            modal.querySelector('.btn-cancel-alert').onclick = () => closeConfirm(false);
            modal.querySelector('.btn-confirm-alert').onclick = () => closeConfirm(true);
        });
    }

    // Thiết lập hệ thống chọn đánh giá sao (Star Rating Selector)
    const starsSelector = document.querySelectorAll('#star-rating-selector i');
    const starValueInput = document.getElementById('feedback-star-value');

    starsSelector.forEach(star => {
        // Sự kiện di chuột (Hover) để sáng dần lên
        star.addEventListener('mouseover', function() {
            const rating = parseInt(this.getAttribute('data-rating'));
            highlightStars(rating, 'hover-active');
        });

        star.addEventListener('mouseout', function() {
            resetStarsHover();
        });

        // Sự kiện nhấp chuột (Click) để chọn số sao đánh giá
        star.addEventListener('click', function() {
            const rating = parseInt(this.getAttribute('data-rating'));
            starValueInput.value = rating;
            setSelectedStars(rating);
        });
    });

    function highlightStars(rating, className) {
        starsSelector.forEach(star => {
            const starRating = parseInt(star.getAttribute('data-rating'));
            if (starRating <= rating) {
                star.classList.add(className);
            } else {
                star.classList.remove(className);
            }
        });
    }

    function resetStarsHover() {
        starsSelector.forEach(star => star.classList.remove('hover-active'));
    }

    function setSelectedStars(rating) {
        starsSelector.forEach(star => {
            const starRating = parseInt(star.getAttribute('data-rating'));
            if (starRating <= rating) {
                star.classList.remove('fa-regular');
                star.classList.add('fa-solid', 'active');
            } else {
                star.classList.remove('fa-solid', 'active');
                star.classList.add('fa-regular');
            }
        });
    }

    // Mặc định chọn trước 5 sao
    setSelectedStars(5);

    // Cấu hình Cloudinary để tải ảnh lên giống trang addProduct.html
    const CLOUD_NAME = "dhoj2nuhf";
    const UPLOAD_PRESET = "mL_default1";

    async function uploadImageToCloudinary(file) {
        const cloudinaryData = new FormData();
        cloudinaryData.append('file', file);
        cloudinaryData.append('upload_preset', UPLOAD_PRESET);

        const cloudRes = await fetch(`https://api.cloudinary.com/v1_1/${CLOUD_NAME}/image/upload`, {
            method: 'POST',
            body: cloudinaryData
        });

        const cloudJson = await cloudRes.json();
        if (!cloudRes.ok) {
            throw new Error(cloudJson.error ? cloudJson.error.message : 'Lỗi không xác định từ Cloudinary');
        }
        return cloudJson.secure_url;
    }

    // Hàm Escape HTML tránh tấn công XSS
    function escapeHTML(str) {
        if (!str) return '';
        return str.replace(/[&<>'"]/g, 
            tag => ({
                '&': '&amp;',
                '<': '&lt;',
                '>': '&gt;',
                "'": '&#39;',
                '"': '&quot;'
            }[tag] || tag)
        );
    }

    // Tải danh sách đánh giá từ API
    async function loadFeedbacks() {
        if (!productId) return;
        try {
            const listContainer = document.getElementById('feedback-list-container');
            const response = await fetch(`/api/feedback/product?id=${productId}`);
            if (!response.ok) throw new Error("Không thể tải danh sách feedback");
            
            const feedbacks = await response.json();
            
            if (!feedbacks || feedbacks.length === 0) {
                listContainer.innerHTML = `<p class="no-feedback"><i class="fa-regular fa-comment-dots"></i> Chưa có đánh giá nào cho sản phẩm này. Hãy là người đầu tiên đánh giá!</p>`;
                return;
            }

            listContainer.innerHTML = feedbacks.map(fb => {
                // Tạo các sao rating tương ứng
                let starsHtml = '';
                for (let i = 1; i <= 5; i++) {
                    if (i <= fb.star) {
                        starsHtml += '<i class="fa-solid fa-star"></i>';
                    } else if (i - 0.5 <= fb.star) {
                        starsHtml += '<i class="fa-solid fa-star-half-stroke"></i>';
                    } else {
                        starsHtml += '<i class="fa-regular fa-star"></i>';
                    }
                }

                // Định dạng thời gian
                let dateStr = '';
                if (fb.createTime) {
                    const d = new Date(fb.createTime);
                    dateStr = `Đăng lúc: ${String(d.getDate()).padStart(2, '0')}/${String(d.getMonth() + 1).padStart(2, '0')}/${d.getFullYear()} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`;
                }

                // Ảnh phản hồi (nếu có)
                const imgHtml = fb.image ? `
                    <div class="feedback-img-wrapper" onclick="window.open('${fb.image}', '_blank')">
                        <img src="${fb.image}" alt="Hình ảnh feedback">
                    </div>
                ` : '';

                return `
                    <div class="feedback-row" id="feedback-item-${fb.id}">
                        <div class="feedback-left">
                            <div class="feedback-rating">${starsHtml}</div>
                            <p class="feedback-text">${escapeHTML(fb.content)}</p>
                            ${dateStr ? `<span class="feedback-time">${dateStr}</span>` : ''}
                        </div>
                        
                        <div class="feedback-right">
                            ${imgHtml}
                            <div class="feedback-actions">
                                <button class="action-btn edit-btn" onclick="startEditFeedback(${fb.id})" title="Sửa đánh giá">
                                    <i class="fa-solid fa-pen-to-square"></i>
                                </button>
                                <button class="action-btn delete-btn" onclick="deleteFeedback(${fb.id})" title="Xóa đánh giá">
                                    <i class="fa-solid fa-trash-can"></i>
                                </button>
                            </div>
                        </div>
                    </div>
                `;
            }).join('');

        } catch (err) {
            console.error("Lỗi khi tải feedback:", err);
        }
    }

    // Định nghĩa các hàm toàn cục để gọi từ HTML
    window.previewFeedbackImage = function(input) {
        const preview = document.getElementById('feedback-image-preview');
        if (input.files && input.files[0]) {
            const reader = new FileReader();
            reader.onload = function(e) {
                preview.innerHTML = `<img src="${e.target.result}" alt="Preview image">`;
            }
            reader.readAsDataURL(input.files[0]);
        } else {
            const existingUrl = document.getElementById('feedback-image-url').value;
            if (existingUrl) {
                preview.innerHTML = `<img src="${existingUrl}" alt="Preview image">`;
            } else {
                preview.innerHTML = `<span class="preview-placeholder">Xem trước ảnh tại đây</span>`;
            }
        }
    };

    window.startEditFeedback = async function(id) {
        try {
            // Lấy chi tiết feedback từ API
            const res = await fetch(`/api/feedback/detail?id=${id}`);
            if (!res.ok) throw new Error('Không thể tải chi tiết nhận xét');
            const data = await res.json();

            // Đưa thông tin vào form
            document.getElementById('edit-feedback-id').value = id;
            document.getElementById('feedback-content').value = data.content;
            document.getElementById('feedback-star-value').value = data.star;
            document.getElementById('feedback-image-url').value = data.image || '';

            // Chọn sao tương ứng
            setSelectedStars(Math.round(data.star));

            // Xem trước ảnh cũ
            const preview = document.getElementById('feedback-image-preview');
            if (data.image) {
                preview.innerHTML = `<img src="${data.image}" alt="Preview image">`;
            } else {
                preview.innerHTML = `<span class="preview-placeholder">Xem trước ảnh tại đây</span>`;
            }

            // Reset input file
            document.getElementById('feedback-image-file').value = '';

            // Đổi trạng thái giao diện sang EDIT
            document.getElementById('feedback-form-title').innerText = 'Chỉnh sửa đánh giá của bạn';
            document.getElementById('btn-cancel-edit').style.display = 'inline-block';
            document.getElementById('btn-submit-feedback').innerHTML = 'Lưu Thay Đổi <i class="fa-solid fa-circle-check"></i>';

            // Cuộn mượt đến vị trí form
            document.querySelector('.feedback-form-container').scrollIntoView({ behavior: 'smooth' });

        } catch (err) {
            showSweetAlert('Lỗi: ' + err.message, false);
        }
    };

    window.cancelEditFeedback = function() {
        resetFeedbackForm();
    };

    window.deleteFeedback = async function(id) {
        const confirmDelete = await showSweetConfirm('Hụ hụ, công chúa chắc chắn muốn xóa phản hồi này sao? 🥺');
        if (!confirmDelete) return;

        try {
            const res = await fetch(`/api/feedback/delete?id=${id}`, { method: 'DELETE' });
            if (!res.ok) throw new Error("Có lỗi khi xóa feedback");
            
            const result = await res.json();
            if (result.success) {
                await showSweetAlert(result.message || 'Xóa phản hồi thành công rồi ạ! 🌸', true);
                await loadFeedbacks();

                // Nếu đang chỉnh sửa feedback vừa xóa thì reset form
                const editingId = document.getElementById('edit-feedback-id').value;
                if (editingId == id) {
                    resetFeedbackForm();
                }
            } else {
                await showSweetAlert('Có lỗi xảy ra: ' + (result.message || 'Không thể xóa'), false);
            }
        } catch (err) {
            await showSweetAlert('Lỗi: ' + err.message, false);
        }
    };

    function resetFeedbackForm() {
        document.getElementById('edit-feedback-id').value = '';
        document.getElementById('feedback-content').value = '';
        document.getElementById('feedback-star-value').value = '5';
        document.getElementById('feedback-image-url').value = '';
        document.getElementById('feedback-image-file').value = '';

        setSelectedStars(5);

        document.getElementById('feedback-image-preview').innerHTML = `<span class="preview-placeholder">Xem trước ảnh tại đây</span>`;
        document.getElementById('feedback-form-title').innerText = 'Để lại đánh giá của bạn';
        document.getElementById('btn-cancel-edit').style.display = 'none';
        document.getElementById('btn-submit-feedback').innerHTML = 'Gửi Đánh Giá Ngay <i class="fa-solid fa-paper-plane"></i>';
    }

    // Xử lý gửi biểu mẫu (gửi mới hoặc chỉnh sửa)
    const feedbackForm = document.getElementById('feedback-form');
    feedbackForm.addEventListener('submit', async function(e) {
        e.preventDefault();

        const submitBtn = document.getElementById('btn-submit-feedback');
        const originalText = submitBtn.innerHTML;
        submitBtn.disabled = true;
        submitBtn.innerHTML = '<i class="fa-solid fa-circle-notch fa-spin"></i> Đang tải ảnh & lưu...';

        try {
            const editId = document.getElementById('edit-feedback-id').value;
            const starValue = document.getElementById('feedback-star-value').value;
            const content = document.getElementById('feedback-content').value.trim();

            let imageUrl = document.getElementById('feedback-image-url').value;
            const fileInput = document.getElementById('feedback-image-file');
            const file = fileInput.files[0];

            // 1. Tải ảnh lên Cloudinary nếu có chọn ảnh mới
            if (file) {
                try {
                    imageUrl = await uploadImageToCloudinary(file);
                } catch (err) {
                    await showSweetAlert('Lỗi từ Cloudinary: ' + err.message, false);
                    return;
                }
            }

            let response;
            if (editId) {
                // Khớp chính xác với DTO FeedBackEditRequest
                response = await fetch('/api/feedback/edit', {
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({
                        feedBackId: parseInt(editId),
                        content: content,
                        star: parseFloat(starValue),
                        image: imageUrl
                    })
                });
            } else {
                // Khớp chính xác với DTO FeedBackRequest
                if (!productId) {
                    await showSweetAlert('Lỗi: Không xác định được sản phẩm để đánh giá.', false);
                    return;
                }
                response = await fetch('/api/feedback/save', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({
                        productId: parseInt(productId),
                        content: content,
                        star: parseFloat(starValue),
                        image: imageUrl
                    })
                });
            }

            if (!response.ok) throw new Error("Gửi dữ liệu lên Server thất bại!");
            
            const result = await response.json();
            if (result.success) {
                await showSweetAlert(result.message || 'Chúc mừng công chúa đã gửi feedback thành công! ❤️', true);
                resetFeedbackForm();
                await loadFeedbacks();
            } else {
                await showSweetAlert('Có lỗi xảy ra: ' + (result.message || 'Lỗi không xác định'), false);
            }

        } catch (err) {
            console.error(err);
            await showSweetAlert('Lỗi kết nối: ' + err.message, false);
        } finally {
            submitBtn.disabled = false;
            submitBtn.innerHTML = originalText;
        }
    });

    // Khởi động lấy danh sách feedback lần đầu
    loadFeedbacks();
});
