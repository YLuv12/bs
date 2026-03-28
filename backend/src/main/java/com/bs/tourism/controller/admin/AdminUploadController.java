package com.bs.tourism.controller.admin;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/admin/upload")
public class AdminUploadController {

    private static final String UPLOAD_DIR = "F:/bs/uploads";
    private static final String URL_PREFIX = "/api/uploads/";

    @PostMapping("/image")
    public Map<String, Object> uploadImage(@RequestParam("file") MultipartFile file) {
        Map<String, Object> result = new HashMap<>();
        
        if (file.isEmpty()) {
            result.put("code", 400);
            result.put("msg", "请选择要上传的文件");
            return result;
        }

        try {
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            
            String newFilename = UUID.randomUUID().toString() + extension;
            
            Path uploadDir = Paths.get(UPLOAD_DIR).toAbsolutePath().normalize();
            System.out.println("Admin Upload - Directory: " + uploadDir.toString());
            
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
            
            Path filePath = uploadDir.resolve(newFilename);
            Files.write(filePath, file.getBytes());
            
            String fileUrl = URL_PREFIX + newFilename;
            
            System.out.println("Admin Upload - File saved: " + filePath.toString());
            System.out.println("Admin Upload - URL: " + fileUrl);
            
            result.put("code", 200);
            result.put("msg", "上传成功");
            result.put("data", Map.of(
                "url", fileUrl,
                "filename", newFilename,
                "originalFilename", originalFilename,
                "size", file.getSize()
            ));
            
        } catch (IOException e) {
            e.printStackTrace();
            result.put("code", 500);
            result.put("msg", "上传失败: " + e.getMessage());
        }
        
        return result;
    }
}
