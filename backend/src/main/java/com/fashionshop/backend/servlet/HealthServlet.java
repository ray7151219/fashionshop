package com.fashionshop.backend.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * GET /api/health：判断 Tomcat 和项目是否正常启动。
 */
@WebServlet("/api/health")
public class HealthServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("status", "UP");
        data.put("service", "fashionshop-backend");
        data.put("time", System.currentTimeMillis());
        writeSuccess(resp, data);
    }
}
