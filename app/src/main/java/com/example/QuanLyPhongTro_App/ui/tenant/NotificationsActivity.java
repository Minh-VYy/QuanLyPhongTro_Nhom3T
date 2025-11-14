package com.example.QuanLyPhongTro_App.ui.tenant;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
    private NotificationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenant_notifications);

        initViews();
        setupToolbar();
        setupBottomNavigation();
        loadNotifications();
    }

    private void initViews() {
        notificationsRecyclerView = findViewById(R.id.notificationsRecyclerView);
        emptyState = findViewById(R.id.emptyState);
        btnMarkAllRead = findViewById(R.id.btnMarkAllRead);

        notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btnMarkAllRead.setOnClickListener(v -> {
            Toast.makeText(this, "Đánh dấu tất cả đã đọc", Toast.LENGTH_SHORT).show();
            // TODO: Mark all notifications as read
        });
    }

    private void setupBottomNavigation() {
        BottomNavigationHelper.setupBottomNavigation(this, "notification");
    }

    private void loadNotifications() {
        // TODO: Load from database
        List<Notification> notifications = getSampleNotifications();

        if (notifications.isEmpty()) {
            notificationsRecyclerView.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
        } else {
            notificationsRecyclerView.setVisibility(View.VISIBLE);
            emptyState.setVisibility(View.GONE);

            adapter = new NotificationAdapter(this, notifications);
            notificationsRecyclerView.setAdapter(adapter);
        }
    }

    private List<Notification> getSampleNotifications() {
        List<Notification> notifications = new ArrayList<>();
        notifications.add(new Notification(
            "Lịch xem phòng đã được xác nhận",
            "Chủ trọ A đã xác nhận lịch 19:00 ngày 21/08/2024",
            "10 phút trước",
            "calendar",
            true
        ));
        notifications.add(new Notification(
            "Phòng mới phù hợp với bạn",
            "Phòng trọ cao cấp gần trường, giá 2.5 triệu/tháng",
            "2 giờ trước",
            "home",
            false
        ));
        notifications.add(new Notification(
            "Tin nhắn mới từ chủ trọ",
            "Chủ trọ B đã gửi tin nhắn cho bạn",
            "1 ngày trước",
            "message",
            false
        ));
        return notifications;
    }
}

