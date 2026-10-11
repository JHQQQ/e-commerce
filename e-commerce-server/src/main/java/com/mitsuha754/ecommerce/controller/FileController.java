package com.mitsuha754.ecommerce.controller;

import com.mitsuha754.ecommerce.exception.BusinessException;
import com.mitsuha754.ecommerce.result.R;
import com.mitsuha754.ecommerce.util.OssUtil;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 通用文件上传（登录后可用，商家上架商品时上传图片）
 */
@RestController
public class FileController {

    /** 图片大小上限：2MB */
    private static final long MAX_IMAGE_SIZE = 2 * 1024 * 1024;

    @Resource
    private OssUtil ossUtil;

    @PostMapping("/upload")
    public R<String> upload(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("请选择要上传的图片");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BusinessException("只能上传图片文件");
        }
        if (file.getSize() > MAX_IMAGE_SIZE) {
            throw new BusinessException("图片大小不能超过 2MB");
        }
        return R.ok(ossUtil.upload(file, "product/"));
    }

}
