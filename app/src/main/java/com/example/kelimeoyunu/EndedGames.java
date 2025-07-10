package com.example.kelimeoyunu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kelimeoyunu.api.ApiService;
import com.example.kelimeoyunu.models.Game;
import com.example.kelimeoyunu.models.User;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EndedGames extends AppCompatActivity {

    private static final String BASE_URL = "http://10.0.2.2:8080";

    private RecyclerView recyclerView;
    private EndedGamesAdapter adapter;
    private Button returnBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ended_games);

        returnBtn = findViewById(R.id.return_btn);
        recyclerView = findViewById(R.id.recyclerViewEndedGames);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        returnBtn.setOnClickListener(v -> {
            Intent intent = new Intent(EndedGames.this, MenuActivity.class);
            startActivity(intent);
            finish();
        });

        loadEndedGames();
    }

    private void loadEndedGames() {
        SharedPreferences preferences = getSharedPreferences("logged_user", MODE_PRIVATE);
        String userJson = preferences.getString("user_json", null);

        if (userJson == null) {
            Toast.makeText(this, "Kullanıcı Bulunamadı", Toast.LENGTH_SHORT).show();
            return;
        }

        Gson gson = new Gson();
        User user = gson.fromJson(userJson, User.class);
        String loggedUsername = user.getUsername();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        apiService.getEndedGames(user).enqueue(new Callback<List<Game>>() {
            @Override
            public void onResponse(Call<List<Game>> call, Response<List<Game>> response) {
                if (response.body() != null && !response.body().isEmpty()) {
                    adapter = new EndedGamesAdapter(response.body(), loggedUsername);
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(EndedGames.this, "Bitmiş oyun bulunmamaktadır", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Game>> call, Throwable t) {
                Toast.makeText(EndedGames.this, "Sunucu hatası", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
