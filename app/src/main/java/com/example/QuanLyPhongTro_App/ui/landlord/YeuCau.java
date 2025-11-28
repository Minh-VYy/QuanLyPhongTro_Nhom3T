package com.example.QuanLyPhongTro_App.ui.landlord;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.QuanLyPhongTro_App.R;
import com.example.QuanLyPhongTro_App.utils.LandlordBottomNavigationHelper;

import java.util.ArrayList;

public class YeuCau extends AppCompatActivity {

    private static final String TAG = "YeuCau";
    private Button btnDatLich, btnTinNhan, btnThanhToan;
    private RecyclerView rvBookings, rvMessages, rvPayments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landlord_request);

        // tabs
        btnDatLich = findViewById(R.id.btn_tab_datlich);
        btnTinNhan = findViewById(R.id.btn_tab_tinnhan);
        btnThanhToan = findViewById(R.id.btn_tab_thanhtoan);

        rvBookings = findViewById(R.id.rv_bookings);
        rvMessages = findViewById(R.id.rv_messages);
        rvPayments = findViewById(R.id.rv_payments);


        // setup recyclers
        rvBookings.setLayoutManager(new LinearLayoutManager(this));
        rvMessages.setLayoutManager(new LinearLayoutManager(this));
        rvPayments.setLayoutManager(new LinearLayoutManager(this));

        // mock bookings
        ArrayList<Booking> bookings = new ArrayList<>();
        bookings.add(new Booking("Nguyễn Văn A","15:30 - 01/10/2025","Xem phòng 'Phòng trọ A'"));
        bookings.add(new Booking("Lê Thị B","10:00 - 02/10/2025","Xem phòng 'Chung cư mini'"));

        BookingsAdapter bookingsAdapter = new BookingsAdapter(bookings);
        rvBookings.setAdapter(bookingsAdapter);

        // mock messages
        ArrayList<MessageItem> messages = new ArrayList<>();
        messages.add(new MessageItem("Nguyễn Văn A","Chào bạn, tôi có thể xem phòng vào chiều nay không?","Hôm qua"));
        messages.add(new MessageItem("Lê Thị C","Cảm ơn bạn, mình đã thuê được phòng rồi","2 ngày trước"));
        MessagesAdapter messagesAdapter = new MessagesAdapter(messages);
        rvMessages.setAdapter(messagesAdapter);

        // mock payments
        ArrayList<Payment> payments = new ArrayList<>();
        payments.add(new Payment("Phạm Văn D - Cọc phòng #123","01/10/2025","1.500.000 đ"));
        payments.add(new Payment("Nguyễn E - Thanh toán tháng 10","05/10/2025","2.500.000 đ"));
        PaymentsAdapter paymentsAdapter = new PaymentsAdapter(payments);
        rvPayments.setAdapter(paymentsAdapter);

        // tab click handlers
        btnDatLich.setOnClickListener(v -> showTab("datlich"));
        btnTinNhan.setOnClickListener(v -> showTab("tinnhan"));
        btnThanhToan.setOnClickListener(v -> showTab("thanhtoan"));

        // Setup bottom navigation
        setupBottomNavigation();

        // Check if there's a default tab from Intent
        String defaultTab = getIntent().getStringExtra("defaultTab");
        if (defaultTab != null && !defaultTab.isEmpty()) {
            showTab(defaultTab);
        } else {
            // default show Tin nhắn
            showTab("tinnhan");
        }
    }

    private void setupBottomNavigation() {
        LandlordBottomNavigationHelper.setupBottomNavigation(this, "requests");
    }

    @Override
    protected void onResume() {
        super.onResume();
        LandlordBottomNavigationHelper.setupBottomNavigation(this, "requests");
    }

    private void showTab(String tab) {
        int mauChinh = ContextCompat.getColor(this, R.color.primary);
        int mauKhong = Color.parseColor("#6B7280");

        btnDatLich.setTextColor(mauKhong);
        btnTinNhan.setTextColor(mauKhong);
        btnThanhToan.setTextColor(mauKhong);

        rvBookings.setVisibility(View.GONE);
        rvMessages.setVisibility(View.GONE);
        rvPayments.setVisibility(View.GONE);

        switch (tab) {
            case "datlich":
                rvBookings.setVisibility(View.VISIBLE);
                btnDatLich.setTextColor(mauChinh);
                break;
            case "tinnhan":
                rvMessages.setVisibility(View.VISIBLE);
                btnTinNhan.setTextColor(mauChinh);
                break;
            case "thanhtoan":
                rvPayments.setVisibility(View.VISIBLE);
                btnThanhToan.setTextColor(mauChinh);
                break;
        }
    }

    // ---- Models ----
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

    // ---- Adapters ----

    // BookingsAdapter
    static class BookingsAdapter extends RecyclerView.Adapter<BookingsAdapter.VH> {
        private final ArrayList<Booking> list;
        BookingsAdapter(ArrayList<Booking> l){ list = l; }
        static class VH extends RecyclerView.ViewHolder {
            android.widget.TextView tvName, tvTime, tvNote;
            Button btnAccept, btnReject;
            VH(View v){ super(v);
                tvName = v.findViewById(R.id.tv_booking_name);
                tvTime = v.findViewById(R.id.tv_booking_time);
                tvNote = v.findViewById(R.id.tv_booking_note);
                btnAccept = v.findViewById(R.id.btn_booking_accept);
                btnReject = v.findViewById(R.id.btn_booking_reject);
            }
        }
        @Override public VH onCreateViewHolder(android.view.ViewGroup parent,int viewType){
            View v = android.view.LayoutInflater.from(parent.getContext()).inflate(R.layout.item_landlord_booking,parent,false);
            return new VH(v);
        }
        @Override public void onBindViewHolder(VH holder,int pos){
            Booking b = list.get(pos);
            holder.tvName.setText(b.name);
            holder.tvTime.setText(b.time);
            holder.tvNote.setText(b.note);
            holder.btnAccept.setOnClickListener(v-> Toast.makeText(v.getContext(),"Chấp nhận "+b.name, Toast.LENGTH_SHORT).show());
            holder.btnReject.setOnClickListener(v-> Toast.makeText(v.getContext(),"Từ chối "+b.name, Toast.LENGTH_SHORT).show());
        }
        @Override public int getItemCount(){ return list.size(); }
    }

    // MessagesAdapter (reuse previous)
    static class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.VH> {
        private final ArrayList<MessageItem> items;
        MessagesAdapter(ArrayList<MessageItem> list){ items = list; }
        static class VH extends RecyclerView.ViewHolder {
            android.widget.TextView tvName, tvPreview, tvTime, tvAvatar;
            View root;
            VH(View v){ super(v);
                root = v;
                tvName = v.findViewById(R.id.tv_msg_name);
                tvPreview = v.findViewById(R.id.tv_msg_preview);
                tvTime = v.findViewById(R.id.tv_msg_time);
                tvAvatar = v.findViewById(R.id.tv_avatar_initial);
            }
        }
        @Override public VH onCreateViewHolder(android.view.ViewGroup parent,int viewType){
            View v = android.view.LayoutInflater.from(parent.getContext()).inflate(R.layout.item_landlord_message,parent,false);
            return new VH(v);
        }
        @Override public void onBindViewHolder(VH holder,int pos){
            MessageItem it = items.get(pos);
            holder.tvName.setText(it.name);
            holder.tvPreview.setText(it.preview);
            holder.tvTime.setText(it.time);
            if (it.name!=null && it.name.length()>0) holder.tvAvatar.setText(it.name.trim().substring(0,1).toUpperCase());
            else holder.tvAvatar.setText("?");
            holder.root.setOnClickListener(v-> {
                Toast.makeText(v.getContext(),"Mở chat với "+it.name, Toast.LENGTH_SHORT).show();
            });
        }
        @Override public int getItemCount(){ return items.size(); }
    }

    // PaymentsAdapter
    static class PaymentsAdapter extends RecyclerView.Adapter<PaymentsAdapter.VH> {
        private final ArrayList<Payment> list;
        PaymentsAdapter(ArrayList<Payment> l){ list = l; }
        static class VH extends RecyclerView.ViewHolder {
            android.widget.TextView tvTitle, tvDate, tvAmount;
            VH(View v){ super(v);
                tvTitle = v.findViewById(R.id.tv_payment_title);
                tvDate = v.findViewById(R.id.tv_payment_date);
                tvAmount = v.findViewById(R.id.tv_payment_amount);
            }
        }
        @Override public VH onCreateViewHolder(android.view.ViewGroup parent,int viewType){
            View v = android.view.LayoutInflater.from(parent.getContext()).inflate(R.layout.item_landlord_payment,parent,false);
            return new VH(v);
        }
        @Override public void onBindViewHolder(VH holder,int pos){
            Payment p = list.get(pos);
            holder.tvTitle.setText(p.title);
            holder.tvDate.setText(p.date);
            holder.tvAmount.setText(p.amount);
            holder.itemView.setOnClickListener(v-> Toast.makeText(v.getContext(),"Chi tiết: "+p.title, Toast.LENGTH_SHORT).show());
        }
        @Override public int getItemCount(){ return list.size(); }
    }
}
