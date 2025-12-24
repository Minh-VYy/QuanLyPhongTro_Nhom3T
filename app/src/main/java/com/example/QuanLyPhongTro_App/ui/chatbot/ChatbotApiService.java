package com.example.QuanLyPhongTro_App.ui.chatbot;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ChatbotApiService {
    @POST("chat") // Endpoint của API chatbot
    Call<ChatResponse> sendMessage(@Body ChatRequest request);
}
