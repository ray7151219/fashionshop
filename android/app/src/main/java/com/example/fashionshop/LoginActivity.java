package com.example.fashionshop;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fashionshop.data.SessionManager;
import com.example.fashionshop.data.model.User;
import com.example.fashionshop.net.ApiCallback;
import com.example.fashionshop.net.ApiClient;
import com.example.fashionshop.net.ApiConfig;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    private static final int MIN_USERNAME_LENGTH = 3;
    private static final int MIN_PASSWORD_LENGTH = 6;

    private SessionManager sessionManager;
    private TextInputLayout tilUsername;
    private TextInputLayout tilPassword;
    private TextInputEditText etUsername;
    private TextInputEditText etPassword;
    private MaterialButton btnLogin;
    private boolean loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);
        if (sessionManager.isLoggedIn()) {
            openMain();
            return;//已经登录过 → 直接进主界面，不再显示登录页
        }//

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        tilUsername = findViewById(R.id.til_username);
        tilPassword = findViewById(R.id.til_password);
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        MaterialSwitch sourceSwitch = findViewById(R.id.switch_login_mock);
        sourceSwitch.setChecked(ApiConfig.USE_MOCK);
        updateSourceLabel();
        sourceSwitch.setOnCheckedChangeListener((button, checked) -> {
            if (loading || checked == ApiConfig.USE_MOCK) return;
            sessionManager.clear();
            ApiConfig.setUseMock(this, checked);
            updateSourceLabel();
            requestLocalNetworkIfNeeded();
        });
        requestLocalNetworkIfNeeded();

        btnLogin.setOnClickListener(v -> attemptLogin());
        findViewById(R.id.tv_go_register).setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));

        etPassword.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                attemptLogin();
                return true;
            }
            return false;
        });

        clearErrorOnInput(etUsername, tilUsername);
        clearErrorOnInput(etPassword, tilPassword);
    }

    private void attemptLogin() {
        if (loading) {
            return;
        }

        String username = textOf(etUsername);
        String password = etPassword.getText() == null ? "" : etPassword.getText().toString();
        tilUsername.setError(null);
        tilPassword.setError(null);

        if (username.length() < MIN_USERNAME_LENGTH) {
            tilUsername.setError(getString(R.string.error_username_short));
            etUsername.requestFocus();
            return;
        }
        if (password.length() < MIN_PASSWORD_LENGTH) {
            tilPassword.setError(getString(R.string.error_password_short));
            etPassword.requestFocus();
            return;
        }

        setLoading(true);
        ApiClient.login(username, password, new ApiCallback<User>() {
            @Override
            public void onSuccess(User user) {
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                setLoading(false);
                if (user == null || user.getUsername() == null || user.getUsername().isEmpty()) {
                    Toast.makeText(LoginActivity.this, R.string.login_invalid_response,
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                sessionManager.saveSession(user);
                Toast.makeText(LoginActivity.this,
                        getString(R.string.login_welcome, user.getUsername()),
                        Toast.LENGTH_SHORT).show();
                openMain();
            }

            @Override
            public void onError(String message) {
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                setLoading(false);
                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setLoading(boolean value) {
        loading = value;
        btnLogin.setEnabled(!value);
        btnLogin.setText(value ? R.string.action_logging_in : R.string.action_login);
        etUsername.setEnabled(!value);
        etPassword.setEnabled(!value);
        ((MaterialSwitch) findViewById(R.id.switch_login_mock)).setEnabled(!value);
    }

    private void updateSourceLabel() {
        ((android.widget.TextView) findViewById(R.id.tv_login_source)).setText(
                getString(R.string.login_source_label, getString(ApiConfig.USE_MOCK
                        ? R.string.data_source_mock : R.string.data_source_real)));
        findViewById(R.id.tv_login_demo_hint).setVisibility(ApiConfig.USE_MOCK
                ? android.view.View.VISIBLE : android.view.View.GONE);
    }

    private void requestLocalNetworkIfNeeded() {
        if (!ApiConfig.USE_MOCK && android.os.Build.VERSION.SDK_INT >= 37
                && checkSelfPermission("android.permission.ACCESS_LOCAL_NETWORK")
                != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{"android.permission.ACCESS_LOCAL_NETWORK"}, 1002);
        }
    }

    private void openMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private static String textOf(TextInputEditText editText) {
        Editable text = editText.getText();
        return text == null ? "" : text.toString().trim();
    }

    private static void clearErrorOnInput(TextInputEditText editText, TextInputLayout layout) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                layout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
}
