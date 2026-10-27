package com.fashionshop.backend.servlet;

import com.fashionshop.backend.model.User;
import com.fashionshop.backend.store.UserStore;
import com.google.gson.JsonObject;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * POST /api/register
 * 请求体：{"username":"newuser","password":"123456"}
 */
@WebServlet("/api/register")
public class RegisterServlet extends BaseServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        JsonObject body = readJson(req);
        if (body == null) {
            writeError(resp, CODE_BAD_REQUEST, "请求体不是合法的 JSON");
            return;
        }

        String username = getString(body, "username");
        String password = getString(body, "password");
        if (username.length() < 3) {
            writeError(resp, CODE_BAD_REQUEST, "用户名至少 3 个字符");
            return;
        }
        if (password.length() < 6) {
            writeError(resp, CODE_BAD_REQUEST, "密码至少 6 位");
            return;
        }

        User user = UserStore.register(username, password);
        if (user == null) {
            writeError(resp, CODE_BAD_REQUEST, "用户名已存在，请更换一个");
            return;
        }
        writeSuccess(resp, user);
    }
}
