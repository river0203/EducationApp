package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ComponentListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 1. 레이아웃 파일을 화면에 설정합니다.
        setContentView(R.layout.activity_component_list);

        // 2. 보여줄 부품 데이터 목록을 만듭니다.
        String[] components = {"건전지", "건전지 홀더", "스위치", "날개"};

        // 3. XML에 있는 ListView를 찾습니다.
        ListView listView = findViewById(R.id.componentListView);

        // 4. 데이터를 리스트뷰에 연결하기 위한 어댑터를 생성합니다.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, components);

        // 5. 리스트뷰에 어댑터를 설정합니다.
        listView.setAdapter(adapter);

        // 6. 리스트뷰의 아이템을 클릭했을 때의 동작을 설정합니다.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 클릭한 아이템의 텍스트(부품 이름)를 가져옵니다.
                String selectedComponent = (String) parent.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(), selectedComponent + " 선택됨", Toast.LENGTH_SHORT).show();

                // 이전 화면(BoatActivity)으로 결과를 돌려주기 위한 Intent를 생성합니다.
                Intent resultIntent = new Intent();
                // "selectedComponent" 라는 키로 선택된 부품 이름을 담습니다.
                resultIntent.putExtra("selectedComponent", selectedComponent);
                // 결과가 성공적임을 알리고(RESULT_OK), 데이터를 함께 보냅니다.
                setResult(RESULT_OK, resultIntent);

                // 현재 액티비티를 종료하여 이전 화면으로 돌아갑니다.
                finish();
            }
        });
    }
}