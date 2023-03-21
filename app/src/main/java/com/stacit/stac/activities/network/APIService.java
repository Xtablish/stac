package com.stacit.stac.activities.network;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

public interface APIService
{
    @POST("send")
    Call<String> senderMessage(
            @HeaderMap HashMap<String, String> headers,
            @Body String messageBody
    );
}
