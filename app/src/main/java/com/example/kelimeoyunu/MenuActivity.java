package com.example.kelimeoyunu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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


public class MenuActivity extends AppCompatActivity {

    private static final String BASE_URL = "http://10.0.2.2:8080";

    Button newgameBtn,activegamesBtn,endedgamesBtn,logoffBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        TextView userInfoText = findViewById(R.id.userInfoText);

        SharedPreferences preferences = getSharedPreferences("logged_user", MODE_PRIVATE);
        String username = preferences.getString("username", "Kullanıcı Bulunamadı");
        int totalGames = preferences.getInt("total_games", 0);
        int wonGames = preferences.getInt("total_wins", 0);

        int successRate = totalGames > 0 ? (wonGames * 100 / totalGames) : 0;

        String info = "Kullanıcı: " + username + " | Başarı: " + successRate + "%";
        userInfoText.setText(info);

        newgameBtn=findViewById(R.id.newgame_btn);
        activegamesBtn=findViewById(R.id.activegames_btn);
        endedgamesBtn=findViewById(R.id.endedgames_btn);
        logoffBtn=findViewById(R.id.logoff_btn);

        newgameBtn.setOnClickListener(v->{

            Intent intent=new Intent(MenuActivity.this, NewGameActivity.class);
            startActivity(intent);
            finish();

        });

        activegamesBtn.setOnClickListener(v->{
            GetActiveGames();

        });

        endedgamesBtn.setOnClickListener(v->{
            getEndedGames();
        });


        logoffBtn.setOnClickListener(v->{

            SharedPreferences sharedPreferences=getSharedPreferences("logged_user",MODE_PRIVATE);
            SharedPreferences.Editor editor= preferences.edit();
            editor.clear();
            editor.apply();

            Toast.makeText(MenuActivity.this,"Başarıyla çıkış yapıldı.",Toast.LENGTH_SHORT).show();

            Intent intent=new Intent(MenuActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        });



    }

    private void GetActiveGames(){

        SharedPreferences preferences = getSharedPreferences("logged_user", MODE_PRIVATE);
        String userjson = preferences.getString("user_json", null);
        Gson gson = new Gson();

        if (userjson == null) {

            Toast.makeText(MenuActivity.this, "Kullanıcı Bulunamadı", Toast.LENGTH_SHORT).show();
            return ;
        }

        User user = gson.fromJson(userjson, User.class);

        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService apiService=retrofit.create(ApiService.class);
        apiService.getGames(user).enqueue(new Callback<List<Game>>() {
            @Override
            public void onResponse(Call<List<Game>> call, Response<List<Game>> response) {
                List<Game> gameResponse=response.body();

                if(gameResponse!=null   && !gameResponse.isEmpty()){


                    for (Game game : gameResponse) {
                        Log.d("Game Info", "Game ID: " + game.getId() +
                                " Player1: " + game.getPlayer1().getUsername() +
                                " Player2: " + game.getPlayer2().getUsername() +
                                " State: " + game.getGameState());
                    }
                    Intent intent=new Intent(MenuActivity.this,ActiveGamesActivity.class);
                    startActivity(intent);
                    finish();

                }else{
                    Toast.makeText(MenuActivity.this, "Aktif oyununuz bulunmamaktadır", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<List<Game>> call, Throwable t) {

                Toast.makeText(MenuActivity.this, "Sunucu Hatası", Toast.LENGTH_SHORT).show();


            }
        });

    }

    private void getEndedGames(){

        SharedPreferences preferences = getSharedPreferences("logged_user", MODE_PRIVATE);
        String userjson = preferences.getString("user_json", null);
        Gson gson = new Gson();

        if (userjson == null) {

            Toast.makeText(MenuActivity.this, "Kullanıcı Bulunamadı", Toast.LENGTH_SHORT).show();
            return ;
        }

        User user = gson.fromJson(userjson, User.class);

        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService apiService=retrofit.create(ApiService.class);
        apiService.getEndedGames(user).enqueue(new Callback<List<Game>>() {
            @Override
            public void onResponse(Call<List<Game>> call, Response<List<Game>> response) {
                List<Game> gameResponse=response.body();

                if(gameResponse!=null   && !gameResponse.isEmpty()){

                    for (Game game : gameResponse) {
                        Log.d("Game Info", "Game ID: " + game.getId() +
                                " Player1: " + game.getPlayer1().getUsername() +
                                " Player2: " + game.getPlayer2().getUsername() +
                                " State: " + game.getGameState());
                    }
                    Intent intent=new Intent(MenuActivity.this,EndedGames.class);
                    startActivity(intent);
                    finish();

                }else{
                    Toast.makeText(MenuActivity.this, "Bitmiş oyununuz bulunmamaktadır", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<List<Game>> call, Throwable t) {

                Toast.makeText(MenuActivity.this, "Sunucu Hatası", Toast.LENGTH_SHORT).show();


            }
        });


    }


}
