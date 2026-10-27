package com.example.fashionshop.net;

import android.os.Handler;
import android.os.Looper;

import com.example.fashionshop.data.model.ApiResponse;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 基于 HttpURLConnection 的简单网络工具类。
 * <p>
 * 请求在子线程（线程池）中执行，结果通过主线程 Handler 回调。
 * 统一处理后端返回格式：{"code":200,"message":"success","data":...}
 */
public final class HttpUtil {

    private static final Gson GSON = new Gson();
    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(4);
    private static final Handler MAIN_HANDLER = new Handler(Looper.getMainLooper());

    private HttpUtil() {
    }

    public static <T> void get(String path, Type dataType, ApiCallback<T> callback) {
        request("GET", path, null, dataType, callback);
    }

    public static <T> void post(String path, Map<String, String> params,
                                Type dataType, ApiCallback<T> callback) {
        JsonObject body = new JsonObject();
        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                body.addProperty(entry.getKey(), entry.getValue());
            }
        }
        request("POST", path, body.toString(), dataType, callback);
    }

    private static <T> void request(String method, String path, String jsonBody,
                                    Type dataType, ApiCallback<T> callback) {
        EXECUTOR.execute(() -> {
            HttpURLConnection connection = null;
            try {
                URL url = new URL(ApiConfig.BASE_URL + path);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod(method);
                connection.setConnectTimeout(ApiConfig.CONNECT_TIMEOUT_MS);
                connection.setReadTimeout(ApiConfig.READ_TIMEOUT_MS);
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("Charset", "UTF-8");

                if (jsonBody != null) {
                    byte[] bytes = jsonBody.getBytes(StandardCharsets.UTF_8);
                    connection.setDoOutput(true);
                    connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    try (OutputStream output = connection.getOutputStream()) {
                        output.write(bytes);
                        output.flush();
                    }
                }

                int statusCode = connection.getResponseCode();
                InputStream inputStream = (statusCode >= 200 && statusCode < 300)
                        ? connection.getInputStream()
                        : connection.getErrorStream();
                String raw = readStream(inputStream);

                if (statusCode < 200 || statusCode >= 300) {
                    deliverError(callback, extractMessage(raw,
                            "请求失败（HTTP " + statusCode + "）"));
                    return;
                }

                if (raw == null || raw.trim().isEmpty()) {
                    deliverError(callback, "服务器未返回 JSON 数据");
                    return;
                }

                JsonObject root;
                try {
                    JsonElement parsed = JsonParser.parseString(raw);
                    if (!parsed.isJsonObject()) {
                        deliverError(callback, "服务器返回数据格式错误");
                        return;
                    }
                    root = parsed.getAsJsonObject();
                } catch (Exception e) {
                    deliverError(callback, "服务器返回数据格式错误");
                    return;
                }

                if (!root.has("code") || root.get("code").isJsonNull()) {
                    deliverError(callback, "服务器响应缺少 code 字段");
                    return;
                }
                int code = root.get("code").getAsInt();
                String message = (root.has("message") && !root.get("message").isJsonNull())
                        ? root.get("message").getAsString()
                        : "";

                if (code != ApiResponse.CODE_SUCCESS) {
                    deliverError(callback, message.isEmpty()
                            ? "请求失败（错误码 " + code + "）"
                            : message);
                    return;
                }

                if (dataType != null && (!root.has("data") || root.get("data").isJsonNull())) {
                    deliverError(callback, "服务器响应缺少 data 字段");
                    return;
                }
                T data = dataType == null ? null : GSON.fromJson(root.get("data"), dataType);
                deliverSuccess(callback, data);
            } catch (Exception e) {
                deliverError(callback, friendlyMessage(e));
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        });
    }

    private static String readStream(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            return null;
        }
        try (InputStream input = inputStream;
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int length;
            while ((length = input.read(buffer)) != -1) {
                output.write(buffer, 0, length);
            }
            return new String(output.toByteArray(), StandardCharsets.UTF_8);
        }
    }

    private static String extractMessage(String raw, String fallback) {
        if (raw == null || raw.trim().isEmpty()) {
            return fallback;
        }
        try {
            JsonElement parsed = JsonParser.parseString(raw);
            if (parsed.isJsonObject()) {
                JsonObject object = parsed.getAsJsonObject();
                if (object.has("message") && !object.get("message").isJsonNull()) {
                    String message = object.get("message").getAsString();
                    if (!message.isEmpty()) {
                        return message;
                    }
                }
            }
        } catch (Exception ignored) {
            // 返回内容不是 JSON 时使用默认提示
        }
        return fallback;
    }

    private static String friendlyMessage(Exception e) {
        if (e instanceof UnknownHostException) {
            return "无法连接服务器，请检查网络或服务器地址";
        }
        if (e instanceof SocketTimeoutException) {
            return "连接服务器超时，请稍后重试";
        }
        if (e instanceof IOException) {
            return "网络请求失败，请检查网络连接";
        }
        return "数据处理失败：" + e.getMessage();
    }

    private static <T> void deliverSuccess(ApiCallback<T> callback, T data) {
        if (callback == null) {
            return;
        }
        MAIN_HANDLER.post(() -> callback.onSuccess(data));
    }

    private static <T> void deliverError(ApiCallback<T> callback, String message) {
        if (callback == null) {
            return;
        }
        MAIN_HANDLER.post(() -> callback.onError(message));
    }
}
