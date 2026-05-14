package com.fnb.backend.service;

import com.fnb.backend.dto.UserSessionDTO;
import com.fnb.backend.entity.Users;
import com.fnb.backend.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;

@Service
public class UserService {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public UserSessionDTO login(@RequestParam("phone_number") String phone_number, @RequestParam("password") String password){
        Users user = usersRepository.findByPhoneNumber(phone_number);
        if(user == null){
            throw new RuntimeException("Tên Đăng Nhập Hoặc Mật Khẩu Không Chính Xác");
        }
        else{
            if(!passwordEncoder.matches(password,user.getPassword())){
                throw new RuntimeException("Tên Đăng Nhập Hoặc Mật Khẩu Không Chính Xác");
            }
        }
        return new UserSessionDTO(user.getUserId(), user.getUsername(), user.getRole());
    }
}
