package com.goldenx.bypass400;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private boolean isEnabled = false;
    private TextView statusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        statusText = new TextView(this);
        statusText.setText("Bypass 400: OFF");
        statusText.setTextSize(24);

        Button toggleBtn = new Button(this);
        toggleBtn.setText("Toggle Bypass 400");
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
    }

    public native String applyBypass(int patchId, boolean enable);

    static {
        System.loadLibrary("bypass400");
    }
}
