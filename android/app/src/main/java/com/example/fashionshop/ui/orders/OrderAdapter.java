package com.example.fashionshop.ui.orders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fashionshop.R;
import com.example.fashionshop.data.model.Order;
import com.example.fashionshop.data.OrderManager;
import com.example.fashionshop.data.model.OrderItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    public interface Listener {
        void onPay(Order order);
        void onCancel(Order order);
        void onOpen(Order order);
        void onDelete(Order order);
    }

    private final Listener listener;

    public OrderAdapter(Listener listener) { this.listener = listener; }

    private final List<Order> orders = new ArrayList<>();
    private final SimpleDateFormat dateFormat =
            new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);

    public void submitList(List<Order> newOrders) {
        orders.clear();
        if (newOrders != null) {
            orders.addAll(newOrders);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);
        Context context = holder.itemView.getContext();

        holder.tvOrderNo.setText(context.getString(R.string.order_no_format, order.getOrderNo()));
        holder.tvOrderStatus.setText(order.getStatus());
        holder.tvOrderTime.setText(dateFormat.format(new Date(order.getCreatedAt())));

        StringBuilder itemText = new StringBuilder();
        if (order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                if (itemText.length() > 0) {
                    itemText.append('\n');
                }
                String size = item.getSize();
                if (size == null || size.isEmpty()) {
                    itemText.append(context.getString(R.string.order_item_line,
                            item.getName(), item.getQuantity()));
                } else {
                    itemText.append(context.getString(R.string.order_item_line_with_size,
                            item.getName(), size, item.getQuantity()));
                }
            }
        }
        holder.tvOrderItems.setText(itemText.toString());
        holder.tvOrderCount.setText(context.getString(
                R.string.order_count_format, order.getTotalQuantity()));
        holder.tvOrderTotal.setText(context.getString(R.string.order_total_format,
                context.getString(R.string.price_format, order.getTotal())));
        boolean unpaid = OrderManager.STATUS_UNPAID.equals(order.getStatus());
        boolean deletable = OrderManager.STATUS_PAID.equals(order.getStatus())
                || OrderManager.STATUS_CANCELLED.equals(order.getStatus());
        holder.btnPay.setVisibility(unpaid ? View.VISIBLE : View.GONE);
        holder.btnCancel.setVisibility(unpaid ? View.VISIBLE : View.GONE);
        holder.btnDelete.setVisibility(deletable ? View.VISIBLE : View.GONE);
        holder.btnPay.setOnClickListener(v -> listener.onPay(order));
        holder.btnCancel.setOnClickListener(v -> listener.onCancel(order));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(order));
        holder.itemView.setOnClickListener(v -> listener.onOpen(order));
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {

        final TextView tvOrderNo;
        final TextView tvOrderStatus;
        final TextView tvOrderTime;
        final TextView tvOrderItems;
        final TextView tvOrderCount;
        final TextView tvOrderTotal;
        final View btnPay;
        final View btnCancel;
        final View btnDelete;

        OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderNo = itemView.findViewById(R.id.tv_order_no);
            tvOrderStatus = itemView.findViewById(R.id.tv_order_status);
            tvOrderTime = itemView.findViewById(R.id.tv_order_time);
            tvOrderItems = itemView.findViewById(R.id.tv_order_items);
            tvOrderCount = itemView.findViewById(R.id.tv_order_count);
            tvOrderTotal = itemView.findViewById(R.id.tv_order_total);
            btnPay = itemView.findViewById(R.id.btn_order_pay);
            btnCancel = itemView.findViewById(R.id.btn_order_cancel);
            btnDelete = itemView.findViewById(R.id.btn_order_delete);
        }
    }
}
