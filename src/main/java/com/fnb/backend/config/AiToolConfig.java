package com.fnb.backend.config;

import com.fnb.backend.dto.Request.*;
import com.fnb.backend.dto.Response.ApiResponse;
import com.fnb.backend.service.FavouriteService;
import com.fnb.backend.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Configuration
@AllArgsConstructor
public class AiToolConfig {
    private final FavouriteService favouriteService;
    private final OrderService orderService;

    @Bean
    @Description("""
    Gọi hàm này NGAY LẬP TỨC (không hỏi lại người dùng) khi phát hiện bất kỳ tín hiệu nào \
    cho thấy người dùng MUỐN hoặc THÈM một món ăn/đồ uống KHÔNG CÓ trong menu hiện tại.
    
    ✅ CÁC TRƯỜNG HỢP BẮT BUỘC GỌI HÀM NÀY:
    - Người dùng hỏi trực tiếp: "có [món X] không?", "cho tôi [món X]"
    - Người dùng gợi ý hoặc đề xuất: "sao không có [món X] nhỉ", "ước gì có [món X]"
    - Người dùng bày tỏ cảm xúc thèm muốn: "thèm [món X] quá", "muốn ăn [món X]"
    - Người dùng so sánh thiếu hụt: "chỗ khác có [món X], sao đây không có?"
    
    ❌ KHÔNG gọi hàm này khi:
    - Món đó ĐÃ CÓ trong menu (dù đang hết hàng tạm thời)
    - Người dùng chỉ đang hỏi thông tin chung, không có ý muốn đặt món đó
    
    📌 LƯU Ý KHI ĐIỀN THAM SỐ:
    - `product`: tên món cụ thể, viết đầy đủ (ví dụ: "trà sữa trân châu đường đen", không viết tắt)
    - `note`: ghi lại nguyên văn cảm xúc/ngữ cảnh của người dùng nếu có (ví dụ: "khách thèm vào buổi tối")
    - `type`: phân loại FOOD hoặc DRINK
    
    Sau khi gọi hàm thành công, hãy xác nhận với người dùng rằng nguyện vọng đã được ghi lại \
    và sẽ được cân nhắc bổ sung vào menu.""")
    public Function<AiFavouriteRequest, String> createNewFavouriteRequest() {
        return request -> {
            try {
                FavouriteRequestRequest favouriteRequest =  new FavouriteRequestRequest();
                favouriteRequest.setProduct(request.getProduct());
                favouriteRequest.setNote(request.getNote());
                favouriteRequest.setType(request.getType());
                ApiResponse response = favouriteService.saveFavouriteRequest(favouriteRequest);
                return response.getMessage();
            }
            catch (Exception e) {
                return "Hệ thống gặp lỗi khi tạo yêu cầu: " + e.getMessage() + ". Hãy báo tới công chúa thử lại sau.";
            }
        };
    }

    @Bean
    @Description("""
    Gọi hàm này NGAY LẬP TỨC khi phát hiện tín hiệu người dùng CHỐT ĐƠN, MUA hoặc \
    YÊU CẦU ĐẶT một hay nhiều món ăn/đồ uống ĐÃ CÓ trong menu hiện tại.
    ⚠️ QUY TRÌNH BẮT BUỘC TRƯỚC KHI GỌI HÀM (RẤT QUAN TRỌNG):
        Nếu người dùng đặt món nhưng CHƯA dặn dò gì thêm về yêu cầu đặc biệt (note), bạn KHÔNG ĐƯỢC gọi hàm này ngay.
        Thay vào đó, hãy giữ vai trò chủ tiệm, hỏi lại công chúa thật ngọt ngào. Ví dụ: "Người yêu của anh có muốn dặn anh làm ít ngọt, cắt đôi, hay hâm nóng bánh không?".
        Bạn CHỈ ĐƯỢC GỌI HÀM NÀY SAU KHI người dùng đã trả lời câu hỏi đó (dù người dùng đưa thêm ghi chú hay bảo là không cần).
        Nếu người dùng không cần thêm ghi chú gì nữa thì cứ gọi hàm với `note` là trống, anh vẫn sẽ chuẩn bị món ngon nhất cho em! Nhưng nếu người dùng có yêu cầu đặc biệt, hãy điền vào `note` để anh chuẩn bị đúng ý em nhé!
        ✅ CÁC TRƯỜNG HỢP GỌI HÀM (Sau khi đã đủ thông tin):
        - Yêu cầu mua trực tiếp đã kèm ghi chú: "cho em đặt 1 bánh bao ít béo"
        - Chốt đơn sau khi bạn đã hỏi thêm: "thế không cần hâm nóng đâu anh", "cho em ít ngọt nhé"
    ✅ CÁC TRƯỜNG HỢP BẮT BUỘC GỌI HÀM NÀY:
    - Yêu cầu mua trực tiếp: "cho em đặt [món X]", "lấy anh 2 [món Y]", "ship cho em [món Z]"
    - Chốt đơn sau khi được tư vấn: "vậy lấy món đó đi", "ok chốt món này", "lấy cho em cái bánh bao đó"
    - Gọi món kèm yêu cầu đặc biệt: "cho 1 [món X] ít ngọt nhé", "lấy 2 [món Y] không cay"

    ❌ KHÔNG gọi hàm này khi:
    - Món ăn/đồ uống KHÔNG CÓ TRONG MENU (nếu khách đòi món không có, phải dùng hàm khác để lưu vào danh sách yêu thích)
    - Người dùng chỉ đang hỏi thông tin (hỏi giá, hỏi thành phần, hỏi calo) hoặc nhờ tư vấn chứ chưa quyết định mua
    - Người dùng còn đang phân vân: "để em suy nghĩ đã", "hay là ăn món khác nhỉ"

    📌 LƯU Ý KHI ĐIỀN THAM SỐ (Hỗ trợ danh sách nhiều món cùng lúc):
    - `nameOfFood`: Tên món ăn/đồ uống chính xác dựa theo menu đang có.
    - `quantity`: Số lượng khách đặt. Nếu khách nói mua mà không nhắc tới số lượng cụ thể, mặc định điền là 1.
    - `note`: Ghi chú yêu cầu đặc biệt của khách cho món đó (ví dụ: "ít béo", "nhiều sốt", "giao nóng"). Nếu không có thì để trống.
    
    Sau khi gọi hàm thành công, hãy dùng giọng điệu ngọt ngào xác nhận lại với công chúa \
    rằng đơn hàng đã được chốt thành công và anh đang tự tay chuẩn bị món ngon cho em!
    """)
    public Function<AiOrderRequestWrapper, String> createNewOrderRequest() {
        return requestsWrapper -> {
            List<AiOrderRequest> requests = requestsWrapper.getOrders();
            try {
                List<OrderRequestDTO> orderRequests = new ArrayList<>();
                requests.forEach(request -> {
                    OrderRequestDTO orderRequest = new OrderRequestDTO();
                    orderRequest.setNameOfFood(request.getNameOfFood());
                    orderRequest.setQuantity(request.getQuantity());
                    orderRequest.setNote(request.getNote());
                    orderRequests.add(orderRequest);
                });
                return orderService.createOrders(orderRequests).getMessage();
            }
            catch (Exception e) {
                return "Hệ thống gặp lỗi khi tạo đơn hàng: " + e.getMessage() + ". Hãy báo tới công chúa thử lại sau.";
            }
        };
    }
}
