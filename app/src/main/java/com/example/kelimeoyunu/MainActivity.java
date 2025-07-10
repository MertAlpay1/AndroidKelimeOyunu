package com.example.kelimeoyunu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.kelimeoyunu.api.ApiService;
import com.example.kelimeoyunu.models.LoginResponse;
import com.example.kelimeoyunu.models.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {


    private static final String BASE_URL = "http://10.0.2.2:8080";
    EditText usernameInput, passwordInput;
    Button loginBtn, registerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
            usernameInput = findViewById(R.id.username_input);
            passwordInput = findViewById(R.id.password_input);
            loginBtn = findViewById(R.id.login_btn);
            registerBtn = findViewById(R.id.register_btn);

            WordValidator.loadWords(getApplicationContext());

            loginBtn.setOnClickListener(v->  {
                loginUser();
            });

            registerBtn.setOnClickListener(v->{
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            } );

    }


    private void loginUser() {
        String username = usernameInput.getText().toString();
        String password = passwordInput.getText().toString();

        if(username.isEmpty() || password.isEmpty()){

            Toast.makeText(MainActivity.this,"Lütfen kullanıcı adı ve şirenizi doldurun.",Toast.LENGTH_SHORT).show();

            return;
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        User user = new User(username, password);

        apiService.login(user).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                LoginResponse loginResponse = response.body();
                if (response.isSuccessful() && response.body() != null) {



                    if (loginResponse.getStatus().equals("success")) {
                        Toast.makeText(MainActivity.this, loginResponse.getMessage(), Toast.LENGTH_SHORT).show();

                        SharedPreferences preferences = getSharedPreferences("logged_user", MODE_PRIVATE);
                        SharedPreferences.Editor editor=preferences.edit();

                        editor.putString("username",loginResponse.getUsername());
                        editor.putInt("total_wins",Integer.parseInt(loginResponse.getWonGames()));
                        editor.putInt("total_games",Integer.parseInt(loginResponse.getTotalGames()));
                        editor.putString("user_json",loginResponse.getUserjson());

                        editor.apply();

                        System.out.println(loginResponse.getUserjson());


                        Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                        startActivity(intent);
                        finish();

                    } else {
                        Toast.makeText(MainActivity.this, "Giriş başarısız", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Giriş başarısız kullanıcı adı veya şifre yanlış", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e("LoginError", "Login failed: " + t.getMessage());
                Toast.makeText(MainActivity.this, "Hata: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void registerUser(){




    }




}