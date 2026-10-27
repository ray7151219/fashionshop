package com.example.fashionshop.ui.cart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fashionshop.MainActivity;
import com.example.fashionshop.ProductDetailActivity;
import com.example.fashionshop.R;
import com.example.fashionshop.data.CartManager;
import com.example.fashionshop.data.OrderManager;
import com.example.fashionshop.data.model.CartItem;
import com.example.fashionshop.data.model.Order;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

/**
 * 购物车：数量增减、勾选、全选、删除、合计与结算。
 */
public class CartFragment extends Fragment {

    private RecyclerView recyclerView;
    private View emptyLayout;
    private View bottomLayout;
    private MaterialCheckBox cbSelectAll;
    private TextView tvTotal;
    private MaterialButton btnCheckout;
    private CartAdapter adapter;
    private boolean updatingSelectAll;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recycler_cart);
        emptyLayout = view.findViewById(R.id.layout_cart_empty);
        bottomLayout = view.findViewById(R.id.layout_cart_bottom);
        cbSelectAll = view.findViewById(R.id.cb_select_all);
        tvTotal = view.findViewById(R.id.tv_cart_total);
        btnCheckout = view.findViewById(R.id.btn_checkout);

        adapter = new CartAdapter(new CartAdapter.Listener() {
            @Override
            public void onCartChanged() {
                reload();
            }

            @Override
            public void onSelectionChanged() {
                updateSummary();
                updateBadge();
            }

            @Override
            public void onOpenProduct(CartItem item) {
                startActivity(ProductDetailActivity.createIntent(
                        requireContext(), item.toProduct()));
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        cbSelectAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (updatingSelectAll) {
                return;
            }
            CartManager.setAllSelected(requireContext(), isChecked);
            reload();
        });

        btnCheckout.setOnClickListener(v -> checkout());
        view.findViewById(R.id.btn_go_home).setOnClickListener(v -> openHome());

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
        List<CartItem> items = CartManager.getItems(requireContext());
        adapter.submitList(items);

        boolean empty = items.isEmpty();
        emptyLayout.setVisibility(empty ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(empty ? View.GONE : View.VISIBLE);
        bottomLayout.setVisibility(empty ? View.GONE : View.VISIBLE);

        updateSummary();
        updateBadge();
    }

    private void updateSummary() {
        if (!isAdded() || getView() == null) {
            return;
        }
        int selectedQuantity = CartManager.getSelectedQuantity(requireContext());
        tvTotal.setText(getString(R.string.price_format,
                CartManager.getSelectedTotal(requireContext())));
        btnCheckout.setEnabled(selectedQuantity > 0);
        btnCheckout.setText(selectedQuantity > 0
                ? getString(R.string.checkout_with_count, selectedQuantity)
                : getString(R.string.checkout));

        updatingSelectAll = true;
        cbSelectAll.setChecked(CartManager.isAllSelected(requireContext()));
        updatingSelectAll = false;
    }

    private void updateBadge() {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).updateCartBadge();
        }
    }

    private void checkout() {
        List<CartItem> selected = CartManager.getSelectedItems(requireContext());
        if (selected.isEmpty()) {
            Toast.makeText(requireContext(), R.string.no_selected_items,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        startActivity(new android.content.Intent(requireContext(),
                com.example.fashionshop.CheckoutActivity.class));
    }

    private void openHome() {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).selectTab(R.id.nav_home);
        }
    }
}
