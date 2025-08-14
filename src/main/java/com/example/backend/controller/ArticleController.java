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
            return success ? Result.success() : Result.error("已点赞");
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
            return success ? Result.success() : Result.error("未点赞");
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
            return success ? Result.success() : Result.error("已收藏");
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
            return success ? Result.success() : Result.error("未收藏");
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
        return vo;
    }
}


