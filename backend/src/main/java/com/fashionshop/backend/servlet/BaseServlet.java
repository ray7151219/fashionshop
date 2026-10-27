package com.fashionshop.backend.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Servlet 公共基类：统一 JSON 返回格式、UTF-8 和跨域头。
 * 返回格式：{"code":200,"message":"success","data":...}
 */
public abstract class BaseServlet extends HttpServlet {

    protected static final Gson GSON = new Gson();

    protected static final int CODE_SUCCESS = 200;
    protected static final int CODE_BAD_REQUEST = 400;
    protected static final int CODE_UNAUTHORIZED = 401;
    protected static final int CODE_NOT_FOUND = 404;
    protected static final int CODE_SERVER_ERROR = 500;

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        addCorsHeaders(resp);
        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    protected void writeSuccess(HttpServletResponse resp, Object data) throws IOException {
        writeResult(resp, CODE_SUCCESS, "success", data);
    }

    protected void writeError(HttpServletResponse resp, int code, String message)
            throws IOException {
        writeResult(resp, code, message, null);
    }

    protected void writeResult(HttpServletResponse resp, int code, String message, Object data)
            throws IOException {
        addCorsHeaders(resp);
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("code", code);
        body.put("message", message);
        body.put("data", data);
        resp.getWriter().write(GSON.toJson(body));
    }

    /**
     * 读取 application/json 请求体；解析失败返回 null。
     */
    protected JsonObject readJson(HttpServletRequest request) throws IOException {
        request.setCharacterEncoding("UTF-8");
        StringBuilder text = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                text.append(line);
            }
        }
        if (text.length() == 0) {
            return new JsonObject();
        }
        try {
            return JsonParser.parseString(text.toString()).getAsJsonObject();
        } catch (Exception e) {
            return null;
        }
    }

    protected String getString(JsonObject object, String key) {
        if (object == null || !object.has(key) || object.get(key).isJsonNull()) {
            return "";
        }
        return object.get(key).getAsString().trim();
    }

    private void addCorsHeaders(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Accept");
    }
}
