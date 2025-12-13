package com.example.project_uts.network;

import android.content.Context;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;

public class AuthInterceptor implements Interceptor {
    private Context context;

    public AuthInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();

        AuthManage authManager = new AuthManage(context);
        String token = authManager.getToken();

        Request.Builder requestBuilder = originalRequest.newBuilder()
                .header("Content-Type", "application/json");

        if (token != null && !token.isEmpty()) {
            requestBuilder.header("Authorization", "Bearer " + token);
        }

        return chain.proceed(requestBuilder.build());
    }
}