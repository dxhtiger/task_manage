package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.pojo.OperationLog;

@Mapper
public interface OperationLogMapper {
    int insert(OperationLog log);
}