package ru.binaryblitz.sportup.server;

import com.google.gson.JsonArray;

import io.reactivex.Observable;
import retrofit2.http.GET;

public interface ApiEndpoints {
    @GET("cities")
    Observable<JsonArray> getCitiesList();
}
