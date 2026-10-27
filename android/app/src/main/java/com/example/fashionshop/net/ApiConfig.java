package com.example.fashionshop.net;

import android.content.Context;

/**
 * 网络配置与接口路径。
 */
public final class ApiConfig {

    private static final String SETTINGS_PREFS = "fashion_shop_settings";
    private static final String KEY_USE_MOCK = "use_mock";

    private ApiConfig() {
    }

    /**
     * true：使用本地 Mock 数据，后端未启动也能跑通页面。
     * false：请求真实后端。
     * 可在“我的”页面运行时切换，切换后立即生效。
     */
    public static boolean USE_MOCK = true;

    /**
     * 10.0.2.2 是 Android 模拟器访问电脑 localhost 的地址。
     * 真机调试时改成电脑的局域网 IP，例如 http://192.168.1.100:8080/fashionshop/
     */
    public static final String BASE_URL = "http://10.0.2.2:8080/fashionshop/";

    public static final String API_LOGIN = "api/login";
    public static final String API_REGISTER = "api/register";
    public static final String API_PRODUCTS = "api/products";

    public static final int CONNECT_TIMEOUT_MS = 10_000;
    public static final int READ_TIMEOUT_MS = 10_000;

    public static final long MOCK_DELAY_MS = 600;

    /**
     * App 启动时读取上次选择的数据源。默认使用 Mock，保证没有后端也能演示。
     */
    public static void init(Context context) {
        USE_MOCK = context.getApplicationContext()
                .getSharedPreferences(SETTINGS_PREFS, Context.MODE_PRIVATE)
                .getBoolean(KEY_USE_MOCK, true);
    }

    public static void setUseMock(Context context, boolean useMock) {
        USE_MOCK = useMock;
        context.getApplicationContext()
                .getSharedPreferences(SETTINGS_PREFS, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(KEY_USE_MOCK, useMock)
                .apply();
    }
}
