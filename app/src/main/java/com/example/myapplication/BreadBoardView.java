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

    private Bitmap originalBitmap;
    private Bitmap scaledBitmap;
    private Paint componentPaint;

    // 부품의 위치를 픽셀(PointF) 대신 그리드(행, 열)로 저장합니다.
    private List<Point> componentsOnGrid = new ArrayList<>();
    private Random random = new Random();

    // 브레드보드 이미지의 실제 좌표를 저장할 변수
    private float bitmapDrawX, bitmapDrawY;

    // =================================================================================
    // ▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼ 중요! ▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼
    // 사용하는 브레드보드 이미지(img.png)에 맞게 이 값들을 조정해야 합니다.
    // 이 값들은 이미지 전체 크기에 대한 비율입니다. (0.0 ~ 1.0)
    // =================================================================================
    // 예시: 첫 번째 홀이 이미지 왼쪽에서 5%, 위에서 15% 지점에 있다면 아래와 같이 설정
    private static final float GRID_START_X_RATIO = 0.05f; // 그리드 시작점 X좌표 비율
    private static final float GRID_START_Y_RATIO = 0.15f; // 그리드 시작점 Y좌표 비율

    // 예시: 그리드가 이미지 너비의 90%를 차지하고, 높이의 70%를 차지한다면
    private static final float GRID_WIDTH_RATIO = 0.90f;   // 그리드 전체 너비 비율
    private static final float GRID_HEIGHT_RATIO = 0.70f;  // 그리드 전체 높이 비율

    // 브레드보드의 실제 홀 개수
    private static final int HORIZONTAL_HOLES = 63; // 가로 홀 개수
    private static final int VERTICAL_HOLES = 20;   // 세로 홀 개수 (상단/하단 전원부 제외)
    // =================================================================================

    public BreadBoardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img);

        componentPaint = new Paint();
        componentPaint.setColor(Color.RED);
        componentPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // 뷰의 크기가 결정되면, 비율에 맞게 비트맵 크기를 조정합니다.
        if (originalBitmap != null && w > 0 && h > 0) {
            int viewWidth = w;
            // 원본 이미지의 비율에 따라 높이를 계산합니다.
            float aspectRatio = (float) originalBitmap.getHeight() / originalBitmap.getWidth();
            int scaledHeight = (int) (viewWidth * aspectRatio);

            scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, viewWidth, scaledHeight, true);

            // 비트맵이 그려질 시작 좌표를 계산합니다 (뷰의 중앙에 배치).
            bitmapDrawX = (getWidth() - scaledBitmap.getWidth()) / 2f;
            bitmapDrawY = (getHeight() - scaledBitmap.getHeight()) / 2f;
        }
    }

    public void addComponent() {
        if (scaledBitmap == null) return;

        // 추가할 부품의 그리드 위치를 무작위로 선택합니다.
        int col = random.nextInt(HORIZONTAL_HOLES);
        int row = random.nextInt(VERTICAL_HOLES);

        componentsOnGrid.add(new Point(col, row));
        invalidate(); // 뷰를 다시 그리도록 요청합니다.
    }

    /**
     * 그리드 좌표(행, 열)를 실제 캔버스 위의 픽셀 좌표로 변환합니다.
     * @param col 그리드 열 (0부터 시작)
     * @param row 그리드 행 (0부터 시작)
     * @return 캔버스에 그려질 Point 객체
     */
    private Point getPixelForGridPoint(int col, int row) {
        // 이미지 내에서 그리드가 차지하는 실제 픽셀 크기 계산
        float gridPixelWidth = scaledBitmap.getWidth() * GRID_WIDTH_RATIO;
        float gridPixelHeight = scaledBitmap.getHeight() * GRID_HEIGHT_RATIO;

        // 홀 하나당 간격 계산
        float horizontalSpacing = gridPixelWidth / (HORIZONTAL_HOLES - 1);
        float verticalSpacing = gridPixelHeight / (VERTICAL_HOLES - 1);

        // 이미지 내에서 그리드의 시작 픽셀 좌표 계산
        float gridStartX = scaledBitmap.getWidth() * GRID_START_X_RATIO;
        float gridStartY = scaledBitmap.getHeight() * GRID_START_Y_RATIO;

        // 최종 픽셀 좌표 계산
        // (비트맵의 시작 위치 + 그리드의 시작 위치 + (간격 * 그리드 인덱스))
        int x = (int) (bitmapDrawX + gridStartX + (col * horizontalSpacing));
        int y = (int) (bitmapDrawY + gridStartY + (row * verticalSpacing));

        return new Point(x, y);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 1. 크기가 조정된 비트맵을 뷰의 중앙에 그립니다.
        if (scaledBitmap != null) {
            canvas.drawBitmap(scaledBitmap, bitmapDrawX, bitmapDrawY, null);
        }

        // 2. 추가된 모든 부품들을 그리드 위치에 맞게 그립니다.
        for (Point gridPoint : componentsOnGrid) {
            Point pixelPoint = getPixelForGridPoint(gridPoint.x, gridPoint.y); // col, row
            canvas.drawCircle(pixelPoint.x, pixelPoint.y, 10f, componentPaint);
        }
    }
}