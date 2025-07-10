package com.example.kelimeoyunu;


import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.example.kelimeoyunu.api.ApiService;
import com.example.kelimeoyunu.models.CreateGame;
import com.example.kelimeoyunu.models.CreateGameResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NewGameActivity extends AppCompatActivity {

    private static final String BASE_URL = "http://10.0.2.2:8080";
    Button m2Btn,m5Btn,h12Btn,h24Btn,returnBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);

        m2Btn=findViewById(R.id.newgame2m_btn);
        m5Btn=findViewById(R.id.newgame5m_btn);
        h12Btn=findViewById(R.id.newgame12h_btn);
        h24Btn=findViewById(R.id.newgame24h_btn);
        returnBtn=findViewById(R.id.return_btn);

        m2Btn.setOnClickListener(v->{
            newGame("2 Dakika");
        });
        m5Btn.setOnClickListener(v->{
            newGame("5 Dakika");
        });
        h12Btn.setOnClickListener(v->{
            newGame("12 Saat");
        });
        h24Btn.setOnClickListener(v->{
            newGame("24 Saat");
        });


        returnBtn.setOnClickListener(v->{
            Intent intent = new Intent(NewGameActivity.this, MenuActivity.class);
            startActivity(intent);
            finish();
        });


    }


    private void newGame(String selectedDuration){


        SharedPreferences preferences = getSharedPreferences("logged_user", MODE_PRIVATE);
        String username = preferences.getString("username", "");

        if(username==null ){
            Toast.makeText(NewGameActivity.this, "Hata kullanıcı bulunamadı", Toast.LENGTH_SHORT).show();
            return;
        }
        if(selectedDuration==null){
            Toast.makeText(NewGameActivity.this, "Hata oyun süresi seçilmedi.", Toast.LENGTH_SHORT).show();
            return;
        }

        CreateGame game=new CreateGame();
        game.setPlayer(username);
        game.setSelectedDuration(selectedDuration);

        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService=retrofit.create(ApiService.class);
        apiService.createGame(game).enqueue(new Callback<CreateGameResponse>() {
            @Override
            public void onResponse(Call<CreateGameResponse> call, Response<CreateGameResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CreateGameResponse createGameResponse = response.body();

                    Toast.makeText(NewGameActivity.this,createGameResponse.getMessage(), Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(NewGameActivity.this, MenuActivity.class);
                    startActivity(intent);
                    finish();

                } else {
                    Toast.makeText(NewGameActivity.this, "Sunucu hatası: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CreateGameResponse> call, Throwable t) {
                Log.e("newGameError", "newGameError failed: " + t.getMessage());
                Toast.makeText(NewGameActivity.this, "Hata: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });




    }



}