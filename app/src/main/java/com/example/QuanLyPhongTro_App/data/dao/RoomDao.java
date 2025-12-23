package com.example.QuanLyPhongTro_App.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.QuanLyPhongTro_App.ui.tenant.Room;
import java.util.List;

@Dao
public interface RoomDao {
    @Insert
    void insert(Room room);

    @Query("SELECT * FROM rooms")
    List<Room> getAll();

    @Query("SELECT * FROM rooms WHERE id = :roomId")
    Room findById(int roomId);
}