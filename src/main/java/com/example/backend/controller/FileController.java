package com.example.backend.controller;

import com.example.backend.common.Result;
import com.example.backend.entity.ImageStorage;
import com.example.backend.entity.FileStorage;
import com.example.backend.vo.FileUploadVO;
import com.example.backend.vo.ImageUploadVO;
import com.example.backend.service.FileUploadService;
import com.example.backend.service.FileStorageService;
import com.example.backend.service.ImageStorageService;
import com.example.backend.service.UserAccountService;
import com.example.backend.util.JwtUtil;
import com.example.backend.util.RateLimit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * 文件上传控制器
 */
@Api(tags = "文件上传管理")
@RestController
@RequestMapping("/file")
@CrossOrigin
public class FileController {
    
    @Autowired
    private FileUploadService fileUploadService;
    
    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private ImageStorageService imageStorageService;

    @Autowired
    private UserAccountService userAccountService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 上传图片文件
     */
    @ApiOperation(value = "上传图片文件并入库", notes = "支持JPG、PNG、GIF格式，最大2MB；返回imageId、fileId、url\n\n使用说明：multipart/form-data，字段：file、folder(可选)、usageType(可选)、altText(可选)")
    @PostMapping(value = "/upload/image")
    @RateLimit(capacity = 30, ratePerSecond = 3.0, key = "#{ip}:/file/upload/image")
    public Result<ImageUploadVO> uploadImage(@RequestParam("file") MultipartFile file,
                                      @RequestParam(value = "folder", required = false) String folder,
                                      @RequestParam(value = "usageType", required = false) Integer usageType,
                                      @RequestParam(value = "altText", required = false) String altText,
                                      HttpServletRequest request) {
        if (folder == null || folder.trim().isEmpty()) {
            folder = "images";
        }
        if (usageType == null) {
            usageType = 0; // 通用
        }
        try {
            // 验证文件类型
            String[] allowedTypes = {"image/jpeg", "image/png", "image/gif"};
            if (!fileUploadService.isValidFileType(file, allowedTypes)) {
                return Result.error("只支持 JPG、PNG、GIF 格式的图片");
            }
            
            // 验证文件大小（2MB）
            long maxSize = 2 * 1024 * 1024;
            if (!fileUploadService.isValidFileSize(file, maxSize)) {
                return Result.error("文件大小不能超过2MB");
            }

            // 解析用户ID（可为空）
            String uploadUserId = null;
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                uploadUserId = jwtUtil.getUserIdFromToken(token);
            }

            // 上传并入库（文件记录 + 图片记录）
            ImageStorage imageStorage = imageStorageService.uploadImageAndSave(
                    file, folder, usageType, uploadUserId, altText
            );

            ImageUploadVO vo = new ImageUploadVO();
            vo.setImageId(imageStorage.getId());
            vo.setFileId(imageStorage.getFileId());
            vo.setUrl(fileStorageService.getFileUrl(imageStorage.getFileId()));
            
            // 记录操作日志
            if (uploadUserId != null) {
                userAccountService.recordUserLog(uploadUserId, "上传图片", "用户上传了图片文件：" + file.getOriginalFilename(), getClientIpAddress(request));
            }

            return Result.success(vo);
        } catch (Exception e) {
            return Result.error("图片上传失败：" + e.getMessage());
        }
    }
    
    /**
     * 上传文档文件
     */
    @ApiOperation(value = "上传文档文件并入库", notes = "支持PDF、DOC、DOCX、TXT格式，最大10MB；返回fileId、url\n\n使用说明：multipart/form-data，字段：file、folder(可选)、usageType(可选)")
    @PostMapping(value = "/upload/document")
    @RateLimit(capacity = 30, ratePerSecond = 2.0, key = "#{ip}:/file/upload/document")
    public Result<FileUploadVO> uploadDocument(@RequestParam("file") MultipartFile file,
                                         @RequestParam(value = "folder", required = false) String folder,
                                         @RequestParam(value = "usageType", required = false) Integer usageType,
                                         HttpServletRequest request) {
        if (folder == null || folder.trim().isEmpty()) {
            folder = "documents";
        }
        if (usageType == null) {
            usageType = 0; // 通用
        }
        try {
            // 验证文件类型
            String[] allowedTypes = {"application/pdf", "application/msword", 
                                   "application/vnd.openxmlformats-officedocument.wordprocessingml.document", 
                                   "text/plain"};
            if (!fileUploadService.isValidFileType(file, allowedTypes)) {
                return Result.error("只支持 PDF、DOC、DOCX、TXT 格式的文档");
            }
            
            // 验证文件大小（10MB）
            long maxSize = 10 * 1024 * 1024;
            if (!fileUploadService.isValidFileSize(file, maxSize)) {
                return Result.error("文件大小不能超过10MB");
            }

            // 解析用户ID（可为空）
            String uploadUserId = null;
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                uploadUserId = jwtUtil.getUserIdFromToken(token);
            }

            // 上传并入库（文件记录）
            FileStorage saved = fileStorageService.uploadAndSave(file, folder, usageType, uploadUserId);

            FileUploadVO vo = new FileUploadVO();
            vo.setFileId(saved.getId());
            vo.setUrl(saved.getFileUrl());
            
            // 记录操作日志
            if (uploadUserId != null) {
                userAccountService.recordUserLog(uploadUserId, "上传文档", "用户上传了文档文件：" + file.getOriginalFilename(), getClientIpAddress(request));
            }

            return Result.success(vo);
        } catch (Exception e) {
            return Result.error("文档上传失败：" + e.getMessage());
        }
    }
    
    /**
     * 删除文件
     */
    @ApiOperation(value = "删除文件(按URL)")
    @DeleteMapping("/delete")
    @RateLimit(capacity = 40, ratePerSecond = 4.0, key = "#{ip}:/file/delete")
    public Result<Void> deleteFile(@ApiParam("文件URL") @RequestParam String fileUrl,
                                   HttpServletRequest request) {
        try {
            boolean success = fileUploadService.deleteFile(fileUrl);
            
            if (success) {
                // 记录操作日志
                String token = request.getHeader("Authorization");
                if (token != null && token.startsWith("Bearer ")) {
                    token = token.substring(7);
                    String userId = jwtUtil.getUserIdFromToken(token);
                    userAccountService.recordUserLog(userId, "删除文件", "用户删除了文件：" + fileUrl, getClientIpAddress(request));
                }
                return Result.success();
            } else {
                return Result.error("删除失败");
            }
        } catch (Exception e) {
            return Result.error("删除文件失败：" + e.getMessage());
        }
    }

    @ApiOperation(value = "删除文件(按ID，含数据库记录)")
    @DeleteMapping("/delete-by-id")
    @RateLimit(capacity = 40, ratePerSecond = 4.0, key = "#{ip}:/file/delete-by-id")
    public Result<Void> deleteFileById(@ApiParam("文件ID") @RequestParam String fileId,
                                   HttpServletRequest request) {
        try {
            boolean success = fileStorageService.deleteFile(fileId);
            
            if (success) {
                String token = request.getHeader("Authorization");
                if (token != null && token.startsWith("Bearer ")) {
                    token = token.substring(7);
                    String userId = jwtUtil.getUserIdFromToken(token);
                    userAccountService.recordUserLog(userId, "删除文件(按ID)", "用户删除了文件：" + fileId, getClientIpAddress(request));
                }
                return Result.success();
            } else {
                return Result.error("删除失败");
            }
        } catch (Exception e) {
            return Result.error("删除文件失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取客户端IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}
 
 
 