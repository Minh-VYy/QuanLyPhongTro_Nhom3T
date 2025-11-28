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
import com.example.QuanLyPhongTro_App.data.MockData;

import java.util.List;

public class AllListingsAdapter extends RecyclerView.Adapter<AllListingsAdapter.ViewHolder> {

    private final List<MockData.LandlordData.ListingItem> listings;
    private final OnListingClickListener listener;

    public interface OnListingClickListener {
        void onListingClick(MockData.LandlordData.ListingItem listing);
    }

    public AllListingsAdapter(List<MockData.LandlordData.ListingItem> listings, OnListingClickListener listener) {
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
        MockData.LandlordData.ListingItem listing = listings.get(position);

        holder.tvTitle.setText(listing.title);
        holder.tvPrice.setText(listing.price);
        holder.tvStatus.setText(listing.status);
        holder.swActive.setChecked(listing.isActive);

        // Set màu cho trạng thái
        setStatusColor(holder.tvStatus, listing.status);

        // Xử lý switch active
        holder.swActive.setOnCheckedChangeListener((buttonView, isChecked) -> {
            listing.isActive = isChecked;
            // Cập nhật trạng thái hiển thị
            String newStatus = isChecked ? "Còn trống" : "Không hoạt động";
            holder.tvStatus.setText(newStatus);
            setStatusColor(holder.tvStatus, newStatus);
        });

        // Xử lý click edit
        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onListingClick(listing);
            }
        });

        // Xử lý click toàn bộ item
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onListingClick(listing);
            }
        });
    }

    private void setStatusColor(TextView textView, String status) {
        switch (status) {
            case "Còn trống":
                textView.setTextColor(textView.getContext().getColor(R.color.success));
                break;
            case "Đã thuê":
                textView.setTextColor(textView.getContext().getColor(R.color.error));
                break;
            case "Chờ xử lý":
                textView.setTextColor(textView.getContext().getColor(R.color.warning));
                break;
            default:
                textView.setTextColor(textView.getContext().getColor(R.color.gray));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return listings.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvPrice, tvStatus;
        Switch swActive;
        ImageView btnEdit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_listing_title);
            tvPrice = itemView.findViewById(R.id.tv_listing_price);
            tvStatus = itemView.findViewById(R.id.tv_listing_status);
            swActive = itemView.findViewById(R.id.sw_listing_active);
            btnEdit = itemView.findViewById(R.id.btn_edit_listing);
        }
    }
}
