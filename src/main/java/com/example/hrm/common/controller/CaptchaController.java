package com.example.hrm.common.controller;

import com.example.hrm.common.utils.RedisUtils;
import com.example.hrm.common.utils.Result;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/auth")  // ✅ 统一路径
public class CaptchaController {

    @Autowired
    private DefaultKaptcha defaultKaptcha;

    @Autowired
    private RedisUtils redisUtils;

    @GetMapping("/captcha")
    public Result<?> getCaptcha() throws IOException {
        // 1. 生成验证码文本
        String capText = defaultKaptcha.createText();

        // 2. 生成验证码图片
        BufferedImage image = defaultKaptcha.createImage(capText);

        // 3. 图片转 Base64
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);
        byte[] bytes = baos.toByteArray();
        String imageBase64 = Base64.getEncoder().encodeToString(bytes);

        // 4. 生成 key 存入 Redis（60秒过期）
        String key = UUID.randomUUID().toString();
        redisUtils.set("captcha:" + key, capText, 60, TimeUnit.SECONDS);

        // 5. 返回 JSON
        Map<String, Object> result = new HashMap<>();
        result.put("key", key);
        result.put("image", "data:image/jpeg;base64," + imageBase64);

        return Result.success(result);
    }
}