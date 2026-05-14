package com.fnb.backend.utils;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class SecurityUtils {

    public static Long getCurrentUserId() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession(false);

        if (session != null) {
            Object userId = session.getAttribute("UserID");
            return (userId != null) ? (Long) userId : null;
        }
        return null;
    }

    // Kiểm tra xem đã đăng nhập chưa
    public static boolean isAuthenticated() {
        return getCurrentUserId() != null;
    }
}