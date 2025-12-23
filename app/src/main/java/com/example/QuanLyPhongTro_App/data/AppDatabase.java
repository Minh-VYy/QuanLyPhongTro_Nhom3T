package com.example.QuanLyPhongTro_App.data;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.QuanLyPhongTro_App.data.dao.RoomDao;
import com.example.QuanLyPhongTro_App.ui.tenant.Room;

import java.util.List;
import java.util.concurrent.Executors;

// Sử dụng trực tiếp lớp Room từ UI, đã được đánh dấu là @Entity
@Database(entities = {Room.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract RoomDao roomDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    // Sử dụng tên đầy đủ "androidx.room.Room" để tránh trùng lặp
                    INSTANCE = androidx.room.Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "app_database")
                            .addCallback(roomDatabaseCallback) // Dùng callback để khởi tạo dữ liệu
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static final RoomDatabase.Callback roomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            // Chạy trên một luồng nền
            Executors.newSingleThreadExecutor().execute(() -> {
                RoomDao dao = INSTANCE.roomDao();
                List<Room> mockRooms = MockData.getRooms(); // Lấy dữ liệu từ MockData

                // Không cần chuyển đổi, insert trực tiếp
                for (Room mockRoom : mockRooms) {
                    dao.insert(mockRoom);
                }
            });
        }
    };
}
