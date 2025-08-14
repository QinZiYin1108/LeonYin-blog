package com.example.backend.controller;

import com.example.backend.common.Result;
import com.example.backend.entity.Category;
import com.example.backend.vo.CategoryVO;
import com.example.backend.util.RateLimit;
import com.example.backend.service.CategoryService;
import com.example.backend.service.ImageStorageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "文章分类-前台")
@RestController
@RequestMapping("/category")
@CrossOrigin
public class CategoryController {

	@Autowired
	private CategoryService categoryService;

    @Autowired
    private ImageStorageService imageStorageService;

	@ApiOperation("获取启用的分类（用于前台展示）")
    @GetMapping("/enabled")
    @RateLimit(capacity = 120, ratePerSecond = 25.0, key = "#{ip}:/category/enabled")
    public Result<List<CategoryVO>> enabled() {
		try {
            List<Category> list = categoryService.getEnabledCategories();
            java.util.List<CategoryVO> vos = new java.util.ArrayList<>();
            for (Category c : list) {
                vos.add(toVO(c, null));
            }
            return Result.success(vos);
		} catch (Exception e) {
			return Result.error(e.getMessage());
		}
	}

    @ApiOperation("按ID获取单个分类")
    @GetMapping("/{categoryId}")
    @RateLimit(capacity = 120, ratePerSecond = 25.0, key = "#{ip}:/category/get")
    public Result<CategoryVO> getById(@ApiParam("分类ID") @PathVariable String categoryId) {
		try {
			Category category = categoryService.getById(categoryId);
            return category == null ? Result.error("分类不存在") : Result.success(toVO(category, null));
		} catch (Exception e) {
			return Result.error(e.getMessage());
		}
	}

    private CategoryVO toVO(Category c, Integer articleCount) {
        CategoryVO vo = new CategoryVO();
        vo.setId(c.getId());
        vo.setName(c.getName());
        vo.setDescription(c.getDescription());
        vo.setIconImageId(c.getIconImageId());
        try {
            if (c.getIconImageId() != null && !c.getIconImageId().trim().isEmpty()) {
                String url = imageStorageService.getImageUrl(c.getIconImageId(), null);
                // CategoryVO 建议包含 iconUrl 字段
                try {
                    java.lang.reflect.Method m = vo.getClass().getMethod("setIconUrl", String.class);
                    m.invoke(vo, url);
                } catch (Exception ignore) { /* 如果无该方法则忽略 */ }
            }
        } catch (Exception ignore) {}
        vo.setSortOrder(c.getSortOrder());
        vo.setStatus(c.getStatus());
        vo.setArticleCount(articleCount);
        return vo;
    }
}


