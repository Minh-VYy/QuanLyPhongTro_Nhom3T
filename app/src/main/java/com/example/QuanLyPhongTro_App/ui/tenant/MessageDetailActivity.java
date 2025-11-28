package com.example.QuanLyPhongTro_App.ui.tenant;
import com.example.QuanLyPhongTro_App.R;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.QuanLyPhongTro_App.ui.tenant.Message;


public class MessageDetailActivity extends AppCompatActivity {

    private TextView tvRequesterName, tvBookingTime, tvRoomName, tvFullContent;
    private Button btnReply, btnDelete;
    private long messageId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_detail);

        // 1. Khởi tạo Views
        initViews();

        // 2. Lấy ID tin nhắn từ Intent
        if (getIntent().hasExtra("message_id")) {
            messageId = getIntent().getLongExtra("message_id", -1);
            if (messageId != -1) {
                // Tải dữ liệu chi tiết tin nhắn
                loadMessageDetails(messageId);
            } else {
                Toast.makeText(this, "ID tin nhắn không hợp lệ.", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(this, "Không có dữ liệu tin nhắn.", Toast.LENGTH_SHORT).show();
            finish();
        }

        // 3. Thiết lập sự kiện cho các nút
        setupActionButtons();
    }

    private void initViews() {
        tvRequesterName = findViewById(R.id.tv_detail_requester_name);
        tvBookingTime = findViewById(R.id.tv_detail_booking_time);
        tvRoomName = findViewById(R.id.tv_detail_room_name);
        tvFullContent = findViewById(R.id.tv_detail_full_content);
        btnReply = findViewById(R.id.btn_detail_reply);
        btnDelete = findViewById(R.id.btn_detail_delete);
    }

    private void loadMessageDetails(long id) {
        // *** LOGIC THỰC TẾ: Gọi API/DB để lấy tin nhắn dựa trên 'id' ***

        // Dữ liệu mẫu (Giả định bạn đã có đối tượng Message)
        Message message = new Message(
                id,
                "Nguyễn Văn A",
                "29/11/2025 lúc 15:30",
                "Chào bạn, tôi có thể xem phòng...",
                "Chào bạn, tôi rất quan tâm đến căn phòng này. Tôi có thể xem phòng vào khoảng 4 giờ chiều hôm nay (Thứ 6) được không? Vui lòng phản hồi sớm cho tôi biết nhé. Cảm ơn nhiều.",
                "Phòng trọ A (Mã 101)"
        );

        tvRequesterName.setText(message.getRequesterName());
        tvBookingTime.setText(message.getSentTime());
        tvRoomName.setText(message.getRoomName());
        tvFullContent.setText(message.getFullContent());
    }

    private void setupActionButtons() {
        // 1. Phản hồi/Chat
        btnReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // *** LOGIC THỰC TẾ: Chuyển sang màn hình Chat/Reply ***
                Toast.makeText(MessageDetailActivity.this, "Chuyển sang Chat với ID: " + messageId, Toast.LENGTH_SHORT).show();
            }
        });

        // 2. Xóa
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // *** LOGIC THỰC TẾ: Gọi API xóa tin nhắn ***
                // Sau khi xóa thành công, thông báo và kết thúc Activity
                Toast.makeText(MessageDetailActivity.this, "Đã xóa tin nhắn ID: " + messageId, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}