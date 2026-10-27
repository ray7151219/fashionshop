package com.example.fashionshop.net;

import com.example.fashionshop.data.model.Product;
import com.example.fashionshop.data.model.User;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 业务接口统一入口，UI 层只调用这里的方法。
 * 通过 ApiConfig.USE_MOCK 在 Mock 数据和真实后端之间切换。
 */
public final class ApiClient {

    private static final Type PRODUCT_LIST_TYPE =
            new TypeToken<List<Product>>() {
            }.getType();

    private ApiClient() {
    }

    public static void login(String username, String password, ApiCallback<User> callback) {
        if (ApiConfig.USE_MOCK) {
            MockApi.login(username, password, callback);
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        HttpUtil.post(ApiConfig.API_LOGIN, params, User.class, callback);
    }

    public static void register(String username, String password, ApiCallback<User> callback) {
        if (ApiConfig.USE_MOCK) {
            MockApi.register(username, password, callback);
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        HttpUtil.post(ApiConfig.API_REGISTER, params, User.class, callback);
    }

    public static void getProducts(ApiCallback<List<Product>> callback) {
        if (ApiConfig.USE_MOCK) {
            MockApi.getProducts(callback);
            return;
        }
        HttpUtil.get(ApiConfig.API_PRODUCTS, PRODUCT_LIST_TYPE, callback);
    }
}
