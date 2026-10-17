package com.goldenx.bypass400;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private boolean isEnabled = false;
    private TextView statusText;
    private Button toggleBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        statusText = new TextView(this);
        statusText.setText("Checking Root...");
        statusText.setTextSize(20);

        toggleBtn = new Button(this);
        toggleBtn.setText("Toggle Bypass 400");
        toggleBtn.setEnabled(false); // ปิดปุ่มไว้ก่อน
        toggleBtn.setOnClickListener(v -> {
            isEnabled = !isEnabled;
            String result = applyBypass(400, isEnabled);
            statusText.setText("Bypass 400: " + (isEnabled ? "ON" : "OFF") + "\n" + result);
            Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
        });

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 50, 50, 50);
        layout.addView(statusText);
        layout.addView(toggleBtn);
        setContentView(layout);

        // --- ตรวจสอบ Root ทันทีที่เปิดแอป ---
        new Thread(() -> {
            boolean hasRoot = RootUtils.checkAndRequestRoot();
            runOnUiThread(() -> {
                if (hasRoot) {
                    statusText.setText("Root: GRANTED ✅\nReady to use.");
                    toggleBtn.setEnabled(true);
                } else {
                    statusText.setText("Root: DENIED ❌\nPlease grant root permission.");
                    showRootDialog();
                }
            });
        }).start();
    }

    private void showRootDialog() {
        new AlertDialog.Builder(this)
            .setTitle("Root Required")
            .setMessage("This app requires Root permission.\nPlease grant root access in KernelSU-Next.")
            .setPositiveButton("Retry", (dialog, which) -> recreate())
            .setNegativeButton("Exit", (dialog, which) -> finish())
            .show();
    }

    public native String applyBypass(int patchId, boolean enable);

    static {
        System.loadLibrary("bypass400");
    }
}
