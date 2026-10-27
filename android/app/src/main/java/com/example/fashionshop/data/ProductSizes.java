package com.example.fashionshop.data;

import com.example.fashionshop.data.model.Product;

/**
 * 商品可选尺码。按商品名称简单区分：围巾为均码，鞋类用鞋码，其余用服装码。
 * 后端商品也走这套规则，保证 Mock 与真实数据表现一致。
 */
public final class ProductSizes {

    private static final String[] CLOTHING = {"S", "M", "L", "XL"};
    private static final String[] ONE_SIZE = {"均码"};
    private static final String[] SHOES = {"37", "38", "39", "40", "41", "42"};

    private ProductSizes() {
    }

    public static String[] forProduct(Product product) {
        String name = (product == null || product.getName() == null)
                ? "" : product.getName();
        if (name.contains("围巾")) {
            return ONE_SIZE;
        }
        if (name.contains("鞋")) {
            return SHOES;
        }
        return CLOTHING;
    }
}
