package com.example.QuanLyPhongTro_App.data.request;

import com.google.gson.annotations.SerializedName;

public class UpdateBookingStatusRequest {
    @SerializedName("trangThaiId")
    public Integer trangThaiId;

    public UpdateBookingStatusRequest(Integer trangThaiId) {
        this.trangThaiId = trangThaiId;
    }
}

