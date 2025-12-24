package com.example.QuanLyPhongTro_App.ui.landlord;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.QuanLyPhongTro_App.R;

import java.util.List;

public class AllListingsAdapter extends RecyclerView.Adapter<AllListingsAdapter.ViewHolder> {

    // Bước 1: Định nghĩa lại lớp ListingItem ngay tại đây
    public static class ListingItem {
        public String title;
        public String price;
        public String status;
        public boolean isActive;

        public ListingItem(String title, String price, String status, boolean isActive) {
            this.title = title;
            this.price = price;
            this.status = status;
            this.isActive = isActive;
        }
    }

    // Bước 2: Thay đổi các tham chiếu để sử dụng ListingItem mới
    private final List<ListingItem> listings;
    private final OnListingClickListener listener;

    public interface OnListingClickListener {
        void onListingClick(ListingItem listing);
    }

    public AllListingsAdapter(List<ListingItem> listings, OnListingClickListener listener) {
        this.listings = listings;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_all_listing, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ListingItem listing = listings.get(position);

        holder.tvTitle.setText(listing.title);
        holder.tvPrice.setText(listing.price);
        holder.tvStatus.setText(listing.status);
        holder.swActive.setChecked(listing.isActive);

        setStatusStyle(holder.tvStatus, listing.status);

        holder.cardView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onListingClick(listing);
            }
        });

        holder.swActive.setOnCheckedChangeListener((buttonView, isChecked) -> {
            listing.isActive = isChecked;
            String newStatus = isChecked ? "Còn trống" : "Không hoạt động";
            holder.tvStatus.setText(newStatus);
            setStatusStyle(holder.tvStatus, newStatus);
        });

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onListingClick(listing);
            }
        });
    }

    private void setStatusStyle(TextView textView, String status) {
        int backgroundColor;
        int textColor;

        switch (status) {
            case "Còn trống":
                backgroundColor = R.drawable.bg_status_available;
                textColor = R.color.success;
                break;
            case "Đã thuê":
                backgroundColor = R.drawable.bg_status_rented;
                textColor = R.color.error;
                break;
            case "Chờ xử lý":
                backgroundColor = R.drawable.bg_status_pending;
                textColor = R.color.warning;
                break;
            default:
                backgroundColor = R.drawable.bg_status_inactive;
                textColor = R.color.gray;
                break;
        }

        textView.setBackgroundResource(backgroundColor);
        textView.setTextColor(textView.getContext().getColor(textColor));
    }

    @Override
    public int getItemCount() {
        return listings.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View cardView;
        TextView tvTitle, tvPrice, tvStatus;
        Switch swActive;
        ImageView btnEdit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_listing);
            tvTitle = itemView.findViewById(R.id.tv_listing_title);
            tvPrice = itemView.findViewById(R.id.tv_listing_price);
            tvStatus = itemView.findViewById(R.id.tv_listing_status);
            swActive = itemView.findViewById(R.id.sw_listing_active);
            btnEdit = itemView.findViewById(R.id.btn_edit_listing);
        }
    }
}
