package com.example.fashionshop.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.fashionshop.data.model.CartItem;
import com.example.fashionshop.data.model.Product;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 购物车本地存储。
 * 按用户 id 分开保存，数据通过 Gson 转成 JSON 放进 SharedPreferences。
 */
public final class CartManager {

    private static final String PREFS_NAME = "fashion_shop_cart";
    private static final String KEY_PREFIX = "cart_";
    private static final int MAX_QUANTITY = 99;

    private static final Gson GSON = new Gson();
    private static final Type LIST_TYPE = new TypeToken<List<CartItem>>() {
    }.getType();

    private CartManager() {
    }

    public static List<CartItem> getItems(Context context) {
        String json = preferences(context).getString(key(context), null);
        if (json == null || json.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            List<CartItem> items = GSON.fromJson(json, LIST_TYPE);
            return items == null ? new ArrayList<>() : items;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public static void add(Context context, Product product, String size) {
        List<CartItem> items = getItems(context);
        for (CartItem item : items) {
            if (isSameItem(item, product.getId(), size)) {
                item.setQuantity(Math.min(MAX_QUANTITY, item.getQuantity() + 1));
                item.setSelected(true);
                save(context, items);
                return;
            }
        }
        items.add(new CartItem(product, size, 1));
        save(context, items);
    }

    public static void updateQuantity(Context context, long productId, String size, int quantity) {
        List<CartItem> items = getItems(context);
        for (int i = 0; i < items.size(); i++) {
            CartItem item = items.get(i);
            if (isSameItem(item, productId, size)) {
                if (quantity <= 0) {
                    items.remove(i);
                } else {
                    item.setQuantity(Math.min(MAX_QUANTITY, quantity));
                }
                save(context, items);
                return;
            }
        }
    }

    public static void remove(Context context, long productId, String size) {
        List<CartItem> items = getItems(context);
        for (int i = 0; i < items.size(); i++) {
            if (isSameItem(items.get(i), productId, size)) {
                items.remove(i);
                save(context, items);
                return;
            }
        }
    }

    public static void setSelected(Context context, long productId, String size, boolean selected) {
        List<CartItem> items = getItems(context);
        for (CartItem item : items) {
            if (isSameItem(item, productId, size)) {
                item.setSelected(selected);
                save(context, items);
                return;
            }
        }
    }

    /**
     * 同一商品的不同尺码是两条独立记录。
     */
    private static boolean isSameItem(CartItem item, long productId, String size) {
        return item.getProductId() == productId && Objects.equals(item.getSize(), size);
    }

    public static void setAllSelected(Context context, boolean selected) {
        List<CartItem> items = getItems(context);
        for (CartItem item : items) {
            item.setSelected(selected);
        }
        save(context, items);
    }

    public static List<CartItem> getSelectedItems(Context context) {
        List<CartItem> selected = new ArrayList<>();
        for (CartItem item : getItems(context)) {
            if (item.isSelected()) {
                selected.add(item);
            }
        }
        return selected;
    }

    public static void removeSelected(Context context) {
        List<CartItem> remaining = new ArrayList<>();
        for (CartItem item : getItems(context)) {
            if (!item.isSelected()) {
                remaining.add(item);
            }
        }
        save(context, remaining);
    }

    public static double getSelectedTotal(Context context) {
        double total = 0;
        for (CartItem item : getItems(context)) {
            if (item.isSelected()) {
                total += item.getSubtotal();
            }
        }
        return total;
    }

    public static int getSelectedQuantity(Context context) {
        int count = 0;
        for (CartItem item : getItems(context)) {
            if (item.isSelected()) {
                count += item.getQuantity();
            }
        }
        return count;
    }

    public static int getTotalQuantity(Context context) {
        int count = 0;
        for (CartItem item : getItems(context)) {
            count += item.getQuantity();
        }
        return count;
    }

    public static boolean isAllSelected(Context context) {
        List<CartItem> items = getItems(context);
        if (items.isEmpty()) {
            return false;
        }
        for (CartItem item : items) {
            if (!item.isSelected()) {
                return false;
            }
        }
        return true;
    }

    private static void save(Context context, List<CartItem> items) {
        preferences(context).edit()
                .putString(key(context), GSON.toJson(items))
                .apply();
    }

    private static SharedPreferences preferences(Context context) {
        return context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    private static String key(Context context) {
        return KEY_PREFIX + new SessionManager(context).getStorageScope();
    }
}
