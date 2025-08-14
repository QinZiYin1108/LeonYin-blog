package com.example.backend.common;

import java.util.List;

/**
 * 分页结果类
 */
public class PageResult<T> {
    
    private List<T> records;
    private Long total;
    private Long current;
    private Long size;
    private Long pages;
    
    public PageResult() {}
    
    public PageResult(List<T> records, Long total, Long current, Long size) {
        this.records = records;
        this.total = total;
        this.current = current;
        this.size = size;
        this.pages = (total + size - 1) / size;
    }
    
    public static <T> PageResult<T> of(List<T> records, Long total, Long current, Long size) {
        return new PageResult<>(records, total, current, size);
    }
    
    public static <T> PageResult<T> success(List<T> records, Long total, Integer current, Integer size) {
        return new PageResult<>(records, total, current.longValue(), size.longValue());
    }
    
    // Getters and Setters
    public List<T> getRecords() {
        return records;
    }
    
    public void setRecords(List<T> records) {
        this.records = records;
    }
    
    public Long getTotal() {
        return total;
    }
    
    public void setTotal(Long total) {
        this.total = total;
    }
    
    public Long getCurrent() {
        return current;
    }
    
    public void setCurrent(Long current) {
        this.current = current;
    }
    
    public Long getSize() {
        return size;
    }
    
    public void setSize(Long size) {
        this.size = size;
    }
    
    public Long getPages() {
        return pages;
    }
    
    public void setPages(Long pages) {
        this.pages = pages;
    }
} 