package com.example.hrm.modules.user.controller;

import lombok.Data;

@Data
public class LoginDTO {
    private String username;
    private String password;
    private String captcha;
    private String captchaKey;
}