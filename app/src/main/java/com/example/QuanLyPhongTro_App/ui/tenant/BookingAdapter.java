package com.example.QuanLyPhongTro_App.ui.tenant;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.QuanLyPhongTro_App.R;
import com.example.QuanLyPhongTro_App.data.repository.BookingRepository;
import com.example.QuanLyPhongTro_App.utils.ApiClient;
import com.example.QuanLyPhongTro_App.utils.SessionManager;

import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    private Context context;
    private List<Booking> bookingList;
    private OnBookingActionListener listener;
    private final BookingRepository bookingRepository;
    private final SessionManager sessionManager;

    public interface OnBookingActionListener {
        void onBookingCancelled();
    }

    public BookingAdapter(Context context, List<Booking> bookingList) {
        this.context = context;
        this.bookingList = bookingList;
        this.bookingRepository = new BookingRepository();
        this.sessionManager = new SessionManager(context);
    }

    public void setOnBookingActionListener(OnBookingActionListener listener) {
        this.listener = listener;
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
            try {
                String bookingId = booking.getId();
                if (bookingId == null || bookingId.isEmpty()) {
                    Toast.makeText(context, "Không tìm thấy ID đặt lịch", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                Intent intent = new Intent(context, BookingDetailActivity.class);
                intent.putExtra(BookingDetailActivity.EXTRA_BOOKING_ID, bookingId);
                context.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        holder.btnCancel.setOnClickListener(v -> {
            showCancelConfirmDialog(booking, position);
        });
    }

    private void showCancelConfirmDialog(Booking booking, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Hủy lịch hẹn")
                .setMessage("Bạn có chắc chắn muốn hủy lịch hẹn này?")
                .setPositiveButton("Hủy lịch", (dialog, which) -> {
                    cancelBooking(booking, position);
                })
                .setNegativeButton("Không", null)
                .show();
    }

    private void cancelBooking(Booking booking, int position) {
        if (!sessionManager.isLoggedIn() || sessionManager.getToken() == null || sessionManager.getToken().isEmpty()) {
            if (context instanceof android.app.Activity) {
                ((android.app.Activity) context).runOnUiThread(() ->
                        Toast.makeText(context, "Vui lòng đăng nhập lại để hủy lịch hẹn", Toast.LENGTH_SHORT).show());
            }
            return;
        }

        // Ensure token is injected into OkHttp
        ApiClient.setToken(sessionManager.getToken());

        bookingRepository.cancelBooking(booking.getId(), new BookingRepository.SimpleCallback() {
            @Override
            public void onSuccess() {
                if (context instanceof android.app.Activity) {
                    ((android.app.Activity) context).runOnUiThread(() -> {
                        Toast.makeText(context, "Đã hủy lịch hẹn", Toast.LENGTH_SHORT).show();

                        booking.setStatus("cancelled");
                        notifyItemChanged(position);

                        if (listener != null) {
                            listener.onBookingCancelled();
                        }
                    });
                }
            }

            @Override
            public void onError(String message) {
                if (context instanceof android.app.Activity) {
                    ((android.app.Activity) context).runOnUiThread(() ->
                            Toast.makeText(context, "Không thể hủy lịch hẹn: " + message, Toast.LENGTH_LONG).show());
                }
            }
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
