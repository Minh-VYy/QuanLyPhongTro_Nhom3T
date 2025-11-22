package com.example.QuanLyPhongTro_App.ui.tenant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.QuanLyPhongTro_App.R;

import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    private Context context;
    private List<Booking> bookingList;

    public BookingAdapter(Context context, List<Booking> bookingList) {
        this.context = context;
        this.bookingList = bookingList;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tenant_booking, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = bookingList.get(position);

        holder.roomName.setText(booking.getTitle());
        holder.roomPrice.setText(booking.getPrice());
        holder.bookingDateTime.setText(booking.getDate() + " - " + booking.getTimeSlot());
        holder.roomAddress.setText(booking.getLocation());


        // Set status badge
        String status = booking.getStatus();
        switch (status) {
            case "pending":
                holder.bookingStatus.setText("Đang chờ");
                holder.bookingStatus.setTextColor(context.getResources().getColor(R.color.warning));
                holder.btnCancel.setVisibility(View.VISIBLE);
                break;
            case "confirmed":
                holder.bookingStatus.setText("Đã xác nhận");
                holder.bookingStatus.setTextColor(context.getResources().getColor(R.color.success));
                holder.btnCancel.setVisibility(View.VISIBLE);
                break;
            case "completed":
                holder.bookingStatus.setText("Đã xem");
                holder.bookingStatus.setTextColor(context.getResources().getColor(R.color.text_secondary));
                holder.btnCancel.setVisibility(View.GONE);
                break;
            case "cancelled":
                holder.bookingStatus.setText("Đã huỷ");
                holder.bookingStatus.setTextColor(context.getResources().getColor(R.color.error));
                holder.btnCancel.setVisibility(View.GONE);
                break;
        }

        holder.btnDetail.setOnClickListener(v -> {
            Toast.makeText(context, "Xem chi tiết: " + booking.getTitle(), Toast.LENGTH_SHORT).show();
        });

        holder.btnCancel.setOnClickListener(v -> {
            Toast.makeText(context, "Huỷ lịch: " + booking.getTitle(), Toast.LENGTH_SHORT).show();
            // TODO: Implement cancel booking
        });
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView roomName, roomPrice, bookingDateTime, roomAddress, bookingStatus;
        Button btnDetail, btnCancel;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            roomName = itemView.findViewById(R.id.roomName);
            roomPrice = itemView.findViewById(R.id.roomPrice);
            bookingDateTime = itemView.findViewById(R.id.bookingDateTime);
            roomAddress = itemView.findViewById(R.id.roomAddress);
            bookingStatus = itemView.findViewById(R.id.bookingStatus);
            btnDetail = itemView.findViewById(R.id.btnDetail);
            btnCancel = itemView.findViewById(R.id.btnCancel);
        }
    }
}

