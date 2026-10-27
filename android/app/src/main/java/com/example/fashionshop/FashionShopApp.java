package com.example.fashionshop;

import android.app.Application;

import com.example.fashionshop.net.ApiConfig;
import com.example.fashionshop.net.MockApi;

/**
 * 应用入口：启动时恢复数据源设置（Mock / 真实后端）。系统创建后第一个执行的回调，
 * 比任何activity都早，所以在在这里做全局初始化。执行完跳login，
 */
public class FashionShopApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ApiConfig.init(this);
        MockApi.init(this);
    }
}
