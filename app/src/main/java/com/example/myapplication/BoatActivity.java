package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class BoatActivity extends AppCompatActivity {
    private Button addComponentButton;
    private BreadBoardView breadboardView;

    // 다른 액티비티를 호출하고 결과를 돌려받을 때 사용할 요청 코드 (고유한 숫자면 됨)
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
                    // 부품 선택 화면으로 이동하는 함수 호출
                    launchComponentPicker();
                }
            });
        }
    }

    // 부품 선택 액티비티를 시작하는 함수
    private void launchComponentPicker() {
        // ComponentListActivity로 가는 Intent(이동 의도) 생성
        Intent intent = new Intent(BoatActivity.this, com.example.myapplication.ComponentListActivity.class);
        // 결과를 돌려받기 위해 startActivityForResult 사용
        startActivityForResult(intent, PICK_COMPONENT_REQUEST);
    }

    // 다른 액티비티에서 결과가 돌아왔을 때 자동으로 호출되는 메소드
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 1. 내가 보낸 요청이 맞는지 확인 (요청 코드 비교)
        // 2. 결과가 성공적인지 확인 (결과 코드 비교)
        // 3. 돌아온 데이터가 있는지 확인
        if (requestCode == PICK_COMPONENT_REQUEST && resultCode == RESULT_OK && data != null) {
            // "selectedComponent" 키로 보냈던 부품 이름을 다시 받습니다.
            String componentName = data.getStringExtra("selectedComponent");

            if (componentName != null && breadboardView != null) {
                // BreadBoardView에 선택된 부품을 추가하도록 요청합니다.
                breadboardView.addComponent(componentName);
                Toast.makeText(this, componentName + " 추가됨", Toast.LENGTH_SHORT).show();
            }
        }
    }
}