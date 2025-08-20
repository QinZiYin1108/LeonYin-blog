package com.example.backend.controller;

import com.example.backend.common.PageResult;
import com.example.backend.common.Result;
import com.example.backend.dto.ArticleCreateRequest;
import com.example.backend.dto.ArticleQueryRequest;
import com.example.backend.vo.ArticleVO;
import com.example.backend.entity.Article;
import com.example.backend.util.RateLimit;
import com.example.backend.service.ArticleService;
import com.example.backend.util.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Api(tags = "文章前台接口")
@RestController
@RequestMapping("/article")
@CrossOrigin
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private com.example.backend.service.ArticleHeatService articleHeatService;

    @Autowired
    private com.example.backend.service.UserProfileService userProfileService;

    @Autowired
    private com.example.backend.service.CategoryService categoryService;

    @Autowired
    private com.example.backend.service.UserAccountService userAccountService;

    @ApiOperation("文章详情（登录浏览计入浏览量）")
    @GetMapping("/{articleId}")
    @RateLimit(capacity = 150, ratePerSecond = 30.0, key = "#{ip}:/article/detail")
    public Result<ArticleVO> detail(@ApiParam("文章ID") @PathVariable String articleId,
                                  HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            String userId = null;
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                userId = jwtUtil.getUserIdFromToken(token);
            }
            Article article = articleService.getArticleDetail(articleId, userId);
            if (article == null) return Result.error("文章不存在或已下架");

            // 合并实时热度
            com.example.backend.entity.ArticleHeat heat = articleHeatService.getRealtimeHeat(articleId);
            ArticleVO vo = toVO(article);
            if (heat != null) {
                vo.setViewCount(heat.getViewCount()==null?0:heat.getViewCount().intValue());
                vo.setLikeCount(heat.getLikeCount()==null?0:heat.getLikeCount().intValue());
                vo.setCollectCount(heat.getCollectCount()==null?0:heat.getCollectCount().intValue());
                vo.setHotScore(heat.getHotScore());
            }

            // 是否已点赞/收藏（登录用户）
            if (userId != null) {
                try {
                    vo.setLiked(articleService.hasLiked(articleId, userId));
                    vo.setCollected(articleService.hasCollected(articleId, userId));
                } catch (Exception ignore) {}
            }
            return Result.success(vo);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @ApiOperation("多条件分页（按热度排序，条件可空）")
    @PostMapping("/page")
    @RateLimit(capacity = 100, ratePerSecond = 20.0, key = "#{ip}:/article/page")
    public Result<PageResult<ArticleVO>> page(@ApiParam("查询请求") @RequestBody ArticleQueryRequest req) {
        try {
            PageResult<Article> page = articleService.pageArticles(
                req.getCurrent(), req.getSize(), req.getCategoryId(), req.getKeyword()
            );
            java.util.List<ArticleVO> vos = new java.util.ArrayList<>();
            if (page.getRecords() != null) {
                for (Article a : page.getRecords()) {
                    ArticleVO vo = toVO(a);
                    // 合并实时热度
                    try {
                        com.example.backend.entity.ArticleHeat heat = articleHeatService.getRealtimeHeat(a.getId());
                        if (heat != null) {
                            vo.setViewCount(heat.getViewCount()==null?0:heat.getViewCount().intValue());
                            vo.setLikeCount(heat.getLikeCount()==null?0:heat.getLikeCount().intValue());
                            vo.setCollectCount(heat.getCollectCount()==null?0:heat.getCollectCount().intValue());
                            vo.setHotScore(heat.getHotScore());
                        }
                    } catch (Exception ignore) {}
                    vos.add(vo);
                }
            }
            return Result.success(new PageResult<>(vos, page.getTotal(), page.getCurrent(), page.getSize()));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @ApiOperation("点赞文章")
    @PostMapping("/{articleId}/like")
    public Result<Void> like(@PathVariable String articleId, HttpServletRequest request) {
        try {
            String userId = extractUserId(request);
            boolean success = articleService.likeArticle(articleId, userId);
            if (success) {
                userAccountService.recordUserLog(userId, "点赞文章", "articleId=" + articleId, getClientIpAddress(request));
                return Result.success();
            }
            return Result.error("已点赞");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @ApiOperation("取消点赞")
    @DeleteMapping("/{articleId}/like")
    public Result<Void> unlike(@PathVariable String articleId, HttpServletRequest request) {
        try {
            String userId = extractUserId(request);
            boolean success = articleService.unlikeArticle(articleId, userId);
            if (success) {
                userAccountService.recordUserLog(userId, "取消点赞", "articleId=" + articleId, getClientIpAddress(request));
                return Result.success();
            }
            return Result.error("未点赞");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @ApiOperation("收藏文章")
    @PostMapping("/{articleId}/collect")
    public Result<Void> collect(@PathVariable String articleId, HttpServletRequest request) {
        try {
            String userId = extractUserId(request);
            boolean success = articleService.collectArticle(articleId, userId);
            if (success) {
                userAccountService.recordUserLog(userId, "收藏文章", "articleId=" + articleId, getClientIpAddress(request));
                return Result.success();
            }
            return Result.error("已收藏");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @ApiOperation("取消收藏")
    @DeleteMapping("/{articleId}/collect")
    public Result<Void> uncollect(@PathVariable String articleId, HttpServletRequest request) {
        try {
            String userId = extractUserId(request);
            boolean success = articleService.uncollectArticle(articleId, userId);
            if (success) {
                userAccountService.recordUserLog(userId, "取消收藏", "articleId=" + articleId, getClientIpAddress(request));
                return Result.success();
            }
            return Result.error("未收藏");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    private String extractUserId(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            return jwtUtil.getUserIdFromToken(token);
        }
        throw new RuntimeException("未登录");
    }

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

    private ArticleVO toVO(Article a) {
        ArticleVO vo = new ArticleVO();
        // 继承Article，直接复制基础字段
        vo.setId(a.getId());
        vo.setTitle(a.getTitle());
        vo.setSummary(a.getSummary());
        vo.setContent(a.getContent());
        vo.setCoverImageId(a.getCoverImageId());
        vo.setAuthorId(a.getAuthorId());
        vo.setCategoryId(a.getCategoryId());
        vo.setTags(a.getTags());
        vo.setViewCount(a.getViewCount());
        vo.setLikeCount(a.getLikeCount());
        vo.setCollectCount(a.getCollectCount());
        vo.setHotScore(a.getHotScore());
        vo.setStatus(a.getStatus());
        vo.setIsTop(a.getIsTop());
        vo.setPublishTime(a.getPublishTime());

        // 作者昵称
        try {
            if (a.getAuthorId() != null) {
                com.example.backend.entity.UserProfile up = userProfileService.getByUserAccountId(a.getAuthorId());
                if (up != null && up.getNickname() != null && up.getNickname().trim().length() > 0) {
                    vo.setAuthorName(up.getNickname());
                } else {
                    vo.setAuthorName("匿名作者");
                }
            }
        } catch (Exception ignore) {}

        // 分类名称
        try {
            if (a.getCategoryId() != null) {
                com.example.backend.entity.Category cat = categoryService.getById(a.getCategoryId());
                if (cat != null) {
                    vo.setCategoryName(cat.getName());
                }
            }
        } catch (Exception ignore) {}

        // 标签列表
        try {
            if (a.getTags() != null && a.getTags().trim().length() > 0) {
                String t = a.getTags().trim();
                java.util.List<String> tagList;
                if (t.startsWith("[") && t.endsWith("]")) {
                    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                    tagList = mapper.readValue(t, mapper.getTypeFactory().constructCollectionType(java.util.List.class, String.class));
                } else {
                    tagList = new java.util.ArrayList<>();
                    for (String s : t.split(",")) {
                        String v = s.trim();
                        if (v.length() > 0) tagList.add(v);
                    }
                }
                vo.setTagList(tagList);
            }
        } catch (Exception ignore) {}
        return vo;
    }
}


