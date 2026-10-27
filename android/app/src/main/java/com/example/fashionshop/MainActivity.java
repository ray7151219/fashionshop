package com.example.fashionshop;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.fashionshop.data.CartManager;
import com.example.fashionshop.data.SessionManager;
import com.example.fashionshop.net.ApiConfig;
import com.example.fashionshop.ui.cart.CartFragment;
import com.example.fashionshop.ui.home.HomeFragment;
import com.example.fashionshop.ui.orders.OrdersFragment;
import com.example.fashionshop.ui.profile.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_OPEN_TAB = "open_tab";

    private static final int REQUEST_LOCAL_NETWORK = 1001;
    private static final String PERMISSION_LOCAL_NETWORK =
            "android.permission.ACCESS_LOCAL_NETWORK";

    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SessionManager sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            goToLogin();
            return;
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        View root = findViewById(R.id.main_root);
        View fragmentContainer = findViewById(R.id.fragment_container);
        bottomNav = findViewById(R.id.bottom_nav);

        // 让状态栏区域留给内容背景，底部导航栏避开系统手势区
        ViewCompat.setOnApplyWindowInsetsListener(root, (view, windowInsets) -> {
            Insets bars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            fragmentContainer.setPadding(bars.left, bars.top, bars.right, 0);
            bottomNav.setPadding(bars.left, bottomNav.getPaddingTop(), bars.right, bars.bottom);
            return windowInsets;
        });

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment fragment = createFragment(item.getItemId());
            if (fragment == null) {
                return false;
            }
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        });

        if (savedInstanceState == null) {
            int requested = getIntent().getIntExtra(EXTRA_OPEN_TAB, R.id.nav_home);
            bottomNav.setSelectedItemId(requested == R.id.nav_orders ? R.id.nav_orders : R.id.nav_home);
        }
        updateCartBadge();
        requestLocalNetworkIfNeeded();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (intent.getIntExtra(EXTRA_OPEN_TAB, 0) == R.id.nav_orders) {
            selectTab(R.id.nav_orders);
        }
    }

    /**
     * Android 17 起访问 10.0.2.2 需要 ACCESS_LOCAL_NETWORK 权限，否则会超时。
     */
    private void requestLocalNetworkIfNeeded() {
        if (ApiConfig.USE_MOCK || Build.VERSION.SDK_INT < 37) {
            return;
        }
        if (ContextCompat.checkSelfPermission(this, PERMISSION_LOCAL_NETWORK)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{PERMISSION_LOCAL_NETWORK}, REQUEST_LOCAL_NETWORK);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != REQUEST_LOCAL_NETWORK) {
            return;
        }
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            recreate(); // 授权后重新加载当前页面
        } else {
            Toast.makeText(this, R.string.local_network_permission_denied,
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartBadge();
    }

    /**
     * 供购物车角标、详情页返回、个人中心跳转使用。
     */
    public void updateCartBadge() {
        if (bottomNav == null) {
            return;
        }
        int count = CartManager.getTotalQuantity(this);
        if (count > 0) {
            bottomNav.getOrCreateBadge(R.id.nav_cart).setNumber(count);
        } else {
            bottomNav.removeBadge(R.id.nav_cart);
        }
    }

    public void selectTab(int itemId) {
        if (bottomNav != null) {
            bottomNav.setSelectedItemId(itemId);
        }
    }

    private Fragment createFragment(int itemId) {
        if (itemId == R.id.nav_home) {
            return new HomeFragment();
        }
        if (itemId == R.id.nav_cart) {
            return new CartFragment();
        }
        if (itemId == R.id.nav_orders) {
            return new OrdersFragment();
        }
        if (itemId == R.id.nav_profile) {
            return new ProfileFragment();
        }
        return null;
    }

    private void goToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
