package com.example.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class BreadBoardView extends View {

    // 부품의 정보(리소스ID, 비트맵, 위치/크기, 선택상태)를 담는 내부 클래스
    private static class Component {
        int imageResId;
        String name;
        Bitmap bitmap;
        RectF bounds;
        boolean isSelected = false;

        Component(int imageResId, String name, Bitmap bitmap, RectF bounds) {
            this.imageResId = imageResId;
            this.name = name;
            this.bitmap = bitmap;
            this.bounds = bounds;
        }
    }

    // 전선 정보를 담는 내부 클래스
    private static class Wire {
        Point startGridPoint;
        Point endGridPoint;
        Paint paint;

        Wire(Point start, Point end, int color) {
            this.startGridPoint = start;
            this.endGridPoint = end;
            this.paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            this.paint.setColor(color);
            this.paint.setStrokeWidth(5f);
            this.paint.setStrokeCap(Paint.Cap.ROUND);
        }
    }

    // 터치 모드 상수
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int RESIZE = 2;
    private static final int DRAW_WIRE = 3; // 전선 그리기 모드
    private static final int PLACE = 4;     // 부품 배치 모드
    private int mode = NONE;

    // 조립 성공 여부 확인을 위한 플래그
    private boolean assemblySuccessNotified = false;

    private Bitmap originalBitmap;
    private Bitmap scaledBitmap;
    private List<Component> placedComponents = new ArrayList<>();
    private List<Wire> placedWires = new ArrayList<>();
    private Component selectedComponent = null;
    private Component placingComponent = null; // 현재 배치(드래그) 중인 부품

    private Paint selectionPaint;
    private Paint wirePaint;

    // 현재 그리고 있는 전선 정보
    private Wire currentDrawingWire = null;
    private PointF currentWireEndPoint = new PointF();

    private float bitmapDrawX, bitmapDrawY;

    // 터치 이벤트 처리를 위한 변수
    private PointF lastTouch = new PointF();
    private float oldDist = 1f;

    // 그리드 관련 상수
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

        selectionPaint = new Paint();
        selectionPaint.setColor(Color.CYAN);
        selectionPaint.setStyle(Paint.Style.STROKE);
        selectionPaint.setStrokeWidth(5f);

        wirePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        wirePaint.setColor(Color.RED);
        wirePaint.setStrokeWidth(5f);
        wirePaint.setStrokeCap(Paint.Cap.ROUND);
    }

    public void enterWireDrawingMode() {
        if (selectedComponent != null) {
            selectedComponent.isSelected = false;
            selectedComponent = null;
        }
        placingComponent = null; // 부품 배치 중이었다면 취소
        this.mode = DRAW_WIRE;
        invalidate();
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

    public void startPlacingComponent(String componentName) {
        if (selectedComponent != null) {
            selectedComponent.isSelected = false;
            selectedComponent = null;
        }
        currentDrawingWire = null;
        mode = NONE;

        placingComponent = createComponent(componentName);

        if (placingComponent != null) {
            mode = PLACE;
            Toast.makeText(getContext(), "부품을 드래그하여 원하는 위치에 놓으세요.", Toast.LENGTH_LONG).show();
            invalidate();
        }
    }

    private Component createComponent(String componentName) {
        if (scaledBitmap == null) return null;

        int imageResId = 0;

        // ▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼
        // 오류 수정을 위해 조건문 순서 변경
        // ▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼
        if (componentName.contains("건전지 홀더")) {
            imageResId = R.drawable.chargerholder;
        } else if (componentName.contains("건전지")) {
            imageResId = R.drawable.charger;
        } else if (componentName.contains("스위치")) {
            imageResId = R.drawable.dipswitch;
        } else if (componentName.contains("날개")) {
            imageResId = R.drawable.wing;
        }
        // ▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲

        if (imageResId != 0) {
            Bitmap componentBitmap = BitmapFactory.decodeResource(getResources(), imageResId);
            Bitmap scaledComponentBitmap = Bitmap.createScaledBitmap(componentBitmap, 150, 150, true);
            RectF bounds = new RectF(0, 0, scaledComponentBitmap.getWidth(), scaledComponentBitmap.getHeight());
            return new Component(imageResId, componentName, scaledComponentBitmap, bounds);
        }
        return null;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mode == DRAW_WIRE) {
            handleWireDrawing(event);
            return true;
        }

        if (mode == PLACE) {
            handlePlacement(event);
            return true;
        }

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mode = DRAG;
                lastTouch.set(event.getX(), event.getY());
                selectedComponent = null;
                for (int i = placedComponents.size() - 1; i >= 0; i--) {
                    Component component = placedComponents.get(i);
                    if (component.bounds.contains(lastTouch.x, lastTouch.y)) {
                        selectedComponent = component;
                        break;
                    }
                }
                for (Component c : placedComponents) {
                    c.isSelected = (c == selectedComponent);
                }
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                if (selectedComponent != null) {
                    oldDist = spacing(event);
                    if (oldDist > 10f) {
                        mode = RESIZE;
                    }
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG && selectedComponent != null) {
                    float dx = event.getX() - lastTouch.x;
                    float dy = event.getY() - lastTouch.y;
                    selectedComponent.bounds.offset(dx, dy);
                    lastTouch.set(event.getX(), event.getY());
                } else if (mode == RESIZE && selectedComponent != null && event.getPointerCount() >= 2) {
                    float newDist = spacing(event);
                    if (newDist > 10f) {
                        float scale = newDist / oldDist;
                        float centerX = selectedComponent.bounds.centerX();
                        float centerY = selectedComponent.bounds.centerY();
                        float newWidth = selectedComponent.bounds.width() * scale;
                        float newHeight = selectedComponent.bounds.height() * scale;
                        selectedComponent.bounds.set(centerX - newWidth / 2, centerY - newHeight / 2, centerX + newWidth / 2, centerY + newHeight / 2);
                    }
                    oldDist = newDist;
                }
                break;

            case MotionEvent.ACTION_UP:
                if (mode == DRAG && selectedComponent != null) {
                    Point gridPoint = mapPixelToGridPoint(selectedComponent.bounds.centerX(), selectedComponent.bounds.centerY());
                    if (gridPoint != null) {
                        Point pixelPos = getPixelForGridPoint(gridPoint.x, gridPoint.y);
                        selectedComponent.bounds.offsetTo(pixelPos.x - selectedComponent.bounds.width() / 2, pixelPos.y - selectedComponent.bounds.height() / 2);
                        Toast.makeText(getContext(), selectedComponent.name + " 위치 조정됨", Toast.LENGTH_SHORT).show();
                        checkAssemblySuccess();
                    } else {
                        Toast.makeText(getContext(), "부품은 브레드보드 위에 있어야 합니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;
        }

        invalidate();
        return true;
    }

    private void handlePlacement(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        if (placingComponent == null) {
            mode = NONE;
            return;
        }

        placingComponent.bounds.offsetTo(x - placingComponent.bounds.width() / 2, y - placingComponent.bounds.height() / 2);

        if (event.getAction() == MotionEvent.ACTION_UP) {
            Point gridPoint = mapPixelToGridPoint(x, y);
            if (gridPoint != null) {
                Point pixelPos = getPixelForGridPoint(gridPoint.x, gridPoint.y);
                placingComponent.bounds.offsetTo(pixelPos.x - placingComponent.bounds.width() / 2, pixelPos.y - placingComponent.bounds.height() / 2);
                placedComponents.add(placingComponent);
                Toast.makeText(getContext(), placingComponent.name + " 장착 완료", Toast.LENGTH_SHORT).show();
                checkAssemblySuccess();
            } else {
                Toast.makeText(getContext(), "브레드보드 위에 배치해야 합니다.", Toast.LENGTH_SHORT).show();
            }
            placingComponent = null;
            mode = NONE;
        }

        invalidate();
    }

    private void handleWireDrawing(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Point startGridPoint = mapPixelToGridPoint(x, y);
                if (startGridPoint != null) {
                    Point startPixelPoint = getPixelForGridPoint(startGridPoint.x, startGridPoint.y);
                    currentWireEndPoint.set(startPixelPoint.x, startPixelPoint.y);
                    currentDrawingWire = new Wire(startGridPoint, startGridPoint, Color.RED);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (currentDrawingWire != null) {
                    currentWireEndPoint.set(x, y);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (currentDrawingWire != null) {
                    Point endGridPoint = mapPixelToGridPoint(x, y);
                    if (endGridPoint != null && !currentDrawingWire.startGridPoint.equals(endGridPoint)) {
                        currentDrawingWire.endGridPoint = endGridPoint;
                        placedWires.add(currentDrawingWire);
                        Toast.makeText(getContext(), "전선 추가됨. 계속해서 그리세요.", Toast.LENGTH_SHORT).show();
                        checkAssemblySuccess();
                    }
                    currentDrawingWire = null;
                }
                break;
        }
        invalidate();
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private Point getPixelForGridPoint(int col, int row) {
        float gridPixelWidth = scaledBitmap.getWidth() * GRID_WIDTH_RATIO;
        float gridPixelHeight = scaledBitmap.getHeight() * GRID_HEIGHT_RATIO;
        float horizontalSpacing = gridPixelWidth / (HORIZONTAL_HOLES - 1);
        float verticalSpacing = gridPixelHeight / (VERTICAL_HOLES - 1);
        float gridStartX = bitmapDrawX + scaledBitmap.getWidth() * GRID_START_X_RATIO;
        float gridStartY = bitmapDrawY + scaledBitmap.getHeight() * GRID_START_Y_RATIO;
        int x = (int) (gridStartX + (col * horizontalSpacing));
        int y = (int) (gridStartY + (row * verticalSpacing));
        return new Point(x, y);
    }

    private Point mapPixelToGridPoint(float pixelX, float pixelY) {
        if (scaledBitmap == null) return null;

        float gridAreaStartX = bitmapDrawX + scaledBitmap.getWidth() * GRID_START_X_RATIO;
        float gridAreaEndX = gridAreaStartX + scaledBitmap.getWidth() * GRID_WIDTH_RATIO;
        float gridAreaStartY = bitmapDrawY + scaledBitmap.getHeight() * GRID_START_Y_RATIO;
        float gridAreaEndY = gridAreaStartY + scaledBitmap.getHeight() * GRID_HEIGHT_RATIO;

        if (pixelX < gridAreaStartX || pixelX > gridAreaEndX || pixelY < gridAreaStartY || pixelY > gridAreaEndY) {
            return null;
        }

        float relativeX = (pixelX - gridAreaStartX) / (scaledBitmap.getWidth() * GRID_WIDTH_RATIO);
        float relativeY = (pixelY - gridAreaStartY) / (scaledBitmap.getHeight() * GRID_HEIGHT_RATIO);

        int col = Math.round(relativeX * (HORIZONTAL_HOLES - 1));
        int row = Math.round(relativeY * (VERTICAL_HOLES - 1));

        return new Point(col, row);
    }

    /**
     * 필수 부품들이 모두 배치되었는지 확인하고, 성공 시 토스트 메시지를 표시하는 메서드
     */
    private void checkAssemblySuccess() {
        // 이미 성공 메시지를 표시했다면 다시 확인하지 않음
        if (assemblySuccessNotified) {
            return;
        }

        boolean hasBatteryHolder = false;
        boolean hasSwitch = false;
        boolean hasMotor = false; // '날개'를 모터 부품으로 간주

        for (Component c : placedComponents) {
            if (c.name.contains("건전지 홀더")) {
                hasBatteryHolder = true;
            } else if (c.name.contains("스위치")) {
                hasSwitch = true;
            } else if (c.name.contains("날개")) { // '날개'를 모터로 가정
                hasMotor = true;
            }
        }

        // 전선이 하나 이상 배치되었는지 확인
        boolean hasWires = !placedWires.isEmpty();

        // 모든 조건이 충족되면 성공 메시지 표시
        if (hasBatteryHolder && hasSwitch && hasMotor && hasWires) {
            Toast.makeText(getContext(), "🎉 조립 성공!", Toast.LENGTH_LONG).show();
            assemblySuccessNotified = true; // 성공 알림 플래그를 설정하여 중복 표시 방지
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (scaledBitmap != null) {
            canvas.drawBitmap(scaledBitmap, bitmapDrawX, bitmapDrawY, null);
        }

        for (Component component : placedComponents) {
            canvas.drawBitmap(component.bitmap, null, component.bounds, null);
            if (component.isSelected) {
                canvas.drawRect(component.bounds, selectionPaint);
            }
        }

        for (Wire wire : placedWires) {
            Point start = getPixelForGridPoint(wire.startGridPoint.x, wire.startGridPoint.y);
            Point end = getPixelForGridPoint(wire.endGridPoint.x, wire.endGridPoint.y);
            canvas.drawLine(start.x, start.y, end.x, end.y, wire.paint);
        }

        if (currentDrawingWire != null) {
            Point start = getPixelForGridPoint(currentDrawingWire.startGridPoint.x, currentDrawingWire.startGridPoint.y);
            canvas.drawLine(start.x, start.y, currentWireEndPoint.x, currentWireEndPoint.y, wirePaint);
        }

        if (placingComponent != null) {
            canvas.drawBitmap(placingComponent.bitmap, null, placingComponent.bounds, null);
        }
    }
}