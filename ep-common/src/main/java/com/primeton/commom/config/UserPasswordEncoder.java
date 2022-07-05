package com.primeton.commom.config;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

@Component
public class UserPasswordEncoder implements PasswordEncoder  {

        @Override
        public String encode(CharSequence rawPassword) {//haiyu
            return DigestUtils.md5DigestAsHex(rawPassword.toString().getBytes());
        }

        @Override
        public boolean matches(CharSequence rawPassword, String encodedPassword) {
            return encodedPassword.equals(DigestUtils.md5DigestAsHex(rawPassword.toString().getBytes()));
        }

}
