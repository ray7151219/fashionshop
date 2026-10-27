package com.example.fashionshop.data.model;

/**
 * 订单中的商品快照，下单后不随购物车变化。
 */
public class OrderItem {

    private long productId;
    private String name;
    private double price;
    private String imageUrl;
    private String size;
    private int quantity;

    public OrderItem() {
    }

    public OrderItem(CartItem cartItem) {
        this.productId = cartItem.getProductId();
        this.name = cartItem.getName();
        this.price = cartItem.getPrice();
        this.imageUrl = cartItem.getImageUrl();
        this.size = cartItem.getSize();
        this.quantity = cartItem.getQuantity();
    }

    public double getSubtotal() {
        return price * quantity;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
