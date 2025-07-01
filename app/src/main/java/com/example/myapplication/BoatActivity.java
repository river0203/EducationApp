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

        breadboardView = findViewById(R.id.boat);
        if (breadboardView != null) {
            breadboardView.addComponent();  // 점 찍기 같은 메서드
        } else {
            Log Log = null;
            Log.e("BoatActivity", "BreadBoardView is null");
        }
        addComponentButton = findViewById(R.id.addComponentButton);


        // 클릭 리스너 설정
        addComponentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 커스텀 뷰의 메서드 호출
                breadboardView.addComponent();
            }
        });
    }
}
