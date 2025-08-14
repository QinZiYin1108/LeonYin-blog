package com.example.backend.controller;

import com.example.backend.common.Result;
import com.example.backend.dto.CategoryCreateRequest;
import com.example.backend.entity.Category;
import com.example.backend.service.FileStorageService;
import com.example.backend.service.ImageStorageService;
import com.example.backend.vo.CategoryVO;
import com.example.backend.service.CategoryService;
import com.example.backend.util.RateLimit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "管理员-文章分类管理")
@RestController
@RequestMapping("/admin/category")
@CrossOrigin
public class AdminCategoryController {

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private FileStorageService fileStorageService;

	@Autowired
	private ImageStorageService imageStorageService;

	@ApiOperation("分类列表（按权重和创建时间排序）")
    @GetMapping("/list")
    @RateLimit(capacity = 60, ratePerSecond = 10.0, key = "#{ip}:/admin/category/list")
	public Result<List<CategoryVO>> list() {
		try {
			List<Category> list = categoryService.getCategoriesBySort();
			List<CategoryVO> vos = new java.util.ArrayList<>();
			for (Category c : list) {
				CategoryVO vo = new CategoryVO();
				vo.setId(c.getId());
				vo.setName(c.getName());
				vo.setDescription(c.getDescription());
				vo.setIconImageId(c.getIconImageId());
				vo.setSortOrder(c.getSortOrder());
				vo.setStatus(c.getStatus());
				vo.setCreateTime(c.getCreateTime());
				vo.setUpdateTime(c.getUpdateTime());
				// 拼接图标URL
				if (c.getIconImageId() != null && !c.getIconImageId().trim().isEmpty()) {
					String url = imageStorageService.getImageUrl(c.getIconImageId(), null);
					vo.setIconUrl(url);
				}
				vos.add(vo);
			}
			return Result.success(vos);
		} catch (Exception e) {
			return Result.error(e.getMessage());
		}
	}

	@ApiOperation("创建分类")
    @PostMapping
    @RateLimit(capacity = 20, ratePerSecond = 2.0, key = "#{ip}:/admin/category/create")
    public Result<Category> create(@ApiParam("分类信息") @RequestBody CategoryCreateRequest req) {
		try {
            Category category = new Category();
            category.setName(req.getName());
            category.setDescription(req.getDescription());
            category.setIconImageId(req.getIconImageId());
            if (req.getSortOrder() != null) category.setSortOrder(req.getSortOrder());
            if (req.getStatus() != null) category.setStatus(req.getStatus());
            Category created = categoryService.createCategory(category);
			return Result.success(created);
		} catch (Exception e) {
			return Result.error(e.getMessage());
		}
	}

	@ApiOperation("更新分类")
    @PutMapping("/{categoryId}")
    @RateLimit(capacity = 20, ratePerSecond = 2.0, key = "#{ip}:/admin/category/update")
    public Result<Void> update(@ApiParam("分类ID") @PathVariable String categoryId,
                              @ApiParam("分类信息") @RequestBody CategoryCreateRequest req) {
		try {
            // 查询旧记录用于比对图标
            Category old = null;
            java.util.List<Category> all = categoryService.getCategoriesBySort();
            if (all != null) {
                for (Category c : all) {
                    if (categoryId.equals(c.getId())) { old = c; break; }
                }
            }
            Category category = new Category();
            category.setName(req.getName());
            category.setDescription(req.getDescription());
            category.setIconImageId(req.getIconImageId());
            category.setSortOrder(req.getSortOrder());
            category.setStatus(req.getStatus());
            boolean ok = categoryService.updateCategory(categoryId, category);
            if (ok && old != null) {
                String oldIcon = old.getIconImageId();
                String newIcon = req.getIconImageId();
                if (oldIcon != null && !oldIcon.trim().isEmpty() && (newIcon == null || !oldIcon.equals(newIcon))) {
                    try { imageStorageService.deleteImageById(oldIcon); } catch (Exception ignore) {}
                }
            }
            return ok ? Result.success() : Result.error("更新失败");
		} catch (Exception e) {
			return Result.error(e.getMessage());
		}
	}

	@ApiOperation("删除分类")
    @DeleteMapping("/{categoryId}")
    @RateLimit(capacity = 20, ratePerSecond = 1.0, key = "#{ip}:/admin/category/delete")
	public Result<Void> delete(@ApiParam("分类ID") @PathVariable String categoryId) {
		try {
            // 先查询分类，取出图标ID
            Category existing = null;
            java.util.List<Category> all = categoryService.getCategoriesBySort();
            if (all != null) {
                for (Category c : all) {
                    if (categoryId.equals(c.getId())) { existing = c; break; }
                }
            }
            boolean ok = categoryService.deleteCategory(categoryId);
            if (ok && existing != null && existing.getIconImageId() != null && !existing.getIconImageId().trim().isEmpty()) {
                try { imageStorageService.deleteImageById(existing.getIconImageId()); } catch (Exception ignore) {}
            }
			return ok ? Result.success() : Result.error("删除失败");
		} catch (Exception e) {
			return Result.error(e.getMessage());
		}
	}

	@ApiOperation("启用/禁用分类（切换状态）")
    @PutMapping("/{categoryId}/toggle")
    @RateLimit(capacity = 30, ratePerSecond = 3.0, key = "#{ip}:/admin/category/toggle")
	public Result<Void> toggle(@ApiParam("分类ID") @PathVariable String categoryId) {
		try {
			boolean ok = categoryService.toggleCategoryStatus(categoryId);
			return ok ? Result.success() : Result.error("操作失败");
		} catch (Exception e) {
			return Result.error(e.getMessage());
		}
	}
}


