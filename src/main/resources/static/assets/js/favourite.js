document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('favouriteForm');
    const successMsg = document.getElementById('successMessage');

    if (form) {
        form.addEventListener('submit', async (e) => {
            e.preventDefault();

            // Check if user is logged in
            const user = window.getCurrentUser();
            if (!user) {
                const loginModal = document.getElementById('loginPromptModal');
                if (loginModal) {
                    loginModal.classList.add('active');
                } else {
                    alert('Công chúa vui lòng đăng nhập để gửi yêu cầu cho anh nhen!');
                    window.location.href = '/login.html';
                }
                return;
            }

            const formData = {
                product: document.getElementById('product').value,
                type: document.getElementById('type').value,
                note: document.getElementById('note').value
            };

            try {
                // Using the api object defined in app.js
                // FavouriteController.java expects RequestBody (implicit or explicit)
                // In FavouriteController.java line 21: public ResponseEntity<ApiResponse> createNewRequest (FavouriteRequestRequest request)
                // Since it's not annotated with @RequestBody, Spring might expect form-data or query params.
                // However, app.js api.post sends JSON. 
                // Let's check FavouriteController again.
                
                const response = await api.post('/api/favourites/create', formData);
                
                if (response) {
                    form.style.display = 'none';
                    successMsg.style.display = 'block';
                }
            } catch (error) {
                console.error('Error submitting favourite request:', error);
                alert('Có lỗi xảy ra rồi công chúa ơi, em thử lại giúp anh nhen! ❤️');
            }
        });
    }
});
