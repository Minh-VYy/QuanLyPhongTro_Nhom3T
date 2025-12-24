package com.example.QuanLyPhongTro_App.ui.landlord;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.QuanLyPhongTro_App.R;

import java.util.List;

public class UtilityAdapter extends RecyclerView.Adapter<UtilityAdapter.ViewHolder> {

    // Bước 1: Định nghĩa lại lớp UtilityItem ngay tại đây
    public static class UtilityItem {
        private int icon;
        private String title;
        private String description;
        private Class<?> targetActivity;

        public UtilityItem(int icon, String title, String description, Class<?> targetActivity) {
            this.icon = icon;
            this.title = title;
            this.description = description;
            this.targetActivity = targetActivity;
        }

        public int getIcon() { return icon; }
        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public Class<?> getTargetActivity() { return targetActivity; }
    }

    private Context context;
    // Bước 2: Thay đổi các tham chiếu để sử dụng UtilityItem mới
    private List<UtilityItem> utilityItems;
    private UtilityDialog dialog;

    public UtilityAdapter(Context context, List<UtilityItem> utilityItems, UtilityDialog dialog) {
        this.context = context;
        this.utilityItems = utilityItems;
        this.dialog = dialog;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_landlord_utility, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UtilityItem item = utilityItems.get(position);

        holder.icon.setImageResource(item.getIcon());
        holder.title.setText(item.getTitle());
        holder.description.setText(item.getDescription());

        holder.itemView.setOnClickListener(v -> {
            if (item.getTargetActivity() != null) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                Intent intent = new Intent(context, item.getTargetActivity());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return utilityItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView title;
        TextView description;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.utility_icon);
            title = itemView.findViewById(R.id.utility_title);
            description = itemView.findViewById(R.id.utility_description);
        }
    }
}
