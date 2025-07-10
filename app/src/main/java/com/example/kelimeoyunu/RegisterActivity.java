package com.example.kelimeoyunu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RegisterActivity extends AppCompatActivity {

    private static final String BASE_URL = "http://10.0.2.2:8080";
    EditText usernameInput ,passwordInput,emailInput;
    Button registerBtn,returnBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        usernameInput=findViewById(R.id.username_input);
        passwordInput=findViewById(R.id.password_input);
        emailInput=findViewById(R.id.email_input);

        registerBtn=findViewById(R.id.register_btn);
        returnBtn=findViewById(R.id.return_btn);

        registerBtn.setOnClickListener(v->{
            registerUser();
        });

        returnBtn.setOnClickListener(v->{
            Intent intent=new Intent(RegisterActivity.this,MainActivity.class);
            startActivity(intent);
            finish();

        });



    }

    private void registerUser() {
        String username = usernameInput.getText().toString();
        String password = passwordInput.getText().toString();
        String email = emailInput.getText().toString();

        if (!validateInput(username, email, password)) {
            return;
        }

        User user = new User(username, password, email);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        apiService.register(user).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                LoginResponse loginResponse = response.body();
                if (response.isSuccessful() && response.body() != null) {

                    Toast.makeText(RegisterActivity.this, loginResponse.getMessage(), Toast.LENGTH_SHORT).show();

                    if (loginResponse.getStatus().equals("success")) {

                        SharedPreferences preferences = getSharedPreferences("logged_user", MODE_PRIVATE);
                        SharedPreferences.Editor editor=preferences.edit();
                        editor.putString("username",loginResponse.getUsername());
                        editor.putInt("total_wins",Integer.parseInt(loginResponse.getWonGames()));
                        editor.putInt("total_games",Integer.parseInt(loginResponse.getTotalGames()));
                        editor.apply();

                        Intent intent = new Intent(RegisterActivity.this, MenuActivity.class);
                        startActivity(intent);
                        finish();
                    }/*else{
                        Toast.makeText(RegisterActivity.this, "Kayıt başarısız"+loginResponse.getMessage(), Toast.LENGTH_SHORT).show();

                    }*/
                } else {
                    Toast.makeText(RegisterActivity.this, "Kayıt başarısız", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Hata: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public boolean isEmailValid(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public boolean isPasswordValid(String password) {
        String passwordPattern = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}";
        Pattern pattern = Pattern.compile(passwordPattern);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    public boolean validateInput(String username, String email, String password) {
        if (username.isEmpty()) {
            Toast.makeText(this, "Kullanıcı adı boş olamaz", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!isEmailValid(email)) {
            Toast.makeText(this, "Geçersiz e-posta adresi", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!isPasswordValid(password)) {
            Toast.makeText(this, "Şifre en az 8 karakter, bir büyük harf, bir küçük harf ve bir rakam içermelidir.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


}