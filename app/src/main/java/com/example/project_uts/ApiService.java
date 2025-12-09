package com.example.project_uts;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {
    @GET("api/complaints")
    Call<List<Komplain>> getComplaints();

    @GET("api/complaints/{id}/statuses")
    Call<List<ComplaintStatus>> getComplaintStatuses(@Path("id") String complaintId);


    @POST("api/complaints/{id}/statuses")
    Call<ResponseBody> addComplaintStatus(
            @Path("id") String complaintId,
            @Body ComplaintStatusRequest request
    );

    @GET("api/complaints/{id}")
    Call<ComplaintResponse> getComplaintById(@Path("id") String id);

    @POST("api/complaints/{id}/statuses")
    Call<Void> updateStatus(
            @Path("id") String complaintId,
            @Body ComplaintStatusRequest request
    );

}


