package com.example.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BreadBoardView extends View {

    private Paint breadboardPaint;
    private Paint componentPaint;

    // 추가된 부품들의 좌표를 저장할 리스트
    private List<PointF> components = new ArrayList<>();
    private Random random = new Random(); // 부품 위치를 랜덤하게 찍기 위한 객체

    // 생성자
    public BreadBoardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    // 초기화 메소드
    private void init() {
        breadboardPaint = new Paint();
        breadboardPaint.setColor(Color.parseColor("#F5DEB3")); // 밀짚 색
        breadboardPaint.setStyle(Paint.Style.FILL);

        componentPaint = new Paint();
        componentPaint.setColor(Color.RED);
        componentPaint.setStyle(Paint.Style.FILL);
    }

    /**
     * Activity에서 호출할 공개 메소드.
     * 새로운 부품(점)을 리스트에 추가하고 화면을 다시 그리도록 요청합니다.
     */
    public void addComponent() {
        // 뷰의 너비와 높이 안에서 랜덤한 위치 생성
        int x = random.nextInt(getWidth());
        int y = random.nextInt(getHeight());
        components.add(new PointF(x, y));
        invalidate(); // onDraw()를 다시 호출하여 화면을 갱신하라는 명령
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 1. 브레드보드 배경 그리기
        canvas.drawRect(0, 0, getWidth(), getHeight(), breadboardPaint);

        // 2. 저장된 모든 부품(점) 그리기
        for (PointF componentPos : components) {
            canvas.drawCircle(componentPos.x, componentPos.y, 20f, componentPaint);
        }
    }
}