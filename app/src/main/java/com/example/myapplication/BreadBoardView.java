package com.example.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BreadBoardView extends View {

    // 부품의 정보(위치, 이름, 색상)를 담는 내부 클래스
    private static class Component {
        Point gridPosition; // 그리드 좌표
        String name;
        int color;

        Component(Point gridPosition, String name, int color) {
            this.gridPosition = gridPosition;
            this.name = name;
            this.color = color;
        }
    }

    private Bitmap originalBitmap;
    private Bitmap scaledBitmap;
    private Paint componentPaint;

    // 이제 Point 대신 Component 객체를 리스트에 저장합니다.
    private List<Component> placedComponents = new ArrayList<>();
    private Random random = new Random();

    private float bitmapDrawX, bitmapDrawY;

    // 그리드 관련 상수들은 이전과 동일합니다.
    private static final float GRID_START_X_RATIO = 0.05f;
    private static final float GRID_START_Y_RATIO = 0.15f;
    private static final float GRID_WIDTH_RATIO = 0.90f;
    private static final float GRID_HEIGHT_RATIO = 0.70f;
    private static final int HORIZONTAL_HOLES = 63;
    private static final int VERTICAL_HOLES = 20;

    public BreadBoardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img);
        componentPaint = new Paint(Paint.ANTI_ALIAS_FLAG); // 안티에일리어싱 플래그 추가
        componentPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (originalBitmap != null && w > 0 && h > 0) {
            int viewWidth = w;
            float aspectRatio = (float) originalBitmap.getHeight() / originalBitmap.getWidth();
            int scaledHeight = (int) (viewWidth * aspectRatio);
            scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, viewWidth, scaledHeight, true);
            bitmapDrawX = (getWidth() - scaledBitmap.getWidth()) / 2f;
            bitmapDrawY = (getHeight() - scaledBitmap.getHeight()) / 2f;
        }
    }

    // BoatActivity에서 호출할 메소드
    public void addComponent(String componentName) {
        if (scaledBitmap == null) return;

        int col = random.nextInt(HORIZONTAL_HOLES);
        int row = random.nextInt(VERTICAL_HOLES);
        Point gridPos = new Point(col, row);

        // 부품 이름에 따라 색상을 다르게 지정합니다.
        int color = Color.RED; // 기본값
        if (componentName.contains("저항")) {
            color = Color.BLUE;
        } else if (componentName.contains("LED")) {
            color = Color.YELLOW;
        } else if (componentName.contains("커패시터")) {
            color = Color.GREEN;
        }

        placedComponents.add(new Component(gridPos, componentName, color));
        invalidate(); // 뷰를 다시 그리도록 요청합니다.
    }

    private Point getPixelForGridPoint(int col, int row) {
        float gridPixelWidth = scaledBitmap.getWidth() * GRID_WIDTH_RATIO;
        float gridPixelHeight = scaledBitmap.getHeight() * GRID_HEIGHT_RATIO;
        float horizontalSpacing = gridPixelWidth / (HORIZONTAL_HOLES - 1);
        float verticalSpacing = gridPixelHeight / (VERTICAL_HOLES - 1);
        float gridStartX = scaledBitmap.getWidth() * GRID_START_X_RATIO;
        float gridStartY = scaledBitmap.getHeight() * GRID_START_Y_RATIO;

        int x = (int) (bitmapDrawX + gridStartX + (col * horizontalSpacing));
        int y = (int) (bitmapDrawY + gridStartY + (row * verticalSpacing));

        return new Point(x, y);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (scaledBitmap != null) {
            canvas.drawBitmap(scaledBitmap, bitmapDrawX, bitmapDrawY, null);
        }

        // 저장된 부품들을 그립니다.
        for (Component component : placedComponents) {
            // 부품에 지정된 색상으로 Paint를 설정합니다.
            componentPaint.setColor(component.color);

            Point pixelPoint = getPixelForGridPoint(component.gridPosition.x, component.gridPosition.y);
            canvas.drawCircle(pixelPoint.x, pixelPoint.y, 10f, componentPaint);
        }
    }
}