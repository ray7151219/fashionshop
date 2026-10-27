package com.example.fashionshop;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fashionshop.data.model.User;
import com.example.fashionshop.net.ApiCallback;
import com.example.fashionshop.net.ApiClient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterActivity extends AppCompatActivity {

    private static final int MIN_USERNAME_LENGTH = 3;
    private static final int MIN_PASSWORD_LENGTH = 6;

    private TextInputLayout tilUsername;
    private TextInputLayout tilPassword;
    private TextInputLayout tilConfirmPassword;
    private TextInputEditText etUsername;
    private TextInputEditText etPassword;
    private TextInputEditText etConfirmPassword;
    private MaterialButton btnRegister;
    private boolean loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        tilUsername = findViewById(R.id.til_username);
        tilPassword = findViewById(R.id.til_password);
        tilConfirmPassword = findViewById(R.id.til_confirm_password);
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnRegister = findViewById(R.id.btn_register);

        btnRegister.setOnClickListener(v -> attemptRegister());
        findViewById(R.id.tv_go_login).setOnClickListener(v -> finish());

        etConfirmPassword.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                attemptRegister();
                return true;
            }
            return false;
        });

        clearErrorOnInput(etUsername, tilUsername);
        clearErrorOnInput(etPassword, tilPassword);
        clearErrorOnInput(etConfirmPassword, tilConfirmPassword);
    }

    private void attemptRegister() {
        if (loading) {
            return;
        }

        String username = textOf(etUsername);
        String password = etPassword.getText() == null ? "" : etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText() == null ? "" : etConfirmPassword.getText().toString();
        tilUsername.setError(null);
        tilPassword.setError(null);
        tilConfirmPassword.setError(null);

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
        if (!password.equals(confirmPassword)) {
            tilConfirmPassword.setError(getString(R.string.error_password_mismatch));
            etConfirmPassword.requestFocus();
            return;
        }

        setLoading(true);
        ApiClient.register(username, password, new ApiCallback<User>() {
            @Override
            public void onSuccess(User user) {
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                setLoading(false);
                Toast.makeText(RegisterActivity.this,
                        R.string.register_success, Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(String message) {
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                setLoading(false);
                Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setLoading(boolean value) {
        loading = value;
        btnRegister.setEnabled(!value);
        btnRegister.setText(value ? R.string.action_registering : R.string.action_register);
        etUsername.setEnabled(!value);
        etPassword.setEnabled(!value);
        etConfirmPassword.setEnabled(!value);
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
