// org.example.pojo.OperationLog
package org.example.pojo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class OperationLog {
    private Long id;
    private Long userId;
    private String username;
    private String action;
    private String method;
    private String uri;
    private String ip;
    private String params;
    private String result;
    private Boolean success;
    private String error;
    private LocalDateTime createdAt;
}
