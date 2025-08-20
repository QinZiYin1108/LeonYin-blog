package com.example.backend.controller;

import com.example.backend.common.PageResult;
import com.example.backend.common.Result;
import com.example.backend.dto.ArticleCreateRequest;
import com.example.backend.dto.ArticleUpdateRequest;
import com.example.backend.entity.Article;
import com.example.backend.service.ArticleService;
import com.example.backend.util.JwtUtil;
import com.example.backend.util.RateLimit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "管理员-文章管理")
@RestController
@RequestMapping("/admin/article")
@CrossOrigin
public class AdminArticleController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private com.example.backend.service.SystemConfigService systemConfigService;

    @ApiOperation("发布文章")
    @PostMapping
    @RateLimit(capacity = 20, ratePerSecond = 2.0, key = "#{ip}:/admin/article/create")
    public Result<Article> publish(@RequestBody ArticleCreateRequest req, javax.servlet.http.HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            String userId = jwtUtil.getUserIdFromToken(token);
            if (userId == null || userId.trim().isEmpty()) {
                return Result.error("未登录或Token无效");
            }
            Article article = new Article();
            article.setAuthorId(userId);
            article.setTitle(req.getTitle());
            article.setSummary(req.getSummary());
            article.setContent(req.getContent());
            // 封面可选：若未提供，则尝试使用系统配置中的默认封面
            String coverId = req.getCoverImageId();
            if (coverId == null || coverId.trim().isEmpty()) {
                com.example.backend.entity.SystemConfig cfg = systemConfigService.getByKey("default_cover_image_id");
                coverId = (cfg != null) ? cfg.getConfigValue() : null;
            }
            article.setCoverImageId(coverId);
            article.setCategoryId(req.getCategoryId());
            article.setTags(req.getTags());
            return Result.success(articleService.publishArticle(article));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @ApiOperation("更新文章")
    @PutMapping("/{articleId}")
    @RateLimit(capacity = 30, ratePerSecond = 3.0, key = "#{ip}:/admin/article/update")
    public Result<Void> update(@ApiParam("文章ID") @PathVariable String articleId,
                               @RequestBody ArticleUpdateRequest req) {
        try {
            Article article = new Article();
            article.setTitle(req.getTitle());
            article.setSummary(req.getSummary());
            article.setContent(req.getContent());
            // 更新时封面也可为空：为空则保留原值或使用默认
            String coverId2 = req.getCoverImageId();
            if (coverId2 == null || coverId2.trim().isEmpty()) {
                // 不设置字段意味着不修改；若明确需要清空可在前端传递特殊标识，这里暂保持不变
            } else {
                article.setCoverImageId(coverId2);
            }
            article.setCategoryId(req.getCategoryId());
            article.setTags(req.getTags());
            article.setStatus(req.getStatus());
            article.setIsTop(req.getIsTop());
            boolean success = articleService.updateArticle(articleId, article);
            return success ? Result.success() : Result.error("更新失败");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @ApiOperation("删除文章")
    @DeleteMapping("/{articleId}")
    @RateLimit(capacity = 20, ratePerSecond = 1.0, key = "#{ip}:/admin/article/delete")
    public Result<Void> delete(@ApiParam("文章ID") @PathVariable String articleId) {
        try {
            boolean success = articleService.deleteArticle(articleId);
            return success ? Result.success() : Result.error("删除失败");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @ApiOperation("置顶/取消置顶")
    @PutMapping("/{articleId}/top")
    @RateLimit(capacity = 20, ratePerSecond = 2.0, key = "#{ip}:/admin/article/top")
    public Result<Void> toggleTop(@ApiParam("文章ID") @PathVariable String articleId) {
        try {
            boolean success = articleService.toggleTopArticle(articleId);
            return success ? Result.success() : Result.error("操作失败");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}


