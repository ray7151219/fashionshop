package com.example.fashionshop.data.model;

/**
 * 购物车中的一条商品记录。
 * 这里保存商品快照，避免商品下架或改价后购物车显示错乱。
 */
public class CartItem {

    private long productId;
    private String name;
    private String category;
    private double price;
    private String imageUrl;
    private String description;
    private String size;
    private int quantity;
    private boolean selected;

    public CartItem() {
    }

    public CartItem(Product product, String size, int quantity) {
        this.productId = product.getId();
        this.name = product.getName();
        this.category = product.getCategory();
        this.price = product.getPrice();
        this.imageUrl = product.getImageUrl();
        this.description = product.getDescription();
        this.size = size;
        this.quantity = quantity;
        this.selected = true;
    }

    public Product toProduct() {
        return new Product(productId, name, category, price, imageUrl, description);
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
