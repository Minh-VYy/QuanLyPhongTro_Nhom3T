package com.example.QuanLyPhongTro_App.ui.tenant;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.QuanLyPhongTro_App.R;
import com.example.QuanLyPhongTro_App.utils.BottomNavigationHelper;

import java.util.ArrayList;
import java.util.List;

public class NotificationsActivity extends AppCompatActivity {

    private RecyclerView notificationsRecyclerView;
    private LinearLayout emptyState;
    private ImageView btnMarkAllRead;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenant_notifications);

        initViews();
        setupToolbar();
        loadNotifications();
    }

    private void initViews() {
        notificationsRecyclerView = findViewById(R.id.notificationsRecyclerView);
        emptyState = findViewById(R.id.emptyState);
        btnMarkAllRead = findViewById(R.id.btnMarkAllRead);

        notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupToolbar() {
        // Toolbar trong layout là LinearLayout, không cần setSupportActionBar

        btnMarkAllRead.setOnClickListener(v -> {
            Toast.makeText(this, "Đánh dấu tất cả đã đọc", Toast.LENGTH_SHORT).show();
            // TODO: Mark all notifications as read
        });
    }

    private void setupBottomNavigation() {
        BottomNavigationHelper.setupBottomNavigation(this, "notification");
    }

    private void loadNotifications() {
        // Sử dụng một danh sách trống vì MockData đã bị xóa
        List<Notification> notifications = new ArrayList<>();

        if (notifications.isEmpty()) {
            notificationsRecyclerView.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
        } else {
            notificationsRecyclerView.setVisibility(View.VISIBLE);
            emptyState.setVisibility(View.GONE);

            NotificationAdapter adapter = new NotificationAdapter(this, notifications);
            notificationsRecyclerView.setAdapter(adapter);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupBottomNavigation();
    }
}
