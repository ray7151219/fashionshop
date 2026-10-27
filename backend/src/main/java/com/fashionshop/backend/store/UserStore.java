package com.fashionshop.backend.store;

import com.fashionshop.backend.model.User;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 用户数据仓库。
 * 仅用于课程演示：密码明文保存在内存中，服务器重启后新注册用户会丢失。
 */
public final class UserStore {

    private static final Map<String, UserAccount> USERS = new ConcurrentHashMap<>();
    private static final AtomicLong USER_ID = new AtomicLong(2);

    static {
        USERS.put("demo", new UserAccount(new User(1, "demo"), "123456"));
    }

    private UserStore() {
    }

    public static User login(String username, String password) {
        UserAccount account = USERS.get(username);
        if (account == null || !account.password.equals(password)) {
            return null;
        }
        return account.user;
    }

    /**
     * @return 注册成功返回用户；用户名已存在返回 null
     */
    public static User register(String username, String password) {
        if (USERS.containsKey(username)) {
            return null;
        }
        User user = new User(USER_ID.getAndIncrement(), username);
        USERS.put(username, new UserAccount(user, password));
        return user;
    }

    private static final class UserAccount {

        final User user;
        final String password;

        UserAccount(User user, String password) {
            this.user = user;
            this.password = password;
        }
    }
}
