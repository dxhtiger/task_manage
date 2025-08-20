package org.example.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.example.dto.TaskCreateDTO;
import org.example.dto.TaskQueryDTO;
import org.example.dto.TaskUpdateDTO;
import org.example.mapper.TaskMapper;
import org.example.pojo.Tasks;
import org.example.service.TaskService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskMapper taskMapper;

    public TaskServiceImpl(TaskMapper taskMapper) {
        this.taskMapper = taskMapper;
    }

    /** 当前登录用户是否管理员 */
    private boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null
                && auth.getAuthorities() != null
                && auth.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
    }

    @Override
    public Long create(Long userId, TaskCreateDTO dto) {
        if (dto == null || dto.getTitle() == null || dto.getTitle().isBlank()) return null;

        Tasks t = new Tasks();
        t.setUserId(userId);
        t.setTitle(dto.getTitle());
        t.setDescription(dto.getDescription());
        t.setPriority(Objects.requireNonNullElse(dto.getPriority(), 2));  // 默认中等优先级
        t.setDeadline(dto.getDeadline());
        t.setStatus(Objects.requireNonNullElse(dto.getStatus(), 0));      // 默认待办

        int n = taskMapper.insert(t);
        System.out.println("插入结果: " + n);
        System.out.println("任务ID: " + t.getId());
        return n > 0 ? t.getId() : null;
    }

    @Override
    public boolean update(Long userId, TaskUpdateDTO dto) {
        if (dto == null || dto.getId() == null) return false;

        boolean admin = isAdmin();

        // 1) 先查任务（管理员不限制，普通用户仅能查自己的）
        Tasks exists = admin
                ? taskMapper.selectByIdAdmin(dto.getId())
                : taskMapper.selectById(dto.getId(), userId);
        if (exists == null) return false;

        // 2) 组装要更新的字段
        Tasks upd = new Tasks();
        upd.setId(dto.getId());
        upd.setTitle(dto.getTitle());
        upd.setDescription(dto.getDescription());
        upd.setPriority(dto.getPriority());
        upd.setDeadline(dto.getDeadline());
        upd.setStatus(dto.getStatus());

        // 3) 管理员按 id 更新；普通用户按 id + userId 更新
        if (admin) {
            return taskMapper.updateByIdAdmin(upd) > 0;
        } else {
            upd.setUserId(userId);              // ✅ 先设置，再传参
            return taskMapper.update(upd) > 0;  // ✅ 传对象，不要传 set... 返回值
        }
    }


    @Override
    public boolean delete(Long userId, Long id) {
        if (id == null) return false;
        boolean admin = isAdmin();

        // 管理员直接删；普通用户只能删自己
        return admin
                ? taskMapper.softDeleteAdmin(id) > 0       // 需要在 Mapper 补充（不带 userId 条件）
                : taskMapper.softDelete(id, userId) > 0;
    }

    @Override
    public Tasks detail(Long userId, Long id) {
        if (id == null) return null;
        boolean admin = isAdmin();

        return admin
                ? taskMapper.selectByIdAdmin(id)            // 需要在 Mapper 补充
                : taskMapper.selectById(id, userId);
    }

    @Override
    public PageInfo<Tasks> page(Long userId, TaskQueryDTO q) {
        int pageNum  = (q.getPageNum()  == null || q.getPageNum()  < 1) ? 1  : q.getPageNum();
        int pageSize = (q.getPageSize() == null || q.getPageSize() < 1) ? 10 : q.getPageSize();
        PageHelper.startPage(pageNum, pageSize);

        boolean admin = isAdmin();
        List<Tasks> list = admin
                ? taskMapper.pageQueryAll(q)               // 需要在 Mapper 补充（不带 userId 条件）
                : taskMapper.pageQuery(q, userId);

        return new PageInfo<>(list);
    }

    @Override
    public boolean changeStatus(Long userId, Long id, Integer status) {
        if (id == null) return false;
        boolean admin = isAdmin();

        // 管理员直接改；普通用户只能改自己的
        return admin
                ? taskMapper.updateStatusAdmin(id, status) > 0  // 需要在 Mapper 补充（不带 userId 条件）
                : taskMapper.updateStatus(userId, id, status) > 0;
    }
}
