package com.example.fashionshop;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fashionshop.data.CartManager;
import com.example.fashionshop.data.OrderManager;
import com.example.fashionshop.data.SessionManager;
import com.example.fashionshop.data.model.CartItem;
import com.example.fashionshop.data.model.Order;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

/** 本地结算：确认收货信息，再生成待付款订单。 */
public class CheckoutActivity extends AppCompatActivity {
    private TextInputEditText nameInput, phoneInput, addressInput;
    private boolean submitting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_checkout);
        ((MaterialToolbar) findViewById(R.id.checkout_toolbar))
                .setNavigationOnClickListener(v -> finish());
        nameInput = findViewById(R.id.checkout_name);
        phoneInput = findViewById(R.id.checkout_phone);
        addressInput = findViewById(R.id.checkout_address);
        String scope = new SessionManager(this).getStorageScope();
        android.content.SharedPreferences prefs = getSharedPreferences("fashion_shop_address", MODE_PRIVATE);
        nameInput.setText(prefs.getString(scope + "_name", ""));
        phoneInput.setText(prefs.getString(scope + "_phone", ""));
        addressInput.setText(prefs.getString(scope + "_address", ""));
        List<CartItem> selected = CartManager.getSelectedItems(this);
        if (selected.isEmpty()) {
            Toast.makeText(this, R.string.no_selected_items, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        int count = CartManager.getSelectedQuantity(this);
        ((TextView) findViewById(R.id.checkout_summary)).setText(getString(
                R.string.checkout_summary, count,
                getString(R.string.price_format, CartManager.getSelectedTotal(this))));
        findViewById(R.id.checkout_submit).setOnClickListener(v -> {
            if (submitting) return;
            String name = value(nameInput);
            String phone = value(phoneInput);
            String address = value(addressInput);
            if (name.length() < 2) { nameInput.setError(getString(R.string.checkout_name_error)); nameInput.requestFocus(); return; }
            if (!phone.matches("1[3-9][0-9]{9}")) { phoneInput.setError(getString(R.string.checkout_phone_error)); phoneInput.requestFocus(); return; }
            if (address.length() < 5) { addressInput.setError(getString(R.string.checkout_address_error)); addressInput.requestFocus(); return; }
            submitting = true;
            prefs.edit().putString(scope + "_name", name).putString(scope + "_phone", phone)
                    .putString(scope + "_address", address).apply();
            Order order = OrderManager.createOrder(this, CartManager.getSelectedItems(this), name, phone, address);
            if (order == null) {
                submitting = false;
                Toast.makeText(this, R.string.checkout_failed, Toast.LENGTH_LONG).show();
                return;
            }
            CartManager.removeSelected(this);
            Intent intent = new Intent(this, PaymentActivity.class);
            intent.putExtra(PaymentActivity.EXTRA_ORDER_NO, order.getOrderNo());
            startActivity(intent);
            finish();
        });
    }

    private static String value(TextInputEditText input) {
        return input.getText() == null ? "" : input.getText().toString().trim();
    }
}
