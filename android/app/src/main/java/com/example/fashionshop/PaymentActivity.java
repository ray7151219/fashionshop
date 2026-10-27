package com.example.fashionshop;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fashionshop.data.OrderManager;
import com.example.fashionshop.data.model.Order;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

/** 课程演示模拟支付：只更新本机订单状态，无第三方交易。 */
public class PaymentActivity extends AppCompatActivity {
    public static final String EXTRA_ORDER_NO = "order_no";
    private String orderNo;
    private boolean processing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payment);
        ((MaterialToolbar) findViewById(R.id.payment_toolbar))
                .setNavigationOnClickListener(v -> finish());
        orderNo = getIntent().getStringExtra(EXTRA_ORDER_NO);
        findViewById(R.id.payment_later).setOnClickListener(v -> openOrders());
        findViewById(R.id.payment_confirm).setOnClickListener(v -> confirmPayment());
        refresh();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    private void refresh() {
        Order order = OrderManager.findOrder(this, orderNo);
        if (order == null) {
            Toast.makeText(this, R.string.order_missing, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        ((TextView) findViewById(R.id.payment_order)).setText(getString(R.string.order_no_format, orderNo));
        ((TextView) findViewById(R.id.payment_amount)).setText(getString(R.string.price_format, order.getTotal()));
        ((TextView) findViewById(R.id.payment_address)).setText(getString(R.string.shipping_summary,
                order.getRecipient(), order.getPhone(), order.getAddress()));
        findViewById(R.id.payment_confirm).setEnabled(OrderManager.STATUS_UNPAID.equals(order.getStatus()) && !processing);
        findViewById(R.id.payment_methods).setVisibility(OrderManager.STATUS_UNPAID.equals(order.getStatus()) ? View.VISIBLE : View.GONE);
        ((TextView) findViewById(R.id.payment_notice)).setText(OrderManager.STATUS_UNPAID.equals(order.getStatus())
                ? R.string.payment_demo_notice : R.string.payment_current_status);
    }

    private void confirmPayment() {
        if (processing) return;
        RadioGroup group = findViewById(R.id.payment_methods);
        int selected = group.getCheckedRadioButtonId();
        if (selected == -1) {
            Toast.makeText(this, R.string.payment_select_method, Toast.LENGTH_SHORT).show();
            return;
        }
        String method = selected == R.id.payment_alipay ? getString(R.string.payment_alipay)
                : getString(R.string.payment_wechat);
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.payment_confirm_title)
                .setMessage(getString(R.string.payment_confirm_message, method))
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.payment_confirm_action, (dialog, which) -> {
                    processing = true;
                    if (OrderManager.simulatePayment(this, orderNo, method)) {
                        new MaterialAlertDialogBuilder(this)
                                .setTitle(R.string.payment_success)
                                .setMessage(R.string.payment_success_detail)
                                .setCancelable(false)
                                .setPositiveButton(R.string.view_orders, (d, w) -> openOrders())
                                .show();
                    } else {
                        processing = false;
                        Toast.makeText(this, R.string.payment_failed, Toast.LENGTH_LONG).show();
                        refresh();
                    }
                }).show();
    }

    private void openOrders() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(MainActivity.EXTRA_OPEN_TAB, R.id.nav_orders);
        startActivity(intent);
        finish();
    }
}
