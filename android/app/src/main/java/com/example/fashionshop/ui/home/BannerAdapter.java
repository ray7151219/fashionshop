package com.example.fashionshop.ui.home;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fashionshop.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 首页顶部轮播图的 Adapter（配合 ViewPager2 使用）。
 * 每张轮播图是一块带标题、副标题和背景色的卡片。
 */
public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {

    /** 一张轮播图：标题 + 副标题 + 背景色。 */
    public static class Banner {
        final String title;
        final String subtitle;
        final int color;

        public Banner(String title, String subtitle, int color) {
            this.title = title;
            this.subtitle = subtitle;
            this.color = color;
        }
    }

    private final List<Banner> banners = new ArrayList<>();

    public void submitList(List<Banner> newBanners) {
        banners.clear();
        if (newBanners != null) {
            banners.addAll(newBanners);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_banner, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        Banner banner = banners.get(position);
        holder.tvTitle.setText(banner.title);
        holder.tvSubtitle.setText(banner.subtitle);

        // 用 GradientDrawable 画一个圆角纯色背景
        float radius = holder.itemView.getResources().getDisplayMetrics().density * 16;
        GradientDrawable background = new GradientDrawable();
        background.setCornerRadius(radius);
        background.setColor(banner.color);
        holder.root.setBackground(background);
    }

    @Override
    public int getItemCount() {
        return banners.size();
    }

    static class BannerViewHolder extends RecyclerView.ViewHolder {

        final View root;
        final TextView tvTitle;
        final TextView tvSubtitle;

        BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.banner_root);
            tvTitle = itemView.findViewById(R.id.tv_banner_title);
            tvSubtitle = itemView.findViewById(R.id.tv_banner_subtitle);
        }
    }
}
