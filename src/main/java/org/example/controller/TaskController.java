package org.example.controller;

import com.github.pagehelper.PageInfo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.common.ApiResponse;
import org.example.dto.TaskCreateDTO;
import org.example.dto.TaskQueryDTO;
import org.example.dto.TaskUpdateDTO;
import org.example.log.Op;
import org.example.pojo.Tasks;
import org.example.service.TaskService;
import org.example.utils.ExcelExportUtil;
import org.example.utils.JwtUtil;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;
    public TaskController(TaskService taskService) { this.taskService = taskService; }

    private Long currentUserId(HttpServletRequest request) {
        Object v = request.getAttribute("currentUserId");
        return (v instanceof Long) ? (Long) v : null;
    }
    @Op("创建任务")
    @PostMapping
    public ApiResponse<Long> create(@RequestBody TaskCreateDTO dto, HttpServletRequest request) {
        Long userId = currentUserId(request);
        if (userId == null) return ApiResponse.fail("未登录或Token无效");
        Long id = taskService.create(userId, dto);
        return (id != null) ? ApiResponse.success("创建成功", id) : ApiResponse.fail("创建失败：标题必填");
    }

    @Op("修改任务")
    @PutMapping("/{id}")
    public ApiResponse<String> update(@PathVariable Long id,
                                      @RequestBody TaskUpdateDTO dto,
                                      HttpServletRequest request) {
        Long userId = currentUserId(request);
        if (userId == null) return ApiResponse.fail("未登录或Token无效");
        dto.setId(id);
        return taskService.update(userId, dto)
                ? ApiResponse.success("更新成功", null)
                : ApiResponse.fail("更新失败");
    }

    @Op("删除任务")
    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable Long id, HttpServletRequest request) {
        Long userId = currentUserId(request);
        if (userId == null) return ApiResponse.fail("未登录或Token无效");
        return taskService.delete(userId, id)
                ? ApiResponse.success("删除成功", null)
                : ApiResponse.fail("删除失败");
    }

    @GetMapping("/{id}")
    public ApiResponse<Tasks> detail(@PathVariable Long id, HttpServletRequest request) {
        Long userId = currentUserId(request);
        if (userId == null) return ApiResponse.fail("未登录或Token无效");
        Tasks t = taskService.detail(userId, id);
        return (t != null) ? ApiResponse.success(t) : ApiResponse.fail("任务不存在");
    }

    // ✅ 正确的分页查询路径（GET /tasks）
    @GetMapping
    public ApiResponse<PageInfo<Tasks>> page(TaskQueryDTO q, HttpServletRequest request) {
        Long userId = currentUserId(request);
        if (userId == null) return ApiResponse.fail("未登录或Token无效");
        return ApiResponse.success(taskService.page(userId, q));
    }

    @Op("修改任务状态")
    @PatchMapping("/{id}/status")
    public ApiResponse<String> changeStatus(@PathVariable Long id,
                                            @RequestParam Integer status,
                                            HttpServletRequest request) {
        Long userId = currentUserId(request);
        if (userId == null) return ApiResponse.fail("未登录或Token无效");
        return taskService.changeStatus(userId, id, status)
                ? ApiResponse.success("状态已更新", null)
                : ApiResponse.fail("更新失败");
    }
    @GetMapping("/export")
    public void export(TaskQueryDTO q, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Long userId = currentUserId(request);
        if (userId == null) {
            response.setStatus(401);
            return;
        }
        // 复用你的 service 查询一页大的（或专门给个 listAllByQuery）
        q.setPageNum(1); q.setPageSize(10000);
        List<Tasks> list = taskService.page(userId, q).getList();

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition","attachment; filename=tasks.xlsx");
        ExcelExportUtil.writeTasks(list, response.getOutputStream());
    }
}

