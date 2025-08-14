package com.example.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.backend.entity.Article;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 文章Mapper接口
 */
@Mapper
public interface ArticleMapper extends BaseMapper<Article> {

    @Select({
        "<script>",
        "SELECT a.* FROM article a",
        "JOIN article_heat h ON h.article_id = a.id",
        "WHERE a.status = 1",
        "<if test=\"categoryId != null and categoryId != ''\">",
        "  AND a.category_id = #{categoryId}",
        "</if>",
        "<if test=\"keyword != null and keyword != ''\">",
        "  AND (a.title LIKE CONCAT('%', #{keyword}, '%') OR a.summary LIKE CONCAT('%', #{keyword}, '%'))",
        "</if>",
        "ORDER BY a.is_top DESC, h.hot_score DESC, h.publish_time DESC",
        "LIMIT #{offset}, #{size}",
        "</script>"
    })
    java.util.List<Article> selectByFiltersOrderByHeat(@Param("categoryId") String categoryId,
                                                       @Param("keyword") String keyword,
                                                       @Param("offset") long offset,
                                                       @Param("size") long size);

    @Select({
        "<script>",
        "SELECT COUNT(1) FROM article a",
        "JOIN article_heat h ON h.article_id = a.id",
        "WHERE a.status = 1",
        "<if test=\"categoryId != null and categoryId != ''\">",
        "  AND a.category_id = #{categoryId}",
        "</if>",
        "<if test=\"keyword != null and keyword != ''\">",
        "  AND (a.title LIKE CONCAT('%', #{keyword}, '%') OR a.summary LIKE CONCAT('%', #{keyword}, '%'))",
        "</if>",
        "</script>"
    })
    long countByFilters(@Param("categoryId") String categoryId,
                        @Param("keyword") String keyword);
} 