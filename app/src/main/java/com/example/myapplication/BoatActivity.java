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

        // XML 레이아웃에서 BreadBoardView와 버튼 참조 가져오기
        // findViewById의 반환 값을 명시적으로 BreadBoardView로 캐스팅합니다.
        // 만약 R.id.breadboardView가 다른 타입의 뷰를 참조한다면, 여기서 ClassCastException이 발생합니다.
        breadboardView = (BreadBoardView) findViewById(R.id.breadboardView);
        addComponentButton = (Button) findViewById(R.id.addComponentButton);

        // breadboardView가 null이 아닌지 확인
        if (breadboardView != null) {
            // 초기 로드 시 점을 찍고 싶다면 이 줄의 주석을 해제하세요.
            // breadboardView.addComponent();
        } else {
            // 이 로그는 ClassCastException이 발생하면 도달하지 않을 수 있습니다.
            // 하지만 findViewById가 null을 반환하는 경우를 대비합니다.
            Log.e("BoatActivity", "BreadBoardView is null. Check your activity_boat.xml layout file for the correct ID (R.id.breadboardView) and ensure it's a BreadBoardView.");
        }

        // 버튼 클릭 리스너 설정
        if (addComponentButton != null) {
            addComponentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (breadboardView != null) {
                        // 뷰가 아직 레이아웃을 완료하지 않았을 경우를 대비하여
                        // post를 사용하여 레이아웃이 완료된 후 addComponent를 호출합니다.
                        breadboardView.post(new Runnable() {
                            @Override
                            public void run() {
                                breadboardView.addComponent();
                            }
                        });
                    } else {
                        Log.e("BoatActivity", "Cannot add component: breadboardView is null in click listener.");
                    }
                }
            });
        } else {
            Log.e("BoatActivity", "Add Component Button is null. Check your activity_boat.xml layout file for the correct ID (R.id.addComponentButton).");
        }
    }
}
