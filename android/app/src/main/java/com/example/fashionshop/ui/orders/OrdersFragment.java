package com.example.fashionshop.ui.orders;

import android.os.Bundle;
import android.content.Intent;
import android.widget.Toast;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fashionshop.MainActivity;
import com.example.fashionshop.PaymentActivity;
import com.example.fashionshop.R;
import com.example.fashionshop.data.OrderManager;
import com.example.fashionshop.data.model.Order;

import java.util.List;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

/**
 * 订单列表：展示本地订单，按时间倒序。
 */
public class OrdersFragment extends Fragment {

    private RecyclerView recyclerView;
    private View emptyLayout;
    private OrderAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_orders, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recycler_orders);
        emptyLayout = view.findViewById(R.id.layout_orders_empty);
        adapter = new OrderAdapter(new OrderAdapter.Listener() {
            @Override public void onPay(Order order) {
                Intent intent = new Intent(requireContext(), PaymentActivity.class);
                intent.putExtra(PaymentActivity.EXTRA_ORDER_NO, order.getOrderNo());
                startActivity(intent);
            }
            @Override public void onCancel(Order order) {
                new MaterialAlertDialogBuilder(requireContext())
                        .setMessage(R.string.order_cancel_confirm)
                        .setNegativeButton(R.string.cancel, null)
                        .setPositiveButton(R.string.order_cancel, (dialog, which) -> {
                            boolean ok = OrderManager.cancelUnpaid(requireContext(), order.getOrderNo());
                            if (!ok) Toast.makeText(requireContext(), R.string.order_action_failed, Toast.LENGTH_SHORT).show();
                            reload();
                        }).show();
            }
            @Override public void onDelete(Order order) {
                new MaterialAlertDialogBuilder(requireContext())
                        .setMessage(R.string.order_delete_confirm)
                        .setNegativeButton(R.string.cancel, null)
                        .setPositiveButton(R.string.order_delete, (dialog, which) -> {
                            boolean ok = OrderManager.deleteOrder(requireContext(), order.getOrderNo());
                            if (!ok) Toast.makeText(requireContext(), R.string.order_action_failed, Toast.LENGTH_SHORT).show();
                            reload();
                        }).show();
            }
            @Override public void onOpen(Order order) {
                Order latest = OrderManager.findOrder(requireContext(), order.getOrderNo());
                if (latest == null) return;
                String shipping = latest.getRecipient() == null ? "—" : getString(R.string.shipping_summary,
                        latest.getRecipient(), latest.getPhone(), latest.getAddress());
                String method = latest.getPaymentMethod() == null ? getString(R.string.not_paid) : latest.getPaymentMethod();
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle(R.string.order_detail_title)
                        .setMessage(getString(R.string.order_detail_message, latest.getOrderNo(),
                                latest.getStatus(), getString(R.string.price_format, latest.getTotal()),
                                shipping, method))
                        .setPositiveButton(android.R.string.ok, null).show();
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        view.findViewById(R.id.btn_orders_go_shopping).setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).selectTab(R.id.nav_home);
            }
        });

        reload();
    }

    @Override
    public void onResume() {
        super.onResume();
        reload();
    }

    private void reload() {
        if (!isAdded() || getView() == null) {
            return;
        }
        List<Order> orders = OrderManager.getOrders(requireContext());
        adapter.submitList(orders);
        boolean empty = orders.isEmpty();
        emptyLayout.setVisibility(empty ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(empty ? View.GONE : View.VISIBLE);
    }
}
