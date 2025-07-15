package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class BoatActivity extends AppCompatActivity {
    private Button addComponentButton;
    private BreadBoardView breadboardView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boat);

        breadboardView = (BreadBoardView) findViewById(R.id.breadboardView);
        addComponentButton = (Button) findViewById(R.id.addComponentButton);

        if (breadboardView == null) {
            Log.e("BoatActivity", "BreadBoardView is null. Check your XML ID.");
        }

        if (addComponentButton != null) {
            addComponentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (breadboardView != null) {
                        // post를 사용하는 것은 좋은 습관입니다.
                        // 뷰가 그려질 준비가 되었을 때 addComponent를 호출합니다.
                        breadboardView.post(new Runnable() {
                            @Override
                            public void run() {
                                breadboardView.addComponent();
                            }
                        });
                    }
                }
            });
        } else {
            Log.e("BoatActivity", "Button is null. Check your XML ID.");
        }
    }
}