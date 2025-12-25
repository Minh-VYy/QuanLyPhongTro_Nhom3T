package com.example.QuanLyPhongTro_App.ui.landlord;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.QuanLyPhongTro_App.R;
import com.example.QuanLyPhongTro_App.data.DatabaseHelper;
import com.example.QuanLyPhongTro_App.ui.tenant.MessageDetailActivity;
import com.example.QuanLyPhongTro_App.utils.LandlordBottomNavigationHelper;
import com.example.QuanLyPhongTro_App.utils.SessionManager;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class YeuCau extends AppCompatActivity {

    private TextView btnDatLich, btnTinNhan, btnThanhToan;
    private RecyclerView rvBookings, rvMessages, rvPayments;
    private View tabIndicator;
    
    private SessionManager sessionManager;
    private BookingRequestDao bookingDao;
    private BookingsAdapter bookingsAdapter;
    private List<BookingRequest> bookingRequests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landlord_request);

        sessionManager = new SessionManager(this);
        bookingDao = new BookingRequestDao();
        bookingRequests = new ArrayList<>();

        btnDatLich = findViewById(R.id.btn_tab_datlich);
        btnTinNhan = findViewById(R.id.btn_tab_tinnhan);
        btnThanhToan = findViewById(R.id.btn_tab_thanhtoan);
        tabIndicator = findViewById(R.id.tab_indicator);

        rvBookings = findViewById(R.id.rv_bookings);
        rvMessages = findViewById(R.id.rv_messages);
        rvPayments = findViewById(R.id.rv_payments);

        rvBookings.setLayoutManager(new LinearLayoutManager(this));
        rvMessages.setLayoutManager(new LinearLayoutManager(this));
        rvPayments.setLayoutManager(new LinearLayoutManager(this));

        // Setup adapters
        bookingsAdapter = new BookingsAdapter(bookingRequests, this);
        rvBookings.setAdapter(bookingsAdapter);

        // Setup messages (keep existing mock data for now)
        ArrayList<MessageItem> messages = new ArrayList<>();
        messages.add(new MessageItem("Nguyễn Văn A","Chào bạn, tôi có thể xem phòng vào chiều nay không?","Hôm qua"));
        messages.add(new MessageItem("Lê Thị C","Cảm ơn bạn, mình đã thuê được phòng rồi","2 ngày trước"));
        MessagesAdapter messagesAdapter = new MessagesAdapter(this, messages);
        rvMessages.setAdapter(messagesAdapter);

        // Setup payments (keep existing mock data for now)
        ArrayList<Payment> payments = new ArrayList<>();
        payments.add(new Payment("Phạm Văn D - Cọc phòng #123","01/10/2025","1.500.000 đ"));
        payments.add(new Payment("Nguyễn E - Thanh toán tháng 10","05/10/2025","2.500.000 đ"));
        PaymentsAdapter paymentsAdapter = new PaymentsAdapter(payments);
        rvPayments.setAdapter(paymentsAdapter);

        btnDatLich.setOnClickListener(v -> showTab("datlich"));
        btnTinNhan.setOnClickListener(v -> showTab("tinnhan"));
        btnThanhToan.setOnClickListener(v -> showTab("thanhtoan"));

        setupBottomNavigation();

        String defaultTab = getIntent().getStringExtra("defaultTab");
        if (defaultTab != null && !defaultTab.isEmpty()) {
            showTab(defaultTab);
        } else {
            showTab("datlich");
        }

        // Load booking requests from database
        loadBookingRequests();
    }

    private void setupBottomNavigation() {
        LandlordBottomNavigationHelper.setupBottomNavigation(this, "requests");
    }

    @Override
    protected void onResume() {
        super.onResume();
        LandlordBottomNavigationHelper.setupBottomNavigation(this, "requests");
    }

    private void loadBookingRequests() {
        String landlordId = sessionManager.getUserId();
        if (landlordId == null) {
            Toast.makeText(this, "Lỗi: Không tìm thấy thông tin chủ trọ", Toast.LENGTH_SHORT).show();
            return;
        }

        new LoadBookingRequestsTask().execute(landlordId);
    }

    private class LoadBookingRequestsTask extends AsyncTask<String, Void, List<BookingRequest>> {
        private String errorMsg = null;

        @Override
        protected List<BookingRequest> doInBackground(String... params) {
            String landlordId = params[0];
            Connection conn = null;
            
            try {
                conn = DatabaseHelper.getConnection();
                return bookingDao.getBookingRequestsByLandlord(conn, landlordId);
            } catch (Exception e) {
                errorMsg = e.getMessage();
                Log.e("YeuCau", "Error loading booking requests", e);
                return new ArrayList<>();
            } finally {
                DatabaseHelper.releaseConnection(conn);
            }
        }

        @Override
        protected void onPostExecute(List<BookingRequest> requests) {
            if (errorMsg != null) {
                Toast.makeText(YeuCau.this, "Lỗi tải dữ liệu: " + errorMsg, Toast.LENGTH_SHORT).show();
            }
            
            bookingRequests.clear();
            bookingRequests.addAll(requests);
            bookingsAdapter.notifyDataSetChanged();
            
            Log.d("YeuCau", "Loaded " + requests.size() + " booking requests");
        }
    }

    private void updateBookingStatus(String datPhongId, String statusName, int position) {
        new UpdateBookingStatusTask(datPhongId, statusName, position).execute();
    }

    private class UpdateBookingStatusTask extends AsyncTask<Void, Void, Boolean> {
        private String datPhongId;
        private String statusName;
        private int position;
        private String errorMsg = null;

        public UpdateBookingStatusTask(String datPhongId, String statusName, int position) {
            this.datPhongId = datPhongId;
            this.statusName = statusName;
            this.position = position;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Connection conn = null;
            
            try {
                conn = DatabaseHelper.getConnection();
                int statusId = bookingDao.getStatusIdByName(conn, statusName);
                if (statusId != -1) {
                    return bookingDao.updateBookingStatus(conn, datPhongId, statusId);
                }
                return false;
            } catch (Exception e) {
                errorMsg = e.getMessage();
                Log.e("YeuCau", "Error updating booking status", e);
                return false;
            } finally {
                DatabaseHelper.releaseConnection(conn);
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                // Update local data
                if (position >= 0 && position < bookingRequests.size()) {
                    bookingRequests.get(position).setTenTrangThai(statusName);
                    bookingsAdapter.notifyItemChanged(position);
                }
                Toast.makeText(YeuCau.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
            } else {
                String msg = errorMsg != null ? errorMsg : "Cập nhật thất bại";
                Toast.makeText(YeuCau.this, msg, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showTab(String tab) {
        int primaryColor = ContextCompat.getColor(this, R.color.primary);
        int mutedColor = ContextCompat.getColor(this, R.color.muted);

        btnDatLich.setTextColor(mutedColor);
        btnTinNhan.setTextColor(mutedColor);
        btnThanhToan.setTextColor(mutedColor);
        tabIndicator.setBackgroundColor(primaryColor);

        rvBookings.setVisibility(View.GONE);
        rvMessages.setVisibility(View.GONE);
        rvPayments.setVisibility(View.GONE);

        switch (tab) {
            case "datlich":
                rvBookings.setVisibility(View.VISIBLE);
                btnDatLich.setTextColor(primaryColor);
                break;
            case "tinnhan":
                rvMessages.setVisibility(View.VISIBLE);
                btnTinNhan.setTextColor(primaryColor);
                break;
            case "thanhtoan":
                rvPayments.setVisibility(View.VISIBLE);
                btnThanhToan.setTextColor(primaryColor);
                break;
        }
    }

    static class Booking {
        String name, time, note;
        Booking(String n, String t, String no) { name = n; time = t; note = no; }
    }
    static class MessageItem {
        String name, preview, time;
        MessageItem(String n, String p, String t) { name = n; preview = p; time = t; }
    }
    static class Payment {
        String title, date, amount;
        Payment(String ti, String da, String am) { title = ti; date = da; amount = am; }
    }

    static class BookingsAdapter extends RecyclerView.Adapter<BookingsAdapter.VH> {
        private final List<BookingRequest> list;
        private final YeuCau activity;
        private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm - dd/MM/yyyy", Locale.getDefault());

        BookingsAdapter(List<BookingRequest> l, YeuCau activity) { 
            this.list = l; 
            this.activity = activity;
        }

        static class VH extends RecyclerView.ViewHolder {
            TextView tvName, tvTime, tvNote, tvStatus;
            Button btnAccept, btnReject;
            
            VH(View v) { 
                super(v);
                tvName = v.findViewById(R.id.tv_booking_name);
                tvTime = v.findViewById(R.id.tv_booking_time);
                tvNote = v.findViewById(R.id.tv_booking_note);
                tvStatus = v.findViewById(R.id.tv_booking_status);
                btnAccept = v.findViewById(R.id.btn_booking_accept);
                btnReject = v.findViewById(R.id.btn_booking_reject);
            }
        }

        @NonNull
        @Override 
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_landlord_booking, parent, false);
            return new VH(v);
        }

        @Override 
        public void onBindViewHolder(VH holder, int pos) {
            BookingRequest booking = list.get(pos);
            
            holder.tvName.setText(booking.getTenNguoiThue());
            
            // Format time
            String timeText = "";
            if (booking.getBatDau() != null) {
                timeText = dateFormat.format(booking.getBatDau());
            }
            holder.tvTime.setText(timeText);
            
            // Format note with room name
            String noteText = booking.getLoai() + " '" + booking.getTenPhong() + "'";
            if (booking.getGhiChu() != null && !booking.getGhiChu().trim().isEmpty()) {
                noteText += "\nGhi chú: " + booking.getGhiChu();
            }
            holder.tvNote.setText(noteText);
            
            // Set status
            if (holder.tvStatus != null) {
                holder.tvStatus.setText(booking.getTenTrangThai());
                
                // Set status color
                int statusColor = Color.GRAY;
                if ("ChoXacNhan".equals(booking.getTenTrangThai())) {
                    statusColor = Color.parseColor("#FF9800"); // Orange
                } else if ("DaXacNhan".equals(booking.getTenTrangThai())) {
                    statusColor = Color.parseColor("#4CAF50"); // Green
                } else if ("DaHuy".equals(booking.getTenTrangThai())) {
                    statusColor = Color.parseColor("#F44336"); // Red
                }
                holder.tvStatus.setTextColor(statusColor);
            }
            
            // Handle buttons based on status
            boolean isChoXacNhan = "ChoXacNhan".equals(booking.getTenTrangThai());
            holder.btnAccept.setVisibility(isChoXacNhan ? View.VISIBLE : View.GONE);
            holder.btnReject.setVisibility(isChoXacNhan ? View.VISIBLE : View.GONE);
            
            holder.btnAccept.setOnClickListener(v -> {
                activity.updateBookingStatus(booking.getDatPhongId(), "DaXacNhan", pos);
            });
            
            holder.btnReject.setOnClickListener(v -> {
                activity.updateBookingStatus(booking.getDatPhongId(), "DaHuy", pos);
            });
        }

        @Override 
        public int getItemCount() { 
            return list.size(); 
        }
    }

    static class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.VH> {
        private final Context context;
        private final ArrayList<MessageItem> items;
        MessagesAdapter(Context context, ArrayList<MessageItem> list){ this.context = context; this.items = list; }
        static class VH extends RecyclerView.ViewHolder {
            TextView tvName, tvPreview, tvTime, tvAvatar;
            VH(View v){ super(v);
                tvName = v.findViewById(R.id.tv_msg_name);
                tvPreview = v.findViewById(R.id.tv_msg_preview);
                tvTime = v.findViewById(R.id.tv_msg_time);
                tvAvatar = v.findViewById(R.id.tv_avatar_initial);
            }
        }
        @NonNull
        @Override public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_landlord_message,parent,false);
            return new VH(v);
        }
        @Override public void onBindViewHolder(VH holder,int pos){
            MessageItem it = items.get(pos);
            holder.tvName.setText(it.name);
            holder.tvPreview.setText(it.preview);
            holder.tvTime.setText(it.time);
            if (it.name!=null && it.name.length()>0) holder.tvAvatar.setText(it.name.trim().substring(0,1).toUpperCase());
            else holder.tvAvatar.setText("?");

            holder.itemView.setOnClickListener(v-> {
                Log.d("YeuCauActivity", "Clicked on item: " + it.name);
                Intent intent = new Intent(context, MessageDetailActivity.class);
                intent.putExtra("USER_NAME", it.name);
                context.startActivity(intent);
            });
        }
        @Override public int getItemCount(){ return items.size(); }
    }

    static class PaymentsAdapter extends RecyclerView.Adapter<PaymentsAdapter.VH> {
        private final ArrayList<Payment> list;
        PaymentsAdapter(ArrayList<Payment> l){ list = l; }
        static class VH extends RecyclerView.ViewHolder {
            TextView tvTitle, tvDate, tvAmount;
            VH(View v){ super(v);
                tvTitle = v.findViewById(R.id.tv_payment_title);
                tvDate = v.findViewById(R.id.tv_payment_date);
                tvAmount = v.findViewById(R.id.tv_payment_amount);
            }
        }
        @NonNull
        @Override public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_landlord_payment_v2, parent, false);
            return new VH(v);
        }
        @Override public void onBindViewHolder(VH holder, int pos){
            Payment p = list.get(pos);
            holder.tvTitle.setText(p.title);
            holder.tvDate.setText(p.date);
            holder.tvAmount.setText(p.amount);
            holder.itemView.setOnClickListener(v-> Toast.makeText(v.getContext(),"Chi tiết: "+p.title, Toast.LENGTH_SHORT).show());
        }
        @Override public int getItemCount(){ return list.size(); }
    }
}
