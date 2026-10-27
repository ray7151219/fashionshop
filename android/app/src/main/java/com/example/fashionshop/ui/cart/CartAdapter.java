package com.example.fashionshop.ui.cart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fashionshop.R;
import com.example.fashionshop.data.CartManager;
import com.example.fashionshop.data.model.CartItem;
import com.google.android.material.checkbox.MaterialCheckBox;

import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    public interface Listener {
        void onCartChanged();

        void onSelectionChanged();

        void onOpenProduct(CartItem item);
    }

    private final List<CartItem> items = new ArrayList<>();
    private final Listener listener;

    public CartAdapter(Listener listener) {
        this.listener = listener;
    }

    public void submitList(List<CartItem> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = items.get(position);
        Context context = holder.itemView.getContext();

        holder.tvName.setText(item.getName());
        String size = item.getSize();
        boolean hasSize = size != null && !size.isEmpty();
        holder.tvSize.setVisibility(hasSize ? View.VISIBLE : View.GONE);
        if (hasSize) {
            holder.tvSize.setText(context.getString(R.string.cart_size_format, size));
        }
        holder.tvPrice.setText(context.getString(R.string.price_format, item.getPrice()));
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));

        Glide.with(context)
                .load(item.getImageUrl())
                .placeholder(R.drawable.bg_placeholder)
                .error(R.drawable.bg_placeholder)
                .centerCrop()
                .into(holder.ivImage);

        holder.cbItem.setOnCheckedChangeListener(null);
        holder.cbItem.setChecked(item.isSelected());
        holder.cbItem.setOnCheckedChangeListener((buttonView, isChecked) -> {
            CartManager.setSelected(context, item.getProductId(), item.getSize(), isChecked);
            if (listener != null) {
                listener.onSelectionChanged();
            }
        });

        holder.btnMinus.setOnClickListener(v -> {
            if (item.getQuantity() > 1) {
                CartManager.updateQuantity(context, item.getProductId(), item.getSize(),
                        item.getQuantity() - 1);
            } else {
                CartManager.remove(context, item.getProductId(), item.getSize());
            }
            if (listener != null) {
                listener.onCartChanged();
            }
        });

        holder.btnPlus.setOnClickListener(v -> {
            if (item.getQuantity() < 99) {
                CartManager.updateQuantity(context, item.getProductId(), item.getSize(),
                        item.getQuantity() + 1);
                if (listener != null) {
                    listener.onCartChanged();
                }
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            CartManager.remove(context, item.getProductId(), item.getSize());
            if (listener != null) {
                listener.onCartChanged();
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onOpenProduct(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {

        final MaterialCheckBox cbItem;
        final ImageView ivImage;
        final TextView tvName;
        final TextView tvSize;
        final TextView tvPrice;
        final TextView tvQuantity;
        final TextView btnMinus;
        final TextView btnPlus;
        final TextView btnDelete;

        CartViewHolder(@NonNull View itemView) {
            super(itemView);
            cbItem = itemView.findViewById(R.id.cb_cart_item);
            ivImage = itemView.findViewById(R.id.iv_cart_image);
            tvName = itemView.findViewById(R.id.tv_cart_name);
            tvSize = itemView.findViewById(R.id.tv_cart_size);
            tvPrice = itemView.findViewById(R.id.tv_cart_price);
            tvQuantity = itemView.findViewById(R.id.tv_cart_quantity);
            btnMinus = itemView.findViewById(R.id.btn_cart_minus);
            btnPlus = itemView.findViewById(R.id.btn_cart_plus);
            btnDelete = itemView.findViewById(R.id.btn_cart_delete);
        }
    }
}
