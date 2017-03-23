package ru.binaryblitz.sportup.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiEndpoints {
    @GET("cities")
    Observable<JsonArray> getCitiesList();

    @GET("sport_types")
    Observable<JsonArray> getSportTypes();

    @GET("sport_types/{id}/events")
    Observable<JsonArray> getEvents(@Path("id") int id, @Query("date") String date);
}
