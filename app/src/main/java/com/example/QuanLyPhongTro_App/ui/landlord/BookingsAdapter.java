package com.example.QuanLyPhongTro_App.ui.landlord;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.QuanLyPhongTro_App.R;

import java.util.List;

/**
 * Minimal adapter for landlord booking requests.
 *
 * Notes:
 * - Actions (accept/reject) are only available when the request is pending (ChoXacNhan).
 * - After the status changes, buttons are hidden so the UI reflects the new state.
 */
public class BookingsAdapter extends RecyclerView.Adapter<BookingsAdapter.VH> {

    public interface OnBookingActionListener {
        void onAccept(BookingRequest request);

        void onReject(BookingRequest request);
    }

    private final List<BookingRequest> items;
    private OnBookingActionListener listener;

    public BookingsAdapter(List<BookingRequest> items, YeuCau ignoredActivity) {
        this.items = items;
    }

    public void setOnBookingActionListener(OnBookingActionListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_landlord_booking, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        BookingRequest br = items.get(position);

        if (holder.tvName != null) holder.tvName.setText(safe(br.getTenNguoiThue()));
        if (holder.tvTime != null) holder.tvTime.setText(br.getBatDau() != null ? String.valueOf(br.getBatDau()) : "");
        if (holder.tvNote != null) {
            String note = safe(br.getTenPhong());
            String loai = safe(br.getLoai());
            holder.tvNote.setText((note.isEmpty() ? "" : note) + (loai.isEmpty() ? "" : (" - " + loai)));
        }
        if (holder.tvStatus != null) holder.tvStatus.setText(getDisplayStatus(safe(br.getTenTrangThai()), br.getTrangThaiId()));

        // Pending status
        boolean isPending = br.getTrangThaiId() == 1 || br.isChoXacNhan() || "ChoXacNhan".equalsIgnoreCase(safe(br.getTenTrangThai()));

        if (holder.btnAccept != null) {
            holder.btnAccept.setVisibility(isPending ? View.VISIBLE : View.GONE);
            holder.btnAccept.setEnabled(isPending);
            holder.btnAccept.setAlpha(isPending ? 1f : 0.5f);
            holder.btnAccept.setOnClickListener(v -> {
                if (!isPending) return;
                if (listener != null) listener.onAccept(br);
            });
        }

        if (holder.btnReject != null) {
            holder.btnReject.setVisibility(isPending ? View.VISIBLE : View.GONE);
            holder.btnReject.setEnabled(isPending);
            holder.btnReject.setAlpha(isPending ? 1f : 0.5f);
            holder.btnReject.setOnClickListener(v -> {
                if (!isPending) return;
                if (listener != null) listener.onReject(br);
            });
        }

        Log.d("BookingsAdapter", "bind position=" + position + " id=" + safe(br.getDatPhongId()) + " statusId=" + br.getTrangThaiId() + " statusName=" + safe(br.getTenTrangThai()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }

    private static String getDisplayStatus(String statusName, Integer statusId) {
        // Prefer id if available
        int id = statusId == null ? -1 : statusId;
        if (id == 1) return "⏳ Chờ xác nhận";
        if (id == 2) return "✅ Đã chấp nhận";
        if (id == 3) return "❌ Đã từ chối";

        // Fallback by name
        switch (statusName) {
            case "ChoXacNhan":
                return "⏳ Chờ xác nhận";
            case "DaXacNhan":
                return "✅ Đã chấp nhận";
            case "DaHuy":
                return "❌ Đã từ chối";
            default:
                return statusName;
        }
    }

    static class VH extends RecyclerView.ViewHolder {
        final TextView tvName;
        final TextView tvTime;
        final TextView tvNote;
        final TextView tvStatus;
        final Button btnReject;
        final Button btnAccept;

        VH(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_booking_name);
            tvTime = itemView.findViewById(R.id.tv_booking_time);
            tvNote = itemView.findViewById(R.id.tv_booking_note);
            tvStatus = itemView.findViewById(R.id.tv_booking_status);
            btnReject = itemView.findViewById(R.id.btn_booking_reject);
            btnAccept = itemView.findViewById(R.id.btn_booking_accept);
        }
    }
}
