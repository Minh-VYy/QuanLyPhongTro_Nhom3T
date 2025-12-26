package com.example.QuanLyPhongTro_App.data.request;

/**
 * Update booking status by landlord.
 * Backend expects: { trangThaiId: number, chuTroId?: string }
 */
public class UpdateBookingStatusByLandlordRequest {
    public int trangThaiId;
    public String chuTroId;

    public UpdateBookingStatusByLandlordRequest(int trangThaiId, String chuTroId) {
        this.trangThaiId = trangThaiId;
        this.chuTroId = chuTroId;
    }
}

