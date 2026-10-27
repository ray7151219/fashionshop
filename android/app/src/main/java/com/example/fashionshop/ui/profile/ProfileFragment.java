package com.example.fashionshop.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fashionshop.LoginActivity;
import com.example.fashionshop.MainActivity;
import com.example.fashionshop.R;
import com.example.fashionshop.data.SessionManager;
import com.example.fashionshop.net.ApiConfig;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.materialswitch.MaterialSwitch;

public class ProfileFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SessionManager sessionManager = new SessionManager(requireContext());
        TextView tvUsername = view.findViewById(R.id.tv_username);
        tvUsername.setText(sessionManager.getUsername());

        view.findViewById(R.id.row_orders).setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).selectTab(R.id.nav_orders);
            }
        });

        MaterialSwitch switchMock = view.findViewById(R.id.switch_mock);
        TextView tvDataSourceDesc = view.findViewById(R.id.tv_data_source_desc);
        TextView tvMockTip = view.findViewById(R.id.tv_mock_tip);

        switchMock.setChecked(ApiConfig.USE_MOCK);
        updateDataSourceUi(tvDataSourceDesc, tvMockTip);
        switchMock.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked == ApiConfig.USE_MOCK) {
                return;
            }
            ApiConfig.setUseMock(requireContext(), isChecked);
            sessionManager.clear();
            Toast.makeText(requireContext(),
                    isChecked ? R.string.data_source_switch_to_mock
                            : R.string.data_source_switch_to_real,
                    Toast.LENGTH_SHORT).show();
            Intent login = new Intent(requireContext(), LoginActivity.class);
            login.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(login);
            requireActivity().finish();
        });

        view.findViewById(R.id.btn_logout).setOnClickListener(v ->
                showLogoutDialog(sessionManager));
    }

    private void updateDataSourceUi(TextView desc, TextView tip) {
        desc.setText(ApiConfig.USE_MOCK
                ? R.string.data_source_mock
                : R.string.data_source_real);
        tip.setVisibility(View.VISIBLE);
        tip.setText(ApiConfig.USE_MOCK
                ? R.string.mock_tip : R.string.backend_scope_tip);
    }

    private void showLogoutDialog(SessionManager sessionManager) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.logout_confirm_title)
                .setMessage(R.string.logout_confirm_message)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.logout, (dialog, which) ->
                        logout(sessionManager))
                .show();
    }

    private void logout(SessionManager sessionManager) {
        sessionManager.clear();
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}
