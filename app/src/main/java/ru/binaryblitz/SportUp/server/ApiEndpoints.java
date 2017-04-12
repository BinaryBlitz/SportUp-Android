package ru.binaryblitz.SportUp.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiEndpoints {
    @GET("events/{id}/memberships")
    Observable<JsonArray> getTeams(@Path("id") int id, @Query("api_token") String token);

    @GET("cities")
    Observable<JsonArray> getCitiesList();

    @GET("user")
    Observable<JsonObject> getUser(@Query("api_token") String token);

    @POST("events/{id}/memberships")
    Observable<JsonObject> joinEvent(@Path("id") int id, @Query("api_token") String token);

    @POST("events/{event_id}/votes")
    Observable<JsonObject> vote(@Path("event_id") int id, @Body JsonObject object, @Query("api_token") String token);

    @POST("events/{id}/teams")
    Observable<JsonObject> joinTeam(@Path("id") int id, @Body JsonObject object, @Query("api_token") String token);

    @PATCH("events/{id}/teams")
    Observable<JsonObject> updateTeam(@Path("id") int id, @Body JsonObject object, @Query("api_token") String token);

    @DELETE("memberships/{id}")
    Observable<JsonObject> leaveEvent(@Path("id") int id, @Query("api_token") String token);

    @DELETE("events/{id}")
    Observable<JsonObject> deleteEvent(@Path("id") int id, @Query("api_token") String token);

    @POST("events")
    Observable<JsonObject> createEvent(@Body JsonObject object, @Query("api_token") String token);

    @PATCH("events/{id}")
    Observable<JsonObject> editEvent(@Body JsonObject object, @Path("id") int id, @Query("api_token") String token);

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

    @PATCH("user")
    Observable<JsonObject> updateUser(@Body JsonObject body, @Query("api_token") String token);

    @POST("verification_tokens")
    Observable<JsonObject> authWithPhoneNumber(@Body JsonObject number);

    @POST("user")
    Observable<JsonObject> createUser(@Body JsonObject user);
}
