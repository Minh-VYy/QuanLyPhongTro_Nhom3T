package com.example.QuanLyPhongTro_App.ui.landlord;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import com.example.QuanLyPhongTro_App.ui.tenant.MessageDetailActivity;
import com.example.QuanLyPhongTro_App.utils.LandlordBottomNavigationHelper;

import java.util.ArrayList;

public class YeuCau extends AppCompatActivity {

    private TextView btnDatLich, btnTinNhan, btnThanhToan;
    private RecyclerView rvBookings, rvMessages, rvPayments;
    private View tabIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landlord_request);

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

        ArrayList<Booking> bookings = new ArrayList<>();
        bookings.add(new Booking("Nguyễn Văn A","15:30 - 01/10/2025","Xem phòng 'Phòng trọ A'"));
        bookings.add(new Booking("Lê Thị B","10:00 - 02/10/2025","Xem phòng 'Chung cư mini'"));
        BookingsAdapter bookingsAdapter = new BookingsAdapter(bookings);
        rvBookings.setAdapter(bookingsAdapter);

        ArrayList<MessageItem> messages = new ArrayList<>();
        messages.add(new MessageItem("Nguyễn Văn A","Chào bạn, tôi có thể xem phòng vào chiều nay không?","Hôm qua"));
        messages.add(new MessageItem("Lê Thị C","Cảm ơn bạn, mình đã thuê được phòng rồi","2 ngày trước"));
        MessagesAdapter messagesAdapter = new MessagesAdapter(this, messages);
        rvMessages.setAdapter(messagesAdapter);

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
        private final ArrayList<Booking> list;
        BookingsAdapter(ArrayList<Booking> l){ list = l; }
        static class VH extends RecyclerView.ViewHolder {
            TextView tvName, tvTime, tvNote;
            Button btnAccept, btnReject;
            VH(View v){ super(v);
                tvName = v.findViewById(R.id.tv_booking_name);
                tvTime = v.findViewById(R.id.tv_booking_time);
                tvNote = v.findViewById(R.id.tv_booking_note);
                btnAccept = v.findViewById(R.id.btn_booking_accept);
                btnReject = v.findViewById(R.id.btn_booking_reject);
            }
        }
        @NonNull
        @Override public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_landlord_booking,parent,false);
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
