package com.example.kelimeoyunu;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.kelimeoyunu.api.ApiService;
import com.example.kelimeoyunu.models.Game;
import com.example.kelimeoyunu.models.Mine;
import com.example.kelimeoyunu.models.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ActivityEndedGame extends AppCompatActivity {

    private static final String BASE_URL = "http://10.0.2.2:8080";

    private long gameId;

    User user;

    Gson gson = new Gson();

    TextView txtScore, txtRemainingLetters, txtOpponentScore, txtWinner;

    TextView txtPuanBolunmesi, txtPuanTransferi, txtHarfKaybı, txtEkstraHamleEngeli,txtKelimeİptali;

    List<Mine> mines=new ArrayList<>();

    int PuanBolunmesiCount=5 ,PuanTransferiCount=4 ,HarfKaybıCount=3,EkstraHamleEngeliCount=2,KelimeİptaliCount=2;

    Game gameState;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ended_game);

        SharedPreferences preferences = getSharedPreferences("logged_user", MODE_PRIVATE);
        String userJson=preferences.getString("user_json","");

        user = gson.fromJson(userJson, User.class);

        gameId=getIntent().getLongExtra("gameId",-1);

        txtScore = findViewById(R.id.txtTotalScore);
        txtRemainingLetters = findViewById(R.id.txtRemainingLetters);
        txtOpponentScore = findViewById(R.id.txtOpponentScore);
        txtWinner = findViewById(R.id.txtWinner);

        txtPuanBolunmesi = findViewById(R.id.txtPuanBolunmesi);
        txtPuanTransferi = findViewById(R.id.txtPuanTransferi);
        txtHarfKaybı = findViewById(R.id.txtHarfKaybı);
        txtEkstraHamleEngeli = findViewById(R.id.txtEkstraHamleEngeli);
        txtKelimeİptali = findViewById(R.id.txtKelimeİptali);


        fetchGameData(gameId);




    }


    private void fetchGameData(long gameId) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        apiService.getgame(gameId).enqueue(new Callback<Game>() {
            @Override
            public void onResponse(Call<Game> call, Response<Game> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Game game = response.body();
                    gameState=game;

                    updateUI();
                }
            }

            @Override
            public void onFailure(Call<Game> call, Throwable t) {
                Log.e("ActivityEndedGame", "Hata oluştu: " + t.getMessage());
            }
        });
    }

    private void updateUI(){

        if(gameState.getPlayer1().getUsername().equals(user.getUsername())){

            txtScore.setText("Puanınız : "+gameState.getPlayer1Point() );
            txtRemainingLetters.setText("Kalan harf sayisi : "+gameState.getRemainingLetters());
            txtOpponentScore.setText("Rakip Puanı : "+gameState.getPlayer2Point() );
            txtWinner.setText("Oyun kazananı : "+gameState.getWinner());

        }else{

            txtScore.setText("Puanınız : "+gameState.getPlayer2Point() );
            txtRemainingLetters.setText("Kalan harf sayisi : "+gameState.getRemainingLetters());
            txtOpponentScore.setText("Rakip Puanı : "+gameState.getPlayer1Point() );
            txtWinner.setText("Oyun kazananı : "+gameState.getWinner());

        }

        Type listType = new TypeToken<List<Mine>>() {}.getType();
        mines=gson.fromJson(gameState.getMineJson(),listType);

        if (gameState.getMineJson() != null) {
            for (Mine m : mines) {
                switch (m.getType()) {
                    case "Puan Bölüm":
                        PuanBolunmesiCount--;
                        break;
                    case "Puan Transfer":
                        PuanTransferiCount--;
                        break;
                    case "Harf Kaybı":
                        HarfKaybıCount--;
                        break;
                    case "Ekstra Hamle Engel":
                        EkstraHamleEngeliCount--;
                        break;
                    case "Kelime İptal":
                        KelimeİptaliCount--;
                        break;
                }
            }
        }

        txtPuanBolunmesi.setText("Puan Bölüm Mayını : Kelimeden %30 puan alınması.Basılma durumu : "+PuanBolunmesiCount);
        txtPuanTransferi.setText("Puan Transfer Mayını : Kelimeden alınan puanın rekibe verilmesi.Basılma durumu :"+PuanTransferiCount);
        txtHarfKaybı.setText("Harf Kaybı Mayını : Kullanıcıya yeni harfler verilmesi.Basılma durumu : "+HarfKaybıCount);
        txtEkstraHamleEngeli.setText("Ekstra Hamle Engel Mayını : Kelimenin özel bölgelerden bonus alamaması.Basılma Durumu : "+EkstraHamleEngeliCount);
        txtKelimeİptali.setText("Kelime İptal Mayını : Kelimeden alınan puanın 0 olması.Basılma Durumu : "+KelimeİptaliCount);
    }

}