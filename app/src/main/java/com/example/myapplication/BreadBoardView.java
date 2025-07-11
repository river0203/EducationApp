package com.example.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

    private Bitmap originalBitmap;
    private Bitmap scaledBitmap; // 크기가 조정된 비트맵
    private Paint componentPaint;

    private List<PointF> components = new ArrayList<>();
    private Random random = new Random();

    public BreadBoardView(Context context) {
        super(context);
        init(context);
    }

    public BreadBoardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        // 원본 이미지를 로드합니다.
        originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img);

        // 부품(점)을 그릴 Paint 객체 설정
        componentPaint = new Paint();
        componentPaint.setColor(Color.RED);
        componentPaint.setStyle(Paint.Style.FILL);

        // 원하는 크기를 DP 단위로 설정
        int desiredWidthDp = 1000;
        int desiredHeightDp = 800;

        // DP를 PX로 변환
        float density = context.getResources().getDisplayMetrics().density;
        int desiredWidthPx = (int) (desiredWidthDp * density);
        int desiredHeightPx = (int) (desiredHeightDp * density);

        // 원본 비트맵이 로드되었는지 확인 후 크기 조정
        if (originalBitmap != null) {
            scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, desiredWidthPx, desiredHeightPx, true);
        }
    }

    public void addComponent() {
        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }
        int x = random.nextInt(getWidth());
        int y = random.nextInt(getHeight());
        components.add(new PointF(x, y));
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (scaledBitmap != null) {
            // 뷰(캔버스)의 중앙 X 좌표 계산
            float canvasCenterX = getWidth() / 2f;
            // 비트맵의 중앙 X 좌표 계산
            float bitmapCenterX = scaledBitmap.getWidth() / 2f;
            // 비트맵을 그릴 시작 X 좌표
            float drawX = canvasCenterX - bitmapCenterX;

            // 뷰(캔버스)의 중앙 Y 좌표 계산
            float canvasCenterY = getHeight() / 2f;
            // 비트맵의 중앙 Y 좌표 계산
            float bitmapCenterY = scaledBitmap.getHeight() / 2f;
            // 비트맵을 그릴 시작 Y 좌표
            float drawY = canvasCenterY - bitmapCenterY;

            // 계산된 위치에 비트맵을 그립니다.
            canvas.drawBitmap(scaledBitmap, drawX, drawY, null);
        }

        // 2. 추가된 모든 부품(점)들을 그립니다.
        //    (이 부분은 그대로 둡니다)
        for (PointF point : components) {
            canvas.drawCircle(point.x, point.y, 10f, componentPaint);
        }
    }
}