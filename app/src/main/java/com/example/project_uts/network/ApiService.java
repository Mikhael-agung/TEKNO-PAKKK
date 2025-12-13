package com.example.project_uts.network;

import com.example.project_uts.models.ApiResponse;
import com.example.project_uts.models.Complaint;
import com.example.project_uts.models.LoginResponse;
import com.example.project_uts.models.RegisterResponse;
import com.example.project_uts.models.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.PATCH;
import retrofit2.http.Path;
import retrofit2.http.Query;
import java.util.List;
import java.util.Map;

public interface ApiService {
    // 🔐 AUTH ENDPOINTS
    @POST("api/auth/login")
    Call<LoginResponse> login(@Body Map<String, String> credentials);

    @POST("api/auth/register")
    Call<RegisterResponse> register(@Body Map<String, String> userData);

    @POST("api/auth/logout")
    Call<ApiResponse<Void>> logout();

    // 📋 COMPLAINT ENDPOINTS
    @GET("api/complaints")
    Call<ApiResponse<List<Complaint>>> getComplaints(
            @Query("page") int page,
            @Query("limit") int limit
    );

    @GET("api/complaints/{id}")
    Call<ApiResponse<Complaint>> getComplaintDetail(@Path("id") String id);

    @GET("api/complaints/{id}/history")
    Call<ApiResponse<List<Map<String, Object>>>> getStatusHistory(@Path("id") String id);

    @POST("api/complaints")
    Call<ApiResponse<Complaint>> createComplaint(@Body Map<String, String> complaintData);

    @PATCH("api/complaints/{id}/status")
    Call<ApiResponse<Complaint>> updateStatus(
            @Path("id") String id,
            @Body Map<String, String> statusData
    );

    // 👤 USER ENDPOINTS
    @GET("api/users/me")
    Call<ApiResponse<User>> getProfile();

    @PUT("api/users/me")
    Call<ApiResponse<User>> updateProfile(@Body Map<String, String> userData);
}