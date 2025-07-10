package com.example.kelimeoyunu.api;


import com.example.kelimeoyunu.models.CreateGame;
import com.example.kelimeoyunu.models.CreateGameResponse;
import com.example.kelimeoyunu.models.Game;
import com.example.kelimeoyunu.models.LoginResponse;
import com.example.kelimeoyunu.models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {

    @POST("/api/login")
    Call<LoginResponse> login(@Body User user);

    @POST("/api/register")
    Call<LoginResponse>register(@Body User user);

    @POST("/api/creategame")
    Call<CreateGameResponse>createGame(@Body CreateGame createGame);

    @POST("/api/getactivegames")
    Call<List<Game>>getGames(@Body User user);

    @POST("/api/getendedgames")
    Call<List<Game>>getEndedGames(@Body User user);

    @POST("/api/getgame")
    Call<Game>getgame(@Body long id);

    @POST("/api/updategame")
    Call<Game>updateGame(@Body Game game);

    @POST("/api/updateuser")
    Call<User>updateUser(@Body User user);


}
