package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class BoatActivity extends AppCompatActivity {
    private Button addComponentButton;
    private BreadBoardView breadboardView;

    private static final int PICK_COMPONENT_REQUEST = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boat);

        breadboardView = findViewById(R.id.breadboardView);
        addComponentButton = findViewById(R.id.addComponentButton);

        if (addComponentButton != null) {
            addComponentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    launchComponentPicker();
                }
            });
        }
    }

    private void launchComponentPicker() {
        Intent intent = new Intent(BoatActivity.this, com.example.myapplication.ComponentListActivity.class);
        startActivityForResult(intent, PICK_COMPONENT_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_COMPONENT_REQUEST && resultCode == RESULT_OK && data != null) {
            String componentName = data.getStringExtra("selectedComponent");

            if (componentName != null && breadboardView != null) {
                // 선택된 부품 이름에 따라 다른 동작을 하도록 수정
                if (componentName.contains("전선")) {
                    // '전선'이 선택된 경우, 그리기 모드로 진입
                    breadboardView.enterWireDrawingMode();
                    Toast.makeText(this, "전선 그리기 모드입니다. 홀을 터치하고 드래그하세요.", Toast.LENGTH_LONG).show();
                } else {
                    // 다른 부품이 선택된 경우, 부품 배치 모드로 진입
                    breadboardView.startPlacingComponent(componentName);
                }
            }
        }
    }
}