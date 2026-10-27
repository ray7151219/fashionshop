package com.example.fashionshop.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.fashionshop.data.model.CartItem;
import com.example.fashionshop.data.model.Order;
import com.example.fashionshop.data.model.OrderItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * 订单本地存储。下单时把购物车商品转成订单快照。
 */
public final class OrderManager {

    private static final String PREFS_NAME = "fashion_shop_orders";
    private static final String KEY_PREFIX = "orders_";
    public static final String STATUS_UNPAID = "待付款";
    public static final String STATUS_PAID = "待发货";
    public static final String STATUS_CANCELLED = "已取消";

    private static final Gson GSON = new Gson();
    private static final Type LIST_TYPE = new TypeToken<List<Order>>() {
    }.getType();
    private static final Random RANDOM = new Random();

    private OrderManager() {
    }

    public static synchronized Order createOrder(Context context, List<CartItem> cartItems,
                                                  String recipient, String phone, String address) {
        if (cartItems == null || cartItems.isEmpty()) {
            return null;
        }

        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setCreatedAt(System.currentTimeMillis());
        order.setStatus(STATUS_UNPAID);
        order.setRecipient(recipient);
        order.setPhone(phone);
        order.setAddress(address);

        List<OrderItem> orderItems = new ArrayList<>();
        java.math.BigDecimal total = java.math.BigDecimal.ZERO;
        for (CartItem cartItem : cartItems) {
            if (cartItem.getQuantity() < 1 || cartItem.getPrice() < 0
                    || !Double.isFinite(cartItem.getPrice())) {
                return null;
            }
            OrderItem orderItem = new OrderItem(cartItem);
            orderItems.add(orderItem);
            total = total.add(java.math.BigDecimal.valueOf(cartItem.getPrice())
                    .multiply(java.math.BigDecimal.valueOf(cartItem.getQuantity())));
        }
        order.setItems(orderItems);
        order.setTotal(total.setScale(2, java.math.RoundingMode.HALF_UP).doubleValue());

        List<Order> orders = getOrders(context);
        orders.add(0, order);
        return save(context, orders) ? order : null;
    }

    public static synchronized Order findOrder(Context context, String orderNo) {
        for (Order order : getOrders(context)) {
            if (orderNo != null && orderNo.equals(order.getOrderNo())) {
                return order;
            }
        }
        return null;
    }

    /** 只有待付款可以变成待发货；commit 成功才报告支付成功。 */
    public static synchronized boolean simulatePayment(Context context, String orderNo,
                                                        String method) {
        if (method == null || method.trim().isEmpty()) return false;
        List<Order> orders = getOrders(context);
        for (Order order : orders) {
            if (orderNo.equals(order.getOrderNo()) && STATUS_UNPAID.equals(order.getStatus())) {
                order.setStatus(STATUS_PAID);
                order.setPaymentMethod(method);
                order.setPaidAt(System.currentTimeMillis());
                return save(context, orders);
            }
        }
        return false;
    }

    public static synchronized boolean cancelUnpaid(Context context, String orderNo) {
        List<Order> orders = getOrders(context);
        for (Order order : orders) {
            if (orderNo.equals(order.getOrderNo()) && STATUS_UNPAID.equals(order.getStatus())) {
                order.setStatus(STATUS_CANCELLED);
                return save(context, orders);
            }
        }
        return false;
    }

    /** 删除已完成（待发货）或已取消的订单；待付款订单需先取消。 */
    public static synchronized boolean deleteOrder(Context context, String orderNo) {
        List<Order> orders = getOrders(context);
        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);
            boolean deletable = STATUS_PAID.equals(order.getStatus())
                    || STATUS_CANCELLED.equals(order.getStatus());
            if (orderNo.equals(order.getOrderNo()) && deletable) {
                orders.remove(i);
                return save(context, orders);
            }
        }
        return false;
    }

    public static List<Order> getOrders(Context context) {
        String json = preferences(context).getString(key(context), null);
        if (json == null || json.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            List<Order> orders = GSON.fromJson(json, LIST_TYPE);
            return orders == null ? new ArrayList<>() : orders;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private static String generateOrderNo() {
        String time = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA)
                .format(new Date());
        return "FS" + time + String.format(Locale.CHINA, "%03d", RANDOM.nextInt(1000));
    }

    private static boolean save(Context context, List<Order> orders) {
        return preferences(context).edit()
                .putString(key(context), GSON.toJson(orders))
                .commit();
    }

    private static SharedPreferences preferences(Context context) {
        return context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    private static String key(Context context) {
        return KEY_PREFIX + new SessionManager(context).getStorageScope();
    }
}
