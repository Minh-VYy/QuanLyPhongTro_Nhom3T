package com.example.QuanLyPhongTro_App.ui.chatbot;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class DraggableChatbotView extends FloatingActionButton implements View.OnTouchListener {
    
    private float dX, dY;
    private int lastAction;
    private boolean isDragging = false;
    private static final int CLICK_THRESHOLD = 10; // pixels
    private float initialX, initialY;
    private ChatbotPositionManager positionManager;

    public DraggableChatbotView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public DraggableChatbotView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DraggableChatbotView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setOnTouchListener(this);
        positionManager = new ChatbotPositionManager(context);
        
        // Khôi phục vị trí đã lưu sau khi view được layout
        post(() -> restoreSavedPosition());
    }
    
    /**
     * Khôi phục vị trí đã lưu
     */
    private void restoreSavedPosition() {
        if (positionManager.hasPosition()) {
            float savedX = positionManager.getSavedX(getX());
            float savedY = positionManager.getSavedY(getY());
            setX(savedX);
            setY(savedY);
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                dX = view.getX() - event.getRawX();
                dY = view.getY() - event.getRawY();
                initialX = event.getRawX();
                initialY = event.getRawY();
                lastAction = MotionEvent.ACTION_DOWN;
                isDragging = false;
                break;

            case MotionEvent.ACTION_MOVE:
                float deltaX = Math.abs(event.getRawX() - initialX);
                float deltaY = Math.abs(event.getRawY() - initialY);
                
                if (deltaX > CLICK_THRESHOLD || deltaY > CLICK_THRESHOLD) {
                    isDragging = true;
                }

                if (isDragging) {
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                    
                    float newX = event.getRawX() + dX;
                    float newY = event.getRawY() + dY;

                    // Giới hạn trong màn hình
                    ViewGroup parent = (ViewGroup) view.getParent();
                    if (parent != null) {
                        int parentWidth = parent.getWidth();
                        int parentHeight = parent.getHeight();
                        
                        // Giới hạn X
                        if (newX < 0) newX = 0;
                        if (newX + view.getWidth() > parentWidth) {
                            newX = parentWidth - view.getWidth();
                        }
                        
                        // Giới hạn Y
                        if (newY < 0) newY = 0;
                        if (newY + view.getHeight() > parentHeight) {
                            newY = parentHeight - view.getHeight();
                        }
                    }

                    view.setX(newX);
                    view.setY(newY);
                    lastAction = MotionEvent.ACTION_MOVE;
                }
                break;

            case MotionEvent.ACTION_UP:
                if (!isDragging && lastAction == MotionEvent.ACTION_DOWN) {
                    // Đây là click, không phải drag
                    performClick();
                    return true;
                }
                
                // Lưu vị trí mới
                positionManager.savePosition(view.getX(), view.getY());
                
                // Snap to edge (tùy chọn)
                snapToEdge(view);
                break;

            default:
                return false;
        }
        return true;
    }

    @Override
    public boolean performClick() {
        super.performClick();
        return true;
    }

    /**
     * Tự động dính vào cạnh gần nhất (tùy chọn)
     */
    private void snapToEdge(View view) {
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent == null) return;

        int parentWidth = parent.getWidth();
        float viewCenterX = view.getX() + view.getWidth() / 2;

        // Dính vào cạnh trái hoặc phải
        if (viewCenterX < parentWidth / 2) {
            // Dính vào trái
            view.animate()
                .x(16) // margin 16dp
                .setDuration(200)
                .start();
        } else {
            // Dính vào phải
            view.animate()
                .x(parentWidth - view.getWidth() - 16)
                .setDuration(200)
                .start();
        }
    }
}
