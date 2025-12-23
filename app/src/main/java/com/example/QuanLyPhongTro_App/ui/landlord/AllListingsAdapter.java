package com.example.QuanLyPhongTro_App.ui.landlord;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
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

        Log.d("AllListingsAdapter",
                "Position: " + position +
                        ", Title: " + listing.title +
                        ", ImageName: " + listing.imageName);
        // 1. HIỂN THỊ TEXT
        holder.tvTitle.setText(listing.title);
        holder.tvPrice.setText(listing.price);
        holder.tvStatus.setText(listing.status);
        holder.swActive.setChecked(listing.isActive);

        // 2. LOAD ẢNH TỪ DRAWABLE (QUAN TRỌNG!)
        loadImage(holder.imgListing, listing.imageName);

        // 3. SET MÀU CHO TRẠNG THÁI
        updateStatusStyle(holder.tvStatus, listing.status);

        // 4. CLICK VÀO ITEM
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onListingClick(listing);
            }
        });

        // 5. XỬ LÝ SWITCH
        holder.swActive.setOnCheckedChangeListener(null);
        holder.swActive.setChecked(listing.isActive);

        holder.swActive.setOnCheckedChangeListener((buttonView, isChecked) -> {
            listing.isActive = isChecked;
            String newStatus = isChecked ? "Còn trống" : "Không hoạt động";
            holder.tvStatus.setText(newStatus);
            updateStatusStyle(holder.tvStatus, newStatus);
        });
    }

    private void loadImage(ImageView imageView, String imageName) {
        Log.d("AllListingsAdapter", "Loading image: " + imageName);

        if (imageName == null || imageName.isEmpty()) {
            Log.w("AllListingsAdapter", "ImageName is null or empty, using default");
            imageView.setImageResource(R.drawable.room_4);
            return;
        }

        try {
            Context context = imageView.getContext();
            int drawableId = context.getResources()
                    .getIdentifier(imageName, "drawable", context.getPackageName());

            Log.d("AllListingsAdapter", "Drawable ID for " + imageName + ": " + drawableId);

            if (drawableId != 0) {
                imageView.setImageResource(drawableId);
                Log.i("AllListingsAdapter", "✓ Successfully loaded: " + imageName);
            } else {
                imageView.setImageResource(R.drawable.room_4);
                Log.e("AllListingsAdapter", "✗ Image not found: " + imageName);
            }
        } catch (Exception e) {
            imageView.setImageResource(R.drawable.room_4);
            Log.e("AllListingsAdapter", "Error loading image: " + imageName, e);
        }
    }

    // PHƯƠNG THỨC SET MÀU STATUS
    private void updateStatusStyle(TextView textView, String status) {
        int textColor;
        int bgColor;

        switch (status) {
            case "Còn trống":
                textColor = Color.parseColor("#059669"); // Xanh đậm
                bgColor = Color.parseColor("#D1FAE5");   // Xanh nhạt
                break;
            case "Đã thuê":
                textColor = Color.parseColor("#DC2626"); // Đỏ đậm
                bgColor = Color.parseColor("#FEE2E2");   // Đỏ nhạt
                break;
            case "Chờ xử lý":
                textColor = Color.parseColor("#D97706"); // Cam đậm
                bgColor = Color.parseColor("#FEF3C7");   // Vàng nhạt
                break;
            case "Không hoạt động":
                textColor = Color.parseColor("#6B7280"); // Xám đậm
                bgColor = Color.parseColor("#F3F4F6");   // Xám nhạt
                break;
            default:
                textColor = Color.BLACK;
                bgColor = Color.TRANSPARENT;
        }

        textView.setTextColor(textColor);

        // Tạo background với góc bo tròn
        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadius(16);
        drawable.setColor(bgColor);
        drawable.setStroke(1, textColor);
        textView.setBackground(drawable);

        // Thêm padding cho đẹp
        int padding = dpToPx(textView.getContext(), 6);
        textView.setPadding(padding, padding/2, padding, padding/2);
    }


    private int dpToPx(Context context, int dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }

    @Override
    public int getItemCount() {
        return listings.size();
    }

    // VIEWHOLDER (PHẢI CÓ imgListing!)
    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgListing;
        TextView tvTitle, tvPrice, tvStatus;
        Switch swActive;

        ViewHolder(View itemView) {
            super(itemView);
            // QUAN TRỌNG: FIND VIEW BY ID PHẢI ĐÚNG
            imgListing = itemView.findViewById(R.id.img_listing_item);
            tvTitle = itemView.findViewById(R.id.tv_title_item);
            tvPrice = itemView.findViewById(R.id.tv_price_item);
            tvStatus = itemView.findViewById(R.id.tv_status_item);
            swActive = itemView.findViewById(R.id.switch_active);
        }
    }
}