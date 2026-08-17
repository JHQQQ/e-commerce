package com.mitsuha754.ecommerce.util;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ObjectMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * 阿里云OSS工具类
 * 功能：上传图片、删除图片、直接返回可访问的URL
 * 配合Bucket 公共读权限使用
 */
@Component
public class OssUtil {

    // 从配置文件读取阿里云OSS信息
    @Value("${aliyun.oss.endpoint}")
    private String endpoint;

    @Value("${aliyun.oss.accessKeyId}")
    private String accessKeyId;

    @Value("${aliyun.oss.accessKeySecret}")
    private String accessKeySecret;

    @Value("${aliyun.oss.bucketName}")
    private String bucketName;

    @Value("${aliyun.oss.urlPrefix}")
    private String urlPrefix;

    /**
     * 上传图片到OSS
     * @param file 上传的文件
     * @param folder 上传到哪个文件夹，例：product/
     * @return 可直接访问的图片URL
     */
    public String upload(MultipartFile file, String folder) {
        // 1. 获取原始文件名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new RuntimeException("上传失败：文件名为空");
        }

        // 2. 生成唯一文件名，防止重复
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = folder + UUID.randomUUID() + suffix;

        // 3. 上传文件到OSS
        try (InputStream inputStream = file.getInputStream()) {
            // 创建OSS客户端
            OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

            // 设置文件类型（浏览器能直接预览图片）
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(getContentType(originalFilename));

            // 执行上传
            ossClient.putObject(bucketName, fileName, inputStream, metadata);

            // 关闭客户端
            ossClient.shutdown();
        } catch (IOException e) {
            throw new RuntimeException("OSS上传图片失败", e);
        }

        // 4. 返回可直接访问的图片URL
        return urlPrefix + fileName;
    }

    /**
     * 根据图片URL删除OSS文件
     * @param fileUrl 图片完整URL
     */
    public void deleteByUrl(String fileUrl) {
        if (fileUrl == null || !fileUrl.startsWith(urlPrefix)) {
            return;
        }

        // 从URL中截取文件路径
        String fileName = fileUrl.replace(urlPrefix, "");

        // 创建OSS客户端并删除
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        ossClient.deleteObject(bucketName, fileName);
        ossClient.shutdown();
    }

    /**
     * 根据文件后缀返回对应的ContentType（让浏览器正常显示图片）
     */
    private String getContentType(String fileName) {
        String suffix = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
        return switch (suffix) {
            case ".png" -> "image/png";
            case ".jpg", ".jpeg" -> "image/jpeg";
            case ".gif" -> "image/gif";
            case ".webp" -> "image/webp";
            default -> "application/octet-stream";
        };
    }
}