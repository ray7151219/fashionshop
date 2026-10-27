package com.example.fashionshop.ui.home;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.fashionshop.ProductDetailActivity;
import com.example.fashionshop.R;
import com.example.fashionshop.data.model.Product;
import com.example.fashionshop.net.ApiCallback;
import com.example.fashionshop.net.ApiClient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 首页：顶部轮播图 + 商品列表，支持刷新和按分类筛选。
 */
public class HomeFragment extends Fragment {

    private static final long BANNER_AUTO_SCROLL_MS = 3000L;

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private View layoutError;
    private TextView tvError;
    private TextView tvEmpty;
    private ChipGroup categoryGroup;
    private MaterialButton refreshButton;
    private ProductAdapter adapter;

    private ViewPager2 bannerPager;
    private LinearLayout bannerDots;
    private BannerAdapter bannerAdapter;
    private View[] dots;
    private final Handler bannerHandler = new Handler(Looper.getMainLooper());
    private Runnable bannerRunnable;

    private final List<Product> allProducts = new ArrayList<>();
    private String selectedCategory;
    private boolean loading;
    private boolean buildingChips;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recycler_products);
        progressBar = view.findViewById(R.id.progress_loading);
        layoutError = view.findViewById(R.id.layout_error);
        tvError = view.findViewById(R.id.tv_error);
        tvEmpty = view.findViewById(R.id.tv_empty);
        categoryGroup = view.findViewById(R.id.chip_group_categories);
        refreshButton = view.findViewById(R.id.btn_refresh);
        bannerPager = view.findViewById(R.id.banner_pager);
        bannerDots = view.findViewById(R.id.banner_dots);

        adapter = new ProductAdapter(this::openDetail);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        recyclerView.setAdapter(adapter);

        setupBanner();

        categoryGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (buildingChips || checkedIds.isEmpty()) {
                return;
            }
            Chip chip = group.findViewById(checkedIds.get(0));
            selectedCategory = chip == null ? null : (String) chip.getTag();
            applyFilter();
        });
        refreshButton.setOnClickListener(v -> loadProducts(true));
        view.findViewById(R.id.btn_retry).setOnClickListener(v -> loadProducts(false));
        loadProducts(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        startAutoScroll();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopAutoScroll();
    }

    private void setupBanner() {
        List<BannerAdapter.Banner> banners = new ArrayList<>();
        banners.add(new BannerAdapter.Banner("秋冬新品上市", "精选服饰 · 温暖过冬", 0xFFE07A5F));
        banners.add(new BannerAdapter.Banner("限时特惠", "全场满 299 包邮", 0xFFE63946));
        banners.add(new BannerAdapter.Banner("新用户专享", "注册即享优惠好礼", 0xFF5C6BC0));
        banners.add(new BannerAdapter.Banner("品质精选", "正品保障 · 值得信赖", 0xFF26A69A));

        bannerAdapter = new BannerAdapter();
        bannerAdapter.submitList(banners);
        bannerPager.setAdapter(bannerAdapter);

        setupDots(banners.size());
        bannerPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateDots(position);
            }
        });
    }

    private void setupDots(int count) {
        bannerDots.removeAllViews();
        dots = new View[count];
        float density = getResources().getDisplayMetrics().density;
        int size = (int) (8 * density + 0.5f);
        int margin = (int) (4 * density + 0.5f);
        for (int i = 0; i < count; i++) {
            View dot = new View(requireContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
            params.setMargins(margin, 0, margin, 0);
            dot.setLayoutParams(params);
            dot.setBackgroundResource(R.drawable.bg_dot);
            bannerDots.addView(dot);
            dots[i] = dot;
        }
        updateDots(0);
    }

    private void updateDots(int position) {
        if (dots == null) {
            return;
        }
        for (int i = 0; i < dots.length; i++) {
            dots[i].setSelected(i == position);
        }
    }

    private void startAutoScroll() {
        stopAutoScroll();
        if (bannerAdapter == null || bannerAdapter.getItemCount() <= 1) {
            return;
        }
        bannerRunnable = new Runnable() {
            @Override
            public void run() {
                if (bannerPager != null && bannerAdapter.getItemCount() > 1) {
                    int next = (bannerPager.getCurrentItem() + 1) % bannerAdapter.getItemCount();
                    bannerPager.setCurrentItem(next, true);
                }
                bannerHandler.postDelayed(this, BANNER_AUTO_SCROLL_MS);
            }
        };
        bannerHandler.postDelayed(bannerRunnable, BANNER_AUTO_SCROLL_MS);
    }

    private void stopAutoScroll() {
        if (bannerRunnable != null) {
            bannerHandler.removeCallbacks(bannerRunnable);
            bannerRunnable = null;
        }
    }

    private void loadProducts(boolean refresh) {
        if (loading) {
            return;
        }
        loading = true;
        refreshButton.setEnabled(false);
        if (!refresh) {
            showLoading();
        }
        ApiClient.getProducts(new ApiCallback<List<Product>>() {
            @Override
            public void onSuccess(List<Product> data) {
                loading = false;
                refreshButton.setEnabled(true);
                if (!isAdded() || getView() == null) {
                    return;
                }
                allProducts.clear();
                if (data != null) {
                    allProducts.addAll(data);
                }
                buildCategoryChips();
                applyFilter();
                if (refresh) {
                    Toast.makeText(requireContext(), R.string.refresh_success,
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String message) {
                loading = false;
                refreshButton.setEnabled(true);
                if (!isAdded() || getView() == null) {
                    return;
                }
                if (refresh && !allProducts.isEmpty()) {
                    Toast.makeText(requireContext(), messageText(message),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                showError(message);
            }
        });
    }

    private void buildCategoryChips() {
        Set<String> categories = new LinkedHashSet<>();
        for (Product product : allProducts) {
            if (product.getCategory() != null && !product.getCategory().isEmpty()) {
                categories.add(product.getCategory());
            }
        }
        if (selectedCategory != null && !categories.contains(selectedCategory)) {
            selectedCategory = null;
        }

        buildingChips = true;
        categoryGroup.removeAllViews();
        addChip(getString(R.string.all_categories), null);
        for (String category : categories) {
            addChip(category, category);
        }
        for (int i = 0; i < categoryGroup.getChildCount(); i++) {
            Chip chip = (Chip) categoryGroup.getChildAt(i);
            chip.setChecked(Objects.equals(chip.getTag(), selectedCategory));
        }
        buildingChips = false;
    }

    private void addChip(String label, String tag) {
        Chip chip = new Chip(requireContext());
        chip.setId(View.generateViewId());
        chip.setText(label);
        chip.setTag(tag);
        chip.setCheckable(true);
        chip.setClickable(true);
        categoryGroup.addView(chip);
    }

    private void applyFilter() {
        List<Product> filtered = new ArrayList<>();
        for (Product product : allProducts) {
            if (selectedCategory == null || selectedCategory.equals(product.getCategory())) {
                filtered.add(product);
            }
        }
        progressBar.setVisibility(View.GONE);
        layoutError.setVisibility(View.GONE);
        adapter.submitList(filtered);

        boolean empty = filtered.isEmpty();
        recyclerView.setVisibility(empty ? View.GONE : View.VISIBLE);
        tvEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        tvEmpty.setText(selectedCategory == null
                ? R.string.empty_products
                : R.string.empty_category_products);
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        layoutError.setVisibility(View.GONE);
        tvEmpty.setVisibility(View.GONE);
    }

    private void showError(String message) {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        tvEmpty.setVisibility(View.GONE);
        layoutError.setVisibility(View.VISIBLE);
        tvError.setText(messageText(message));
    }

    private String messageText(String message) {
        return message == null || message.isEmpty()
                ? getString(R.string.load_failed)
                : message;
    }

    private void openDetail(Product product) {
        startActivity(ProductDetailActivity.createIntent(requireContext(), product));
    }
}
