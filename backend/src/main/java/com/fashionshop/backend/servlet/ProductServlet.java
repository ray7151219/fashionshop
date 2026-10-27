package com.fashionshop.backend.servlet;

import com.fashionshop.backend.model.Product;
import com.fashionshop.backend.store.ProductStore;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * GET /api/products      商品列表
 * GET /api/products/{id} 商品详情
 */
@WebServlet(urlPatterns = {"/api/products", "/api/products/*"})
public class ProductServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.isEmpty() || "/".equals(pathInfo)) {
            writeSuccess(resp, ProductStore.getProducts());
            return;
        }

        String idText = pathInfo.substring(1);
        long id;
        try {
            id = Long.parseLong(idText);
        } catch (NumberFormatException e) {
            writeError(resp, CODE_BAD_REQUEST, "商品 id 格式不正确");
            return;
        }

        Product product = ProductStore.findProduct(id);
        if (product == null) {
            writeError(resp, CODE_NOT_FOUND, "商品不存在");
            return;
        }
        writeSuccess(resp, product);
    }
}
