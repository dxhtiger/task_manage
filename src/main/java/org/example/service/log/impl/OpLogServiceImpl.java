// org.example.service.log.impl.OpLogServiceImpl
package org.example.service.log.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.mapper.OperationLogMapper;
import org.example.pojo.OperationLog;
import org.example.service.log.OpLogService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class OpLogServiceImpl implements OpLogService {

    private final OperationLogMapper mapper;
    private final ObjectMapper om = new ObjectMapper();

    @Value("${ops.log.store:db}")
    private String store;

    @Value("${ops.log.file-path:logs/ops.log}")
    private String filePath;

    public OpLogServiceImpl(OperationLogMapper mapper) { this.mapper = mapper; }

    @Override
    public void save(OperationLog log) {
        try {
            if ("file".equalsIgnoreCase(store)) {
                Path p = Path.of(filePath).toAbsolutePath();
                Files.createDirectories(p.getParent());
                try (FileWriter fw = new FileWriter(p.toFile(), true)) {
                    fw.write(om.writeValueAsString(log));
                    fw.write(System.lineSeparator());
                }
            } else {
                mapper.insert(log);
            }
        } catch (Exception e) {
            // 兜底：不要影响主流程
            e.printStackTrace();
        }
    }
}
