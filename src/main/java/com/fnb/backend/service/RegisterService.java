package com.fnb.backend.service;

import com.fnb.backend.dto.RegisterDTO;
import com.fnb.backend.entity.Users;
import com.fnb.backend.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Service
public class RegisterService {

    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public void register(RegisterDTO registerDTO){
        String phone_number = registerDTO.getPhone_number();
        String username = registerDTO.getUsername();
        String email = registerDTO.getEmail();
        String password = registerDTO.getPassword();
        String confirmPassword = registerDTO.getConfirmPassword();
        
        if(usersRepository.findByPhoneNumber(phone_number) != null){
            throw new RuntimeException("Số điện thoại này đã được đăng ký!");
        }
        
        if(!password.equals(confirmPassword)) {
            throw new RuntimeException("Xác nhận mật khẩu không khớp!");
        }

        String encodedPassword = passwordEncoder.encode(password);

        Users user = new Users(username, email, encodedPassword, phone_number);
        user.setRole("USER"); // Default role
        usersRepository.save(user);
    }
}
