package com.ong.api_backend.util;

import com.ong.api_backend.model.Logs;
import com.ong.api_backend.repository.LogsRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Objects;

@AllArgsConstructor
@Service
public class IpUtil {

    private final LogsRepository logsRepository;
    public static String getClientIp(HttpServletRequest request) {

        String ip = request.getHeader("X-Forwarded-For");

        if (ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }

        if (ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("CF-Connecting-IP");
        }

        if (ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        if (ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        return ip;
    }

}
