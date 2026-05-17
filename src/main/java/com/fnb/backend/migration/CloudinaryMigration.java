package com.fnb.backend.migration;


import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.fnb.backend.entity.Product;
import com.fnb.backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.io.File;
import java.util.List;
import java.util.Map;

@Component
public class CloudinaryMigration implements CommandLineRunner {

    @Autowired
    private ProductRepository productRepository;

    @Value("${cloudinary.cloud-name}")
    private String cloudName;

    @Value("${cloudinary.api-key}")
    private String apiKey;

    @Value("${cloudinary.api-secret}")
    private String apiSecret;

    // Cấu hình thông tin Cloudinary (Lấy API Key và Secret trong Dashboard Cloudinary của bạn)
    private final Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
            "cloud_name", cloudName,
            "api_key", apiKey,
            "api_secret", apiSecret
    ));

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== BẮT ĐẦU MIGRATION ẢNH LÊN CLOUDINARY ===");

        // 1. Lấy toàn bộ sản phẩm từ DB
        List<Product> products = productRepository.findAll();

        for (Product product : products) {
            String currentUrl = product.getImageUrl();

            // 2. Chỉ xử lý các sản phẩm đang dùng ảnh local cũ
            if (currentUrl != null && currentUrl.startsWith("/assets/img/")) {

                // Đường dẫn trỏ tới file ảnh vật lý trong project của bạn
                String localFilePath = "src/main/resources/static" + currentUrl;
                File file = new File(localFilePath);

                if (file.exists()) {
                    try {
                        System.out.println("Đang upload: " + file.getName());

                        // 3. Đẩy file lên Cloudinary
                        Map uploadResult = cloudinary.uploader().upload(file, ObjectUtils.emptyMap());
                        String newCloudinaryUrl = (String) uploadResult.get("secure_url");

                        // 4. Cập nhật lại URL mới vào sản phẩm và lưu xuống DB
                        product.setImageUrl(newCloudinaryUrl);
                        productRepository.save(product);

                        System.out.println("-> Thành công! URL mới: " + newCloudinaryUrl);
                    } catch (Exception e) {
                        System.err.println("-> Lỗi khi xử lý file " + file.getName() + ": " + e.getMessage());
                    }
                } else {
                    System.err.println("-> Không tìm thấy file cục bộ tại: " + localFilePath);
                }
            }
        }
        System.out.println("=== MIGRATION HOÀN TẤT ===");
    }
}