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
import com.example.QuanLyPhongTro_App.data.DatabaseHelper;
import com.example.QuanLyPhongTro_App.data.dao.DatPhongDao;

import java.sql.Connection;
import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    private Context context;
    private List<Booking> bookingList;
    private OnBookingActionListener listener;

    public interface OnBookingActionListener {
        void onBookingCancelled();
    }

    public BookingAdapter(Context context, List<Booking> bookingList) {
        this.context = context;
        this.bookingList = bookingList;
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
        new Thread(() -> {
            Connection conn = null;
            boolean success = false;

            try {
                conn = DatabaseHelper.getConnection();
                DatPhongDao dao = new DatPhongDao();
                
                // Cập nhật trạng thái thành 4 (Đã hủy)
                success = dao.updateTrangThai(conn, booking.getId(), 4);
                
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    DatabaseHelper.releaseConnection(conn);
                }
            }

            final boolean finalSuccess = success;
            
            if (context instanceof android.app.Activity) {
                ((android.app.Activity) context).runOnUiThread(() -> {
                    if (finalSuccess) {
                        Toast.makeText(context, "Đã hủy lịch hẹn", Toast.LENGTH_SHORT).show();
                        
                        // Cập nhật UI
                        booking.setStatus("cancelled");
                        notifyItemChanged(position);
                        
                        // Thông báo cho fragment reload
                        if (listener != null) {
                            listener.onBookingCancelled();
                        }
                    } else {
                        Toast.makeText(context, "Không thể hủy lịch hẹn. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
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

