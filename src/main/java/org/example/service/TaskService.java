package org.example.service;

import com.github.pagehelper.PageInfo;
import org.example.dto.TaskCreateDTO;
import org.example.dto.TaskQueryDTO;
import org.example.dto.TaskUpdateDTO;
import org.example.pojo.Tasks;

public interface TaskService {
    Long create(Long userId, TaskCreateDTO dto);
    boolean update(Long userId, TaskUpdateDTO dto);
    boolean delete(Long userId, Long id);
    Tasks detail(Long userId, Long id);
    PageInfo<Tasks> page(Long userId, TaskQueryDTO q); // 用 PageHelper 做分页
    boolean changeStatus(Long userId, Long id, Integer status);
}
