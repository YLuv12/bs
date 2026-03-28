package com.bs.tourism.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/uploads")
public class FileController {

    private static final String UPLOAD_DIR = "F:/bs/uploads";

    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        try {
            Path uploadDir = Paths.get(UPLOAD_DIR).toAbsolutePath().normalize();
            Path file = uploadDir.resolve(filename);
            
            System.out.println("========== File Request ==========");
            System.out.println("Filename: " + filename);
            System.out.println("Upload directory: " + uploadDir.toString());
            System.out.println("File path: " + file.toString());
            System.out.println("File exists: " + Files.exists(file));
            System.out.println("File is readable: " + Files.isReadable(file));
            System.out.println("==================================");
            
            if (!Files.exists(file)) {
                System.out.println("ERROR: File not found at: " + file.toString());
                return ResponseEntity.notFound().build();
            }
            
            Resource resource = new UrlResource(file.toUri());
            
            if (resource.exists() && resource.isReadable()) {
                String contentType = "image/jpeg";
                String lowerFilename = filename.toLowerCase();
                if (lowerFilename.endsWith(".png")) {
                    contentType = "image/png";
                } else if (lowerFilename.endsWith(".gif")) {
                    contentType = "image/gif";
                } else if (lowerFilename.endsWith(".webp")) {
                    contentType = "image/webp";
                }
                
                System.out.println("SUCCESS: Serving file with content type: " + contentType);
                
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header("Cache-Control", "no-cache")
                        .header("Access-Control-Allow-Origin", "*")
                        .body(resource);
            } else {
                System.out.println("ERROR: File not readable");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            System.out.println("ERROR: Exception while serving file: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
