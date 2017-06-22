package me.philip.tv.client4suki.api;

import com.google.gson.JsonObject;

import io.reactivex.Observable;
import me.philip.tv.client4suki.model.Account;
import retrofit2.http.*;

/**
 * Created by phili on 6/13/2017.
 */

public interface Endpoint {

    @POST("api/user/login")
    public Observable<JsonObject> login(@Body Account account);

    @POST("api/user/logout")
    public Observable<String> logout();

    @GET("api/user/info")
    public Observable<JsonObject> getUserInfo();

    @GET("api/home/on_air")
    public Observable<JsonObject> onAir(
            @Query("type") int count
    );

    @GET("api/home/my_bangumi")
    public Observable<JsonObject> myBangumi();

    @GET("api/home/bangumi")
    public Observable<JsonObject> getSearchBangumi(
            @Query("count") int count,
            @Query("order_by") String orderBy,
            @Query("page") int page,
            @Query("sort") String sort

    );

    @GET("api/home/bangumi/{id}")
    public Observable<JsonObject> getBangumiDetail(
            @Path("id") String id
    );

    @GET("api/home/episode/{id}")
    public Observable<JsonObject> getEpisode(
            @Path("id") String id
    );
}
