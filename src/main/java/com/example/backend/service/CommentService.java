package com.example.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.backend.common.PageResult;
import com.example.backend.entity.Comment;

import java.util.List;

/**
 * 评论服务接口
 */
public interface CommentService extends IService<Comment> {
    
    /**
     * 发表评论
     */
    Comment publishComment(Comment comment);
    
    /**
     * 回复评论
     */
    Comment replyComment(String parentId, Comment comment);
    
    /**
     * 获取文章评论列表
     */
    PageResult<Comment> getArticleComments(String articleId, Integer current, Integer size);
    
    /**
     * 获取评论的回复列表
     */
    List<Comment> getCommentReplies(String parentId);
    
    /**
     * 点赞评论
     */
    boolean likeComment(String commentId, String userId);
    
    /**
     * 取消点赞评论
     */
    boolean unlikeComment(String commentId, String userId);
    
    /**
     * 删除评论
     */
    boolean deleteComment(String commentId, String userId);
    
    /**
     * 管理员删除评论
     */
    boolean adminDeleteComment(String commentId);
    
    /**
     * 审核评论
     */
    boolean auditComment(String commentId, Integer status);
} 