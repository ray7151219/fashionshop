package com.example.fashionshop;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.fashionshop.data.CartManager;
import com.example.fashionshop.data.ProductSizes;
import com.example.fashionshop.data.model.Product;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

public class ProductDetailActivity extends AppCompatActivity {

    private static final String EXTRA_ID = "extra_id";
    private static final String EXTRA_NAME = "extra_name";
    private static final String EXTRA_CATEGORY = "extra_category";
    private static final String EXTRA_PRICE = "extra_price";
    private static final String EXTRA_IMAGE_URL = "extra_image_url";
    private static final String EXTRA_DESCRIPTION = "extra_description";

    public static Intent createIntent(Context context, Product product) {
        Intent intent = new Intent(context, ProductDetailActivity.class);
        intent.putExtra(EXTRA_ID, product.getId());
        intent.putExtra(EXTRA_NAME, product.getName());
        intent.putExtra(EXTRA_CATEGORY, product.getCategory());
        intent.putExtra(EXTRA_PRICE, product.getPrice());
        intent.putExtra(EXTRA_IMAGE_URL, product.getImageUrl());
        intent.putExtra(EXTRA_DESCRIPTION, product.getDescription());
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_detail);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        Intent intent = getIntent();
        long id = intent.getLongExtra(EXTRA_ID, 0);
        String name = intent.getStringExtra(EXTRA_NAME);
        String category = intent.getStringExtra(EXTRA_CATEGORY);
        double price = intent.getDoubleExtra(EXTRA_PRICE, 0);
        String imageUrl = intent.getStringExtra(EXTRA_IMAGE_URL);
        String description = intent.getStringExtra(EXTRA_DESCRIPTION);

        Product product = new Product(id,
                name == null ? "" : name,
                category == null ? "" : category,
                price,
                imageUrl == null ? "" : imageUrl,
                description == null ? "" : description);

        TextView tvName = findViewById(R.id.tv_detail_name);
        TextView tvCategory = findViewById(R.id.tv_detail_category);
        TextView tvPrice = findViewById(R.id.tv_detail_price);
        TextView tvDescription = findViewById(R.id.tv_detail_desc);
        ImageView ivImage = findViewById(R.id.iv_detail_image);

        tvName.setText(name);
        boolean hasCategory = category != null && !category.isEmpty();
        tvCategory.setVisibility(hasCategory ? View.VISIBLE : View.GONE);
        tvCategory.setText(hasCategory ? getString(R.string.category_format, category) : "");
        tvPrice.setText(getString(R.string.price_format, price));
        tvDescription.setText(description == null ? "" : description);

        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.bg_placeholder)
                .error(R.drawable.bg_placeholder)
                .centerCrop()
                .into(ivImage);

        ChipGroup chipGroup = findViewById(R.id.chip_group_sizes);
        String[] sizes = ProductSizes.forProduct(product);
        for (String size : sizes) {
            Chip chip = new Chip(this);
            chip.setId(View.generateViewId());
            chip.setText(size);
            chip.setCheckable(true);
            chip.setClickable(true);
            chipGroup.addView(chip);
            if (sizes.length == 1) {
                chip.setChecked(true); // 均码商品自动选中
            }
        }

        MaterialButton btnAddCart = findViewById(R.id.btn_add_cart);
        btnAddCart.setOnClickListener(v -> {
            int checkedId = chipGroup.getCheckedChipId();
            if (checkedId == View.NO_ID) {
                Toast.makeText(this, R.string.select_size, Toast.LENGTH_SHORT).show();
                return;
            }
            Chip chip = chipGroup.findViewById(checkedId);
            String size = chip.getText().toString();
            CartManager.add(this, product, size);
            Toast.makeText(this, R.string.added_to_cart, Toast.LENGTH_SHORT).show();
        });
    }
}
