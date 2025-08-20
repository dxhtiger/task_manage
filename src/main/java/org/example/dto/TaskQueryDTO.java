package org.example.dto;

public class TaskQueryDTO {
    private Integer pageNum = 1;      // 默认第1页
    private Integer pageSize = 10;    // 默认每页10条
    private Integer status;           // 过滤：0/1/2
    private Integer priority;         // 过滤：1/2/3
    private String keyword;           // 模糊匹配 title/description
    private String orderBy;           // 可选：deadline_desc / deadline_asc

    public Integer getPageNum() { return pageNum; }
    public void setPageNum(Integer pageNum) { this.pageNum = pageNum; }
    public Integer getPageSize() { return pageSize; }
    public void setPageSize(Integer pageSize) { this.pageSize = pageSize; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }
    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
    public String getOrderBy() { return orderBy; }
    public void setOrderBy(String orderBy) { this.orderBy = orderBy; }
}
