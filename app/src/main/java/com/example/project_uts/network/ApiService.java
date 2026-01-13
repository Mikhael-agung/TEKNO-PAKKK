package com.example.project_uts.network;

import com.example.project_uts.Teknisi.Model.Komplain;
import com.example.project_uts.Teknisi.Model.TeknisiComplaintsResponse;
import com.example.project_uts.models.ApiResponse;
import com.example.project_uts.models.Complaint;
import com.example.project_uts.models.ComplaintResponse;
import com.example.project_uts.models.LoginResponse;
import com.example.project_uts.models.RegisterResponse;
import com.example.project_uts.models.User;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    //  AUTH ENDPOINTS
    @POST("api/auth/login")
    Call<LoginResponse> login(@Body Map<String, String> credentials);

    @POST("api/auth/register")
    Call<RegisterResponse> register(@Body Map<String, String> userData);

    @POST("api/auth/logout")
    Call<ApiResponse<Void>> logout();

    // CUSTOMER ENDPOINTS
    @GET("api/complaints")
    Call<ApiResponse<ComplaintResponse>> getComplaints(
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

    @GET("api/complaints/user-complaints")
    Call<ApiResponse<ComplaintResponse>> getUserComplaints(
            @Query("page") int page,
            @Query("limit") int limit
    );

    // USER ENDPOINTS
    @GET("api/users/me")
    Call<ApiResponse<User>> getProfile();

    @PUT("api/users/me")
    Call<ApiResponse<User>> updateProfile(@Body Map<String, String> userData);

    //  TEKNISI ENDPOINTS
    @GET("api/teknisi/dashboard/stats")
    Call<ApiResponse<Map<String, Object>>> getDashboardStats();

    @GET("api/teknisi/complaints/ready")
    Call<ApiResponse<TeknisiComplaintsResponse>> getReadyComplaints(
            @Query("page") int page,
            @Query("limit") int limit
    );

    @GET("api/teknisi/complaints/progress")
    Call<ApiResponse<TeknisiComplaintsResponse>> getProgressComplaints(
            @Query("page") int page,
            @Query("limit") int limit
    );

    @GET("api/teknisi/complaints/completed")
    Call<ApiResponse<TeknisiComplaintsResponse>> getCompletedComplaints(
            @Query("page") int page,
            @Query("limit") int limit
    );

    @PATCH("api/teknisi/complaints/{id}/take")
    Call<ApiResponse<Komplain>> takeComplaint(@Path("id") String id);

    @PATCH("api/teknisi/complaints/{id}/status")
    Call<ApiResponse<Komplain>> updateComplaintStatus(
            @Path("id") String id,
            @Query("status") String status,
            @Query("resolution_notes") String resolutionNotes
    );
}