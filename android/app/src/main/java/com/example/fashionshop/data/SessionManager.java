package com.example.fashionshop.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import java.nio.charset.StandardCharsets;
import com.example.fashionshop.net.ApiConfig;

import com.example.fashionshop.data.model.User;

/**
 * 使用 SharedPreferences 保存登录状态。
 */
public class SessionManager {

    private static final String PREFS_NAME = "fashion_shop_session";
    private static final String KEY_LOGGED_IN = "logged_in";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_SOURCE = "source";

    private final SharedPreferences preferences;

    public SessionManager(Context context) {
        preferences = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveSession(User user) {
        preferences.edit()
                .putBoolean(KEY_LOGGED_IN, true)
                .putLong(KEY_USER_ID, user.getId())
                .putString(KEY_USERNAME, user.getUsername())
                .putString(KEY_SOURCE, ApiConfig.USE_MOCK ? "mock" : "backend")
                .apply();
    }

    public boolean isLoggedIn() {
        return preferences.getBoolean(KEY_LOGGED_IN, false)
                && (ApiConfig.USE_MOCK ? "mock" : "backend")
                .equals(preferences.getString(KEY_SOURCE, ""));
    }

    public long getUserId() {
        return preferences.getLong(KEY_USER_ID, -1L);
    }

    public String getUsername() {
        return preferences.getString(KEY_USERNAME, "");
    }

    /** 本地购物车和订单按数据源、用户名分开保存。 */
    public String getStorageScope() {
        String source = ApiConfig.USE_MOCK ? "mock" : "backend";
        String username = getUsername();
        String safeName = Base64.encodeToString(username.getBytes(StandardCharsets.UTF_8),
                Base64.URL_SAFE | Base64.NO_WRAP | Base64.NO_PADDING);
        return source + "_" + safeName;
    }

    public void clear() {
        preferences.edit().clear().apply();
    }
}
