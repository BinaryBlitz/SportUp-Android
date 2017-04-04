package ru.binaryblitz.SportUp.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiEndpoints {
    @GET("cities")
    Observable<JsonArray> getCitiesList();

    @POST("events")
    Observable<JsonObject> createEvent(@Body JsonObject object, @Query("api_token") String token);

    @GET("cities/{id}/sport_types")
    Observable<JsonArray> getSportTypes(@Path("id") int id);

    @GET("cities/{id}/sport_types/{sport_type_id}/events")
    Observable<JsonArray> getEvents(@Path("id") int id, @Path("sport_type_id") int sportTypeId, @Query("date") String date);

    @GET("invites")
    Observable<JsonArray> getInvites(@Query("api_token") String token);

    @GET("memberships")
    Observable<JsonArray> getMemberships(@Query("api_token") String token);

    @GET("events/{id}")
    Observable<JsonObject> getEvent(@Path("id") int id, @Query("api_token") String token);

    @PATCH("verification_tokens/{token}")
    Observable<JsonObject> verifyPhoneNumber(@Body JsonObject body, @Path("token") String token);

    @POST("verification_tokens")
    Observable<JsonObject> authWithPhoneNumber(@Body JsonObject number);

    @POST("user")
    Observable<JsonObject> createUser(@Body JsonObject user);
}
