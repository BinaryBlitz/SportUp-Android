package ru.binaryblitz.sportup.server;

import com.google.gson.JsonArray;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiEndpoints {
    @GET("cities")
    Call<JsonArray> getCitiesList();
}
