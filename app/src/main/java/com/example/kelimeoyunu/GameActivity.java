package com.example.kelimeoyunu;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Consumer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.SuperscriptSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kelimeoyunu.api.ApiService;
import com.example.kelimeoyunu.models.Game;
import com.example.kelimeoyunu.models.LetterInfo;
import com.example.kelimeoyunu.models.Letters;
import com.example.kelimeoyunu.models.Mine;
import com.example.kelimeoyunu.models.PlacedLetter;
import com.example.kelimeoyunu.models.Reward;
import com.example.kelimeoyunu.models.User;
import com.example.kelimeoyunu.models.WordResult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class GameActivity extends AppCompatActivity {

    GridLayout board;
    TextView playerInfo, opponentInfo, remainingLetters,turnInfo,remainingTimeInfo;
    LinearLayout lettersLayout;
    Button confirmBtn,cancelBtn,passBtn,resignBtn,backBtn;
    private Handler handler = new Handler();
    private Runnable runnable;
    private static final String BASE_URL = "http://10.0.2.2:8080";
    Map<String, String> bonusStartCells = new HashMap<>();
    private final int interval = 1000;
    private long gameId;
    private User user;
    private Game gameState;
    Gson gson = new Gson();
    private boolean updatePlayerLetters =true;
    private boolean updateMap=true;
    private final Set<String> occupiedCells = new HashSet<>();
    private String jokerLetter="";
    List<PlacedLetter> placedLetters = new ArrayList<>();
    List<PlacedLetter> mapLetters = new ArrayList<>();
    List<Letters> letters=new ArrayList<>();

    private boolean wordCheck=true;
    Set<WordResult> formedWords = new LinkedHashSet<>();
    private boolean isMovedLetter=false;
    private boolean isAddedLetter=false;
    private boolean isTouchedMoveLetter=false;
    private boolean isTouchedAddLetter=false;
    List<Mine> mines=new ArrayList<>();
    List<Reward> rewards=new ArrayList<>();

    private boolean harfHaybı=false;
    private boolean puanTransferi=false;
    private int puanTransferiMiktarı=0;
    List<String>mineText =new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        playerInfo = findViewById(R.id.playerInfo);
        opponentInfo = findViewById(R.id.opponentInfo);
        remainingLetters = findViewById(R.id.remainingLetters);
        turnInfo=findViewById(R.id.turnInfo);
        remainingTimeInfo=findViewById(R.id.remainingTime);


        board = findViewById(R.id.board);
        lettersLayout = findViewById(R.id.lettersLayout);
        confirmBtn=findViewById(R.id.confirmButton);
        cancelBtn=findViewById(R.id.cancelButton);
        passBtn=findViewById(R.id.passButton);
        resignBtn=findViewById(R.id.resignButton);
        backBtn=findViewById(R.id.backButton);


        SharedPreferences preferences = getSharedPreferences("logged_user", MODE_PRIVATE);
        String userJson=preferences.getString("user_json","");

        Intent intent = getIntent();
        gameId = intent.getLongExtra("game_id", -1);
        user = gson.fromJson(userJson, User.class);

        confirmBtn.setOnClickListener(v->{

            if(gameState.getCurrentPlayer().getUsername().equals(user.getUsername())) {


                findwords();

                if (wordCheck) {

                    int pointWords = 0;
                    for (WordResult f : formedWords) {

                        pointWords = pointWords + f.getPoint();
                    }

                    if (user.getUsername().equals(gameState.getPlayer1().getUsername())) {

                        gameState.setPlayer1LettersJson(gson.toJson(letters));
                        gameState.setPlayer1Point(gameState.getPlayer1Point() + pointWords);
                        gameState.setPlayer2Point(gameState.getPlayer2Point() + puanTransferiMiktarı);
                        gameState.setPlacedLettersJson(gson.toJson(mapLetters));
                        gameState.setMoveTimeLeft(gameState.getDuration());
                        gameState.setCurrentPlayer(gameState.getPlayer2());
                        gameState.setMineJson(gson.toJson(mines));
                        gameState.setRewardJson(gson.toJson(rewards));
                        gameState.setPlayer1PassCount(0);

                        if (harfHaybı) {
                            gameState = handleShuffleLetters(gameState, 1);
                            Toast.makeText(GameActivity.this, "Harf dağıtılıyor!", Toast.LENGTH_SHORT).show();
                            harfHaybı=false;
                        }
                        if (gameState.getBannedAreaPlayer() != null && gameState.getBannedAreaPlayer().equals(user.getUsername())) {

                            gameState.setBannedAreaPlayer("");

                        }

                        if (gameState.getFrozenLetterPlayer() != null && gameState.getFrozenLetterPlayer().equals(user.getUsername())) {

                            gameState.setFrozenLetterPlayer("");
                        }

                        gameState = hangleRewards(gameState, 1);

                        gameState.setUpdateGamePlayer1(true);
                        gameState.setUpdateGamePlayer2(true);


                        updateGame(gameState);

                    } else {

                        gameState.setPlayer2LettersJson(gson.toJson(letters));
                        gameState.setPlayer2Point(gameState.getPlayer2Point() + pointWords);
                        gameState.setPlayer1Point(gameState.getPlayer1Point() + puanTransferiMiktarı);
                        gameState.setPlacedLettersJson(gson.toJson(mapLetters));
                        gameState.setMoveTimeLeft(gameState.getDuration());
                        gameState.setCurrentPlayer(gameState.getPlayer1());
                        gameState.setMineJson(gson.toJson(mines));
                        gameState.setRewardJson(gson.toJson(rewards));
                        gameState.setPlayer2PassCount(0);


                        if (harfHaybı) {
                            gameState = handleShuffleLetters(gameState, 2);
                            Toast.makeText(GameActivity.this, "Harf dağıtılıyor!", Toast.LENGTH_SHORT).show();
                        }

                        if (gameState.getBannedAreaPlayer() != null && gameState.getBannedAreaPlayer().equals(user.getUsername())) {

                            gameState.setBannedAreaPlayer("");

                        }

                        if (gameState.getFrozenLetterPlayer() != null && gameState.getFrozenLetterPlayer().equals(user.getUsername())) {

                            gameState.setFrozenLetterPlayer("");
                        }

                        gameState = hangleRewards(gameState, 2);

                        gameState.setUpdateGamePlayer1(true);
                        gameState.setUpdateGamePlayer2(true);

                        updateGame(gameState);

                    }

                    List<Letters> player1Letters = new ArrayList<>();
                    List<Letters> player2Letters = new ArrayList<>();

                    Type listType = new TypeToken<List<Letters>>() {
                    }.getType();

                    String jsonPlayer1Letters = gameState.getPlayer1LettersJson();
                    if (jsonPlayer1Letters != null) {
                        player1Letters = gson.fromJson(jsonPlayer1Letters, listType);
                    }


                    String jsonPlayer2Letters = gameState.getPlayer2LettersJson();
                    if (jsonPlayer2Letters != null) {
                        player2Letters = gson.fromJson(jsonPlayer2Letters, listType);
                    }

                    if (player1Letters.isEmpty() || player2Letters.isEmpty()) {
                        gameState.setGameState("ended");

                        int player1Point = gameState.getPlayer1Point();
                        int player2Point = gameState.getPlayer2Point();

                        int remainingPoints = 0;
                        boolean player1Finished = player1Letters.isEmpty();

                        if (player1Finished) {
                            for (Letters letter : player2Letters) {
                                remainingPoints += letter.getLetterInfo().getPoint();
                            }
                            player1Point += remainingPoints;
                            player2Point -= remainingPoints;
                        } else {
                            for (Letters letter : player1Letters) {
                                remainingPoints += letter.getLetterInfo().getPoint();
                            }
                            player2Point += remainingPoints;
                            player1Point -= remainingPoints;
                        }

                        player1Point = Math.max(0, player1Point);
                        player2Point = Math.max(0, player2Point);

                        gameState.setPlayer1Point(player1Point);
                        gameState.setPlayer2Point(player2Point);


                        User player1=gameState.getPlayer1();
                        User player2=gameState.getPlayer2();

                        if (player1Point > player2Point) {
                            gameState.setWinner(gameState.getPlayer1().getUsername());

                            player1.setTotalGames(player1.getTotalGames()+1);
                            player2.setTotalGames(player2.getTotalGames()+1);

                            player1.setWonGames(player1.getWonGames()+1);

                        } else if (player2Point > player1Point) {
                            gameState.setWinner(gameState.getPlayer2().getUsername());

                            player1.setTotalGames(player1.getTotalGames()+1);
                            player2.setTotalGames(player2.getTotalGames()+1);

                            player2.setWonGames(player2.getWonGames()+1);
                        } else {
                            gameState.setWinner("berabere");

                            player1.setTotalGames(player1.getTotalGames()+1);
                            player2.setTotalGames(player2.getTotalGames()+1);

                        }

                        updateUser(player1);
                        updateUser(player2);

                        updateGame(gameState);
                    }


                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        resetBoard();
                    }, 1500);

                } else {
                    Toast.makeText(GameActivity.this, "Tüm Kelimelerin doğru olması gerekir!", Toast.LENGTH_SHORT).show();

                }
            }
            else{

                Toast.makeText(GameActivity.this,"Tur sırası karşı rakipte",Toast.LENGTH_SHORT).show();
            }


        });

        cancelBtn.setOnClickListener(v->{
            resetBoard();
        });

        passBtn.setOnClickListener(v->{

            if(gameState.getCurrentPlayer().getUsername().equals(user.getUsername())){

                if(gameState.getPlayer1().getUsername().equals(user.getUsername())){
                    gameState=handleShuffleLetters(gameState,1);
                    gameState.setCurrentPlayer(gameState.getPlayer2());

                    gameState.setPlayer1PassCount(gameState.getPlayer1PassCount()+1);

                    if(gameState.getBannedAreaPlayer()!=null&&gameState.getBannedAreaPlayer().equals(user.getUsername())){

                        gameState.setBannedAreaPlayer("");

                    }

                    if(gameState.getFrozenLetterPlayer()!=null&&gameState.getFrozenLetterPlayer().equals(user.getUsername())){

                        gameState.setFrozenLetterPlayer("");
                    }
                }else{
                    gameState= handleShuffleLetters(gameState,2);
                    gameState.setCurrentPlayer(gameState.getPlayer1());
                    gameState.setPlayer2PassCount(gameState.getPlayer2PassCount()+1);

                    if(gameState.getBannedAreaPlayer()!=null&&gameState.getBannedAreaPlayer().equals(user.getUsername())){

                        gameState.setBannedAreaPlayer("");

                    }

                    if(gameState.getFrozenLetterPlayer()!=null&&gameState.getFrozenLetterPlayer().equals(user.getUsername())){

                        gameState.setFrozenLetterPlayer("");
                    }
                }

                if(gameState.getPlayer1PassCount()>=2 && gameState.getPlayer2PassCount()>=2){

                    User player1=gameState.getPlayer1();
                    User player2=gameState.getPlayer2();

                    gameState.setGameState("ended");
                    int player1Point=gameState.getPlayer1Point();
                    int player2Point=gameState.getPlayer2Point();


                    if(player1Point>player2Point){
                        gameState.setWinner(gameState.getPlayer1().getUsername());

                        player1.setTotalGames(player1.getTotalGames()+1);
                        player2.setTotalGames(player2.getTotalGames()+1);

                        player1.setWonGames(player1.getWonGames()+1);

                    }else if(player2Point>player1Point){
                        gameState.setWinner(gameState.getPlayer2().getUsername());

                        player1.setTotalGames(player1.getTotalGames()+1);
                        player2.setTotalGames(player2.getTotalGames()+1);

                        player2.setWonGames(player2.getWonGames()+1);
                    }else{

                        gameState.setWinner("berabere");

                        player1.setTotalGames(player1.getTotalGames()+1);
                        player2.setTotalGames(player2.getTotalGames()+1);


                    }

                    updateUser(player1);
                    updateUser(player2);
                }

                gameState.setMoveTimeLeft(gameState.getDuration());

               updateGame(gameState);
               resetBoard();
            }else{

                Toast.makeText(GameActivity.this, "Tur karşı rakibin!", Toast.LENGTH_SHORT).show();

            }

        });

        resignBtn.setOnClickListener(v->{

            User player1=gameState.getPlayer1();
            User player2=gameState.getPlayer2();

            if(gameState.getPlayer1().getUsername().equals(user.getUsername())){
                gameState.setGameState("ended");
                gameState.setWinner(gameState.getPlayer2().getUsername());

                player1.setTotalGames(player1.getTotalGames()+1);
                player2.setTotalGames(player2.getTotalGames()+1);

                player2.setWonGames(player2.getWonGames()+1);


            }
            else{
                gameState.setGameState("ended");
                gameState.setWinner(gameState.getPlayer1().getUsername());

                player1.setTotalGames(player1.getTotalGames()+1);
                player2.setTotalGames(player2.getTotalGames()+1);
                player1.setWonGames(player1.getWonGames()+1);
            }


            updateUser(player1);
            updateUser(player2);
            updateGame(gameState);
        });


        backBtn.setOnClickListener(v->{
            Toast.makeText(GameActivity.this, "Menüye dönülüyor!", Toast.LENGTH_SHORT).show();

            Intent Menuintent = new Intent(GameActivity.this, MenuActivity.class);
            startActivity(Menuintent);
            finish();
        });

        createMap();
        getGameData(gameId);

    }


    private void updateGame(Game game){

        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService apiService=retrofit.create(ApiService.class);
        apiService.updateGame(game).enqueue(new Callback<Game>() {
            @Override
            public void onResponse(Call<Game> call, Response<Game> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(GameActivity.this, "kaydedildi.", Toast.LENGTH_SHORT).show();
                }
                else {
                    Log.e("Server", "Sunucu hatası: " + response.code());
                    Toast.makeText(GameActivity.this, "Sunucu hatası!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Game> call, Throwable t) {
                Log.e("Server", "Bağlantı hatası: " + t.getMessage());
                Toast.makeText(GameActivity.this, "Bağlantı hatası!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void updateUser(User user){

        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService apiService=retrofit.create(ApiService.class);
        apiService.updateUser(user).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {

                if (response.isSuccessful()) {
                    Toast.makeText(GameActivity.this, "Kullanıcıya kaydedildi.", Toast.LENGTH_SHORT).show();
                }
                else {
                    Log.e("Server", "Sunucu hatası: " + response.code());
                    Toast.makeText(GameActivity.this, "Sunucu hatası!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("Server", "Bağlantı hatası: " + t.getMessage());
                Toast.makeText(GameActivity.this, "Bağlantı hatası!", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void createMap() {

        createBonusMap();

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int cellSize = width / 15;

        for (int row = 0; row < 15; row++) {
            for (int col = 0; col < 15; col++) {
                TextView cell = new TextView(this);
                cell.setWidth(cellSize);
                cell.setHeight(cellSize);
                cell.setGravity(Gravity.CENTER);
                cell.setTextSize(12);
                cell.setTextColor(getResources().getColor(R.color.white));

                String key = row + "," + col;
                if (bonusStartCells.containsKey(key)) {
                    String bonus = bonusStartCells.get(key);
                    //cell.setText(bonus);
                    switch (bonus) {
                        case "H2":
                            cell.setBackgroundResource(R.drawable.h2);
                            cell.setText("H\u00B2");
                            break;
                        case "H3":
                            cell.setBackgroundResource(R.drawable.h3);
                            cell.setText("H\u00B3");
                            break;
                        case "K2":
                            cell.setBackgroundResource(R.drawable.k2);
                            cell.setText("K\u00B2");
                            break;
                        case "K3":
                            cell.setBackgroundResource(R.drawable.k3);
                            cell.setText("K\u00B3");
                            break;
                        case "★":
                            cell.setBackgroundResource(R.drawable.star);
                            cell.setText("★");
                            break;
                        default:
                            cell.setBackgroundResource(R.drawable.grid_cell);
                    }
                } else {
                    cell.setText("");
                    cell.setBackgroundResource(R.drawable.grid_cell);
                }
                cell.setTag(row + "," + col);
                cell.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if(!gameState.getCurrentPlayer().getUsername().equals(user.getUsername())){
                            Toast.makeText(GameActivity.this, "Tur karşı rakipte!", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        if(!isTouchedAddLetter) {
                            isTouchedMoveLetter = true;
                            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                            v.startDragAndDrop(null, shadowBuilder, v, 0);

                            if (isMovedLetter) {
                                Toast.makeText(GameActivity.this, "Sadece 1 harf taşıyabilirsiniz!", Toast.LENGTH_SHORT).show();
                            }
                            return true;
                        }

                        return false;
                    }
                });


                cell.setOnDragListener(new View.OnDragListener() {
                    @Override
                    public boolean onDrag(View v, DragEvent event) {
                        switch (event.getAction()) {
                            case DragEvent.ACTION_DRAG_STARTED:
                                return true;

                            case DragEvent.ACTION_DRAG_ENTERED:
                                return true;

                            case DragEvent.ACTION_DRAG_EXITED:
                                return true;

                            case DragEvent.ACTION_DROP:

                                if (isTouchedAddLetter ) {


                                    TextView targetCell = (TextView) v;
                                    String coord = (String) targetCell.getTag();

                                    if (coord == null || occupiedCells.contains(coord)) {
                                        return false;
                                    }

                                    String[] parts = coord.split(",");
                                    int row = Integer.parseInt(parts[0]);
                                    int col = Integer.parseInt(parts[1]);


                                    View draggedView = (View) event.getLocalState();
                                    TextView letterText = (TextView) ((LinearLayout) draggedView).getChildAt(0);
                                    TextView pointText = (TextView) ((LinearLayout) draggedView).getChildAt(1);

                                    String letter = letterText.getText().toString();
                                    String point = pointText.getText().toString();
                                    int intPoint = Integer.parseInt(point);


                                    boolean check = false;

                                    for(PlacedLetter l : mapLetters){
                                        if(l.getRow()==row && l.getCol()==col){
                                            Toast.makeText(GameActivity.this, "Yeni harf mevcut harfin üstüne koyulamaz!", Toast.LENGTH_SHORT).show();
                                            return false;
                                        }
                                    }

                                    if (mapLetters == null || mapLetters.isEmpty()) {
                                        if (!(row == 7 && col == 7)) {
                                            Toast.makeText(GameActivity.this, "İlk harf tahtanın ortasına konmalıdır!", Toast.LENGTH_SHORT).show();
                                            return false;
                                        }

                                    } else {
                                        check=false;
                                        int[][] directions = {
                                                {-1, 0}, {1, 0}, {0, -1}, {0, 1},
                                                {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
                                        };
                                        for (int[] dir : directions) {
                                            int nr = row + dir[0];
                                            int nc = col + dir[1];

                                            for (PlacedLetter l : mapLetters) {
                                                if (l.getRow() == nr && l.getCol() == nc) {
                                                    check = true;
                                                    break;
                                                }

                                            }
                                        }


                                        if (!check) {
                                            Toast.makeText(GameActivity.this, "Yeni harf mevcut harflerle bitişik olmalıdır!", Toast.LENGTH_SHORT).show();
                                            return false;
                                        }
                                    }

                                    if(gameState.getBannedAreaPlayer()!=null&&gameState.getBannedAreaPlayer().equals(user.getUsername())) {

                                        String bannedArea = gameState.getBannedArea();
                                        if (bannedArea == null) return false;

                                        switch (bannedArea) {
                                            case "LEFT":
                                                if (col < 7) {
                                                    Toast.makeText(GameActivity.this, "Sol tarafa harf yerleştiremezsiniz!", Toast.LENGTH_SHORT).show();
                                                    return false;
                                                }
                                                break;
                                            case "RIGHT":
                                                if (col > 7) {
                                                    Toast.makeText(GameActivity.this, "Sağ tarafa harf yerleştiremezsiniz!", Toast.LENGTH_SHORT).show();
                                                    return false;
                                                }
                                        }
                                    }


                                    if (letter.equals("JOKER")) {
                                        showLetterPickerDialog(newLetter -> {
                                            jokerLetter=newLetter;

                                            SpannableString spannable = new SpannableString(jokerLetter + point);
                                            spannable.setSpan(new RelativeSizeSpan(0.5f), jokerLetter.length(), jokerLetter.length() + point.length(), 0);
                                            spannable.setSpan(new SuperscriptSpan(), jokerLetter.length(), jokerLetter.length() + point.length(), 0);

                                            targetCell.setText(spannable);
                                            targetCell.setTextColor(getResources().getColor(R.color.black));
                                            targetCell.setBackgroundResource(R.drawable.letter_background);

                                            draggedView.setVisibility(View.GONE);
                                            occupiedCells.add(coord);


                                            mapLetters.add(new PlacedLetter(row, col, newLetter, intPoint));
                                            placedLetters.add(new PlacedLetter(row, col, newLetter, intPoint));

                                        });
                                    } else {

                                        SpannableString spannable = new SpannableString(letter + point);
                                        spannable.setSpan(new RelativeSizeSpan(0.5f), letter.length(), letter.length() + point.length(), 0);
                                        spannable.setSpan(new SuperscriptSpan(), letter.length(), letter.length() + point.length(), 0);

                                        targetCell.setText(spannable);
                                        targetCell.setTextColor(getResources().getColor(R.color.black));
                                        targetCell.setBackgroundResource(R.drawable.letter_background);

                                        draggedView.setVisibility(View.GONE);
                                        occupiedCells.add(coord);
                                        mapLetters.add(new PlacedLetter(row, col, letter, intPoint));
                                        placedLetters.add(new PlacedLetter(row, col, letter, intPoint));
                                    }

                                    for (int i = 0; i < letters.size(); i++) {
                                        Letters l = letters.get(i);
                                        if (l.getLetter().equals(letter) && l.getLetterInfo().getPoint() == intPoint) {
                                            letters.remove(i);
                                            break;
                                        }
                                    }


                                    //mapLetters.add(new PlacedLetter(row, col, letter, intPoint));
                                    //placedLetters.add(new PlacedLetter(row, col, letter, intPoint));
                                    return true;
                                }


                                 if (isTouchedMoveLetter) {
                                    View draggedView = (View) event.getLocalState();
                                    TextView sourceCell = (TextView) draggedView;
                                    TextView targetCell = (TextView) v;

                                    if (sourceCell == targetCell) return false;


                                    String[] sourceCoord = ((String) sourceCell.getTag()).split(",");
                                    int sourceRow = Integer.parseInt(sourceCoord[0]);
                                    int sourceCol = Integer.parseInt(sourceCoord[1]);

                                    String[] targetCoord = ((String) targetCell.getTag()).split(",");
                                    int targetRow = Integer.parseInt(targetCoord[0]);
                                    int targetCol = Integer.parseInt(targetCoord[1]);

                                     int rowDiff = Math.abs(targetRow - sourceRow);
                                     int colDiff = Math.abs(targetCol - sourceCol);

                                     for(PlacedLetter l : mapLetters){
                                         if(l.getRow()==targetRow && l.getCol()==targetCol){
                                             Toast.makeText(GameActivity.this, "Taşımak istediğiniz harf  bir harfin üstüne koyulamaz!", Toast.LENGTH_SHORT).show();
                                             isMovedLetter = false;
                                             return false;
                                         }
                                     }

                                     if (rowDiff > 1 || colDiff > 1) {
                                         Toast.makeText(GameActivity.this, "Sadece 1 birim taşıyabilrsiniz!", Toast.LENGTH_SHORT).show();
                                         isMovedLetter = false;
                                         return false;
                                     }

                                         boolean check = false;

                                         int[][] directions = {
                                                 {-1, 0}, {1, 0}, {0, -1}, {0, 1},
                                                 {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
                                         };

                                         for (int[] dir : directions) {
                                             int nr = targetRow + dir[0];
                                             int nc = targetCol + dir[1];

                                             for (PlacedLetter l : mapLetters) {
                                                 if(l.getRow()==sourceRow && l.getCol()==sourceCol) {

                                                 }
                                                 else if (l.getRow() == nr && l.getCol() == nc) {
                                                     check = true;
                                                     break;

                                                 }
                                             }
                                             if (check) break;
                                         }

                                         if (!check) {
                                             Toast.makeText(GameActivity.this, "Taşıdığınız yer mevcut harflerle bitişik olmalı!", Toast.LENGTH_SHORT).show();
                                             isMovedLetter = false;
                                             return false;
                                         }


                                     CharSequence text = sourceCell.getText();
                                     sourceCell.setText("");
                                     targetCell.setText(text);


                                     sourceCell.setBackgroundResource(R.drawable.grid_cell);
                                     targetCell.setTextColor(getResources().getColor(R.color.black));
                                     targetCell.setBackgroundResource(R.drawable.letter_background);


                                    for (PlacedLetter pl : mapLetters) {
                                        if (pl.getRow() == sourceRow && pl.getCol() == sourceCol) {
                                            pl.setRow(targetRow);
                                            pl.setCol(targetCol);
                                            placedLetters.add(new PlacedLetter(targetRow, targetCol, pl.getLetter(), pl.getPoint()));
                                            break;
                                        }
                                    }



                                    occupiedCells.remove(sourceRow + "," + sourceCol);
                                    occupiedCells.add(targetRow + "," + targetCol);
                                    isMovedLetter = true;

                                    return true;
                                }

                                return false;

                                    case DragEvent.ACTION_DRAG_ENDED:

                                        //v.setBackgroundResource(R.drawable.grid_cell);
                                        return true;

                                    default:
                                        return false;

                        }
                    }
                });

                GridLayout.LayoutParams param = new GridLayout.LayoutParams();
                param.rowSpec = GridLayout.spec(row);
                param.columnSpec = GridLayout.spec(col);
                param.width = cellSize;
                param.height = cellSize;
                cell.setLayoutParams(param);

                board.addView(cell);


            }

        }
    }
    private void showLetterPickerDialog(Consumer<String> onLetterSelected) {
        final String[] alphabet = "ABCÇDEFGĞHIİJKLMNOÖPRSTUÜVYZ".split("");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Joker Harfi Seç");

        builder.setItems(alphabet, (dialog, which) -> {
            String selectedLetter = alphabet[which];
            onLetterSelected.accept(selectedLetter);
        });

        builder.setCancelable(false);
        builder.show();
    }

    private void createBonusMap () {

        bonusStartCells.put("0,5", "H2");
        bonusStartCells.put("0,9", "H2");
        bonusStartCells.put("1,6", "H2");
        bonusStartCells.put("1,8", "H2");
        bonusStartCells.put("2,7", "K2");

        bonusStartCells.put("5,0", "H2");
        bonusStartCells.put("9,0", "H2");
        bonusStartCells.put("6,1", "H2");
        bonusStartCells.put("8,1", "H2");
        bonusStartCells.put("7,2", "K2");

        bonusStartCells.put("5,14", "H2");
        bonusStartCells.put("9,14", "H2");
        bonusStartCells.put("6,13", "H2");
        bonusStartCells.put("8,13", "H2");
        bonusStartCells.put("7,12", "K2");

        bonusStartCells.put("14,5", "H2");
        bonusStartCells.put("14,9", "H2");
        bonusStartCells.put("13,6", "H2");
        bonusStartCells.put("13,8", "H2");
        bonusStartCells.put("12,7", "K2");

        bonusStartCells.put("0,2", "K3");
        bonusStartCells.put("1,1", "H3");
        bonusStartCells.put("2,0", "K3");

        bonusStartCells.put("0,12", "K3");
        bonusStartCells.put("1,13", "H3");
        bonusStartCells.put("2,14", "K3");

        bonusStartCells.put("12,0", "K3");
        bonusStartCells.put("13,1", "H3");
        bonusStartCells.put("14,2", "K3");

        bonusStartCells.put("12,14", "K3");
        bonusStartCells.put("13,13", "H3");
        bonusStartCells.put("14,12", "K3");

        bonusStartCells.put("3,3", "K2");
        bonusStartCells.put("4,4", "H3");
        bonusStartCells.put("5,5", "H2");
        bonusStartCells.put("6,6", "H2");

        bonusStartCells.put("3,11", "K2");
        bonusStartCells.put("4,10", "H3");
        bonusStartCells.put("5,9", "H2");
        bonusStartCells.put("6,8", "H2");

        bonusStartCells.put("11,3", "K2");
        bonusStartCells.put("10,4", "H3");
        bonusStartCells.put("9,5", "H2");
        bonusStartCells.put("8,6", "H2");

        bonusStartCells.put("11,11", "K2");
        bonusStartCells.put("10,10", "H3");
        bonusStartCells.put("9,9", "H2");
        bonusStartCells.put("8,8", "H2");

        bonusStartCells.put("7,7", "★");

        }

       private void getGameData(long gameId){
            runnable = new Runnable() {
               @Override
               public void run() {
                   fetchGameData(gameId);
                   handler.postDelayed(this, interval);
               }
           };

           handler.post(runnable);
       }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }
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
                    updateGameInfo(game);
                }
            }

            @Override
            public void onFailure(Call<Game> call, Throwable t) {
                Log.e("GameActivity", "Hata oluştu: " + t.getMessage());
            }
        });
    }



    private void updateGameInfo(Game game) {

        if(gameState.getGameState().equals("ended")){

            Toast.makeText(GameActivity.this, "Oyun bitti!", Toast.LENGTH_SHORT).show();


            Intent intentFinished = new Intent(GameActivity.this, ActivityEndedGame.class);
            intentFinished.putExtra("gameId", gameId);
            startActivity(intentFinished);
            finish();


        }

        if(gameState.getPlayer1().getUsername().equals(user.getUsername())){

            if( gameState.isUpdateGamePlayer1()) {
                gameState.setUpdateGamePlayer1(false);
                updateGame(gameState);
                resetBoard();
            }
        }else{

            if( gameState.isUpdateGamePlayer2()) {
                gameState.setUpdateGamePlayer2(false);
                updateGame(gameState);
                resetBoard();
            }
        }

        turnInfo.setText("Sıra: "+game.getCurrentPlayer().getUsername());
        remainingTimeInfo.setText("Süre :"+String.valueOf(game.getMoveTimeLeft()));

        if(game.getPlayer1().getUsername().equals(user.getUsername()))
        {
            playerInfo.setText(game.getPlayer1().getUsername()+ " : " + game.getPlayer1Point());
            opponentInfo.setText(game.getPlayer2().getUsername()+" : " + game.getPlayer2Point());
            remainingLetters.setText(String.valueOf(game.getRemainingLetters()));

            if(updatePlayerLetters) {
                String jsonLetters = game.getPlayer1LettersJson();
                Type listType = new TypeToken<List<Letters>>() {
                }.getType();
                letters = gson.fromJson(jsonLetters, listType);
                showLetters();
                updatePlayerLetters =false;
            }

        }else{
            playerInfo.setText(game.getPlayer2().getUsername()+" : " + game.getPlayer2Point());
            opponentInfo.setText(game.getPlayer1().getUsername()+ " : " + game.getPlayer1Point());
            remainingLetters.setText(String.valueOf(game.getRemainingLetters()));

            if(updatePlayerLetters) {
            String jsonLetters=game.getPlayer2LettersJson();
            Type listType = new TypeToken<List<Letters>>() {}.getType();
            letters=gson.fromJson(jsonLetters, listType);
            showLetters();
            updatePlayerLetters =false;
            }
        }

        if(updateMap) {
            Type listType = new TypeToken<List<PlacedLetter>>() {}.getType();
            mapLetters =gson.fromJson(gameState.getPlacedLettersJson(),listType);
            if(mapLetters != null && !mapLetters.isEmpty()) {
                updateGameMap();
            }
            if (mapLetters == null) {
                mapLetters = new ArrayList<>();
            }

            listType = new TypeToken<List<Mine>>() {}.getType();
            mines=gson.fromJson(gameState.getMineJson(),listType);
            listType = new TypeToken<List<Reward>>() {}.getType();
            rewards=gson.fromJson(gameState.getRewardJson(),listType);

            updateMap=false;
        }
    }

    private void showLetters() {
        lettersLayout.removeAllViews();
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int letterSize = width / 9;

        //Donmuş harf indexleri
        Set<Integer> frozenIndexSet = new HashSet<>();
        if (gameState.getFrozenLetterPlayer() != null &&
                gameState.getFrozenLetterPlayer().equals(user.getUsername()) &&
                gameState.getFrozenLetterIndexes() != null) {

            String indexesStr = gameState.getFrozenLetterIndexes();
            if (!indexesStr.isEmpty()) {
                for (String idx : indexesStr.split(",")) {
                    try {
                        frozenIndexSet.add(Integer.parseInt(idx.trim()));
                    } catch (NumberFormatException ignored) {}
                }
            }
        }

        for (int i = 0; i < letters.size(); i++) {
            Letters letter = letters.get(i);

            LinearLayout letterContainer = new LinearLayout(this);
            letterContainer.setOrientation(LinearLayout.VERTICAL);
            letterContainer.setGravity(Gravity.CENTER);
            letterContainer.setBackgroundResource(R.drawable.letter_background);
            letterContainer.setPadding(8, 8, 8, 8);

            TextView letterText = new TextView(this);
            letterText.setText(letter.getLetter());

            if (letter.getLetter().equals("JOKER")) {
                letterText.setTextSize(12);
            } else {
                letterText.setTextSize(20);
            }
            letterText.setGravity(Gravity.CENTER);
            letterText.setTextColor(getResources().getColor(R.color.black));

            TextView pointText = new TextView(this);
            pointText.setText(String.valueOf(letter.getLetterInfo().getPoint()));
            pointText.setTextSize(10);
            pointText.setGravity(Gravity.END);
            pointText.setTextColor(getResources().getColor(R.color.black));

            letterContainer.addView(letterText);
            letterContainer.addView(pointText);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    letterSize,
                    letterSize
            );
            params.setMargins(8, 8, 8, 8);
            letterContainer.setLayoutParams(params);

            if (frozenIndexSet.contains(i)) {
                letterContainer.setAlpha(0.5f);
                letterText.setTextColor(Color.GRAY);
                pointText.setTextColor(Color.GRAY);
                letterContainer.setOnLongClickListener(null);
            } else {
                letterContainer.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if(!gameState.getCurrentPlayer().getUsername().equals(user.getUsername())){
                            Toast.makeText(GameActivity.this, "Tur karşı rakipte!", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        if (!isTouchedMoveLetter) {
                            isTouchedAddLetter = true;
                            isAddedLetter = true;
                            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                            v.startDragAndDrop(null, shadowBuilder, v, 0);
                            return true;
                        }
                        return false;
                    }
                });
            }

            lettersLayout.addView(letterContainer);
        }
    }


    private void updateGameMap(){

        for (PlacedLetter placed : mapLetters) {
            int row = placed.getRow();
            int col = placed.getCol();
            String letter = placed.getLetter();
            int point = placed.getPoint();

            SpannableString spannable = new SpannableString(letter + point);
            spannable.setSpan(new RelativeSizeSpan(0.5f), letter.length(), letter.length() + String.valueOf(point).length(), 0);
            spannable.setSpan(new SuperscriptSpan(), letter.length(), letter.length() + String.valueOf(point).length(), 0);

            int index = row * 15 + col;
            TextView cell = (TextView) board.getChildAt(index);
            cell.setText(spannable);
            cell.setTextColor(getResources().getColor(R.color.black));
            cell.setBackgroundResource(R.drawable.letter_background);


        }
    }

    private void resetBoard() {
        occupiedCells.clear();
        //letters.clear();
        mapLetters.clear();
        placedLetters.clear();
        board.removeAllViews();
        isAddedLetter=false;
        isMovedLetter=false;
        isTouchedAddLetter=false;
        isTouchedMoveLetter=false;
        createMap();
        updatePlayerLetters = true;
        updateMap=true;
        fetchGameData(gameId);
    }



    private void findwords(){
        wordCheck=true;
        //placedLetters.clear();
        formedWords.clear();
        puanTransferiMiktarı=0;

        List<String> coords = new ArrayList<>(occupiedCells);

       /*
        for (String coord : coords) {
            String[] parts = coord.split(",");
            int row = Integer.parseInt(parts[0]);
            int col = Integer.parseInt(parts[1]);

            int index = row * 15 + col;
            TextView cell = (TextView) board.getChildAt(index);
            CharSequence text = cell.getText();
            if (text != null && text.length() > 0) {
                String letter = text.toString().substring(0, 1);
                int point=Integer.parseInt(text.toString().substring(1));
                Log.d("harf", "Oluşan harf: " + letter+"puanı"+point);
                placedLetters.add(new PlacedLetter(row, col, letter,point));
            }
        }
        */
        for (PlacedLetter pl : placedLetters) {
            WordResult horizontalWord = getWord(pl.getRow(), pl.getCol(), true);
            WordResult verticalWord = getWord(pl.getRow(), pl.getCol(), false);

            if (horizontalWord.getWord().length() > 1) formedWords.add(horizontalWord);
            if (verticalWord.getWord().length() > 1) formedWords.add(verticalWord);

            WordResult diagonalDownWord = getDiagonalWord(pl.getRow(), pl.getCol());
            if (diagonalDownWord.getWord().length() > 1) formedWords.add(diagonalDownWord);
        }
        if(formedWords.isEmpty())
            wordCheck=false;

        for (WordResult word : formedWords) {
            String kelime = word.getWord();
            int puan = word.getPoint();
            int color;

            if (WordValidator.isValidWord(kelime.toLowerCase())) {
                Log.d("Kelime", "GEÇERLİ: " + kelime + " (" + puan + " puan)");
                color= R.drawable.green;
            } else {
                Log.d("Kelime", "GEÇERSİZ: " + kelime+" puan :" +puan);
                color=R.drawable.red;
                wordCheck=false;
            }
            for (PlacedLetter pl : word.getLetters()) {
                int index = pl.getRow() * 15 + pl.getCol();
                TextView cell = (TextView) board.getChildAt(index);
                cell.setBackgroundResource(color);
            }
        }
        Log.d("Kelime", "Durum : " +wordCheck );



    }
    private WordResult getWord(int row, int col, boolean isHorizontal) {
        StringBuilder word = new StringBuilder();
        List<Mine> triggeredMines = new ArrayList<>();
        double wordMul=1;
        int wordPoint=0;
        List<PlacedLetter> usedLetters = new ArrayList<>();

        puanTransferi=false;
        boolean bonusAvailable=true;
        boolean isZero=false;
        int BonusPoint=0;


        int startRow = row;
        int startCol = col;

        while (startRow >= 0 && startCol >= 0) {
            int finalRow = startRow;
            int finalCol = startCol;
            boolean exists = mapLetters.stream().anyMatch(l ->
                    l.getRow() == finalRow && l.getCol() == finalCol
            );
            if (!exists) break;

            if (isHorizontal) startCol--;
            else startRow--;
        }

        if (isHorizontal) startCol++; else startRow++;

        while (startRow < 15 && startCol < 15) {

            int finalRow = startRow;
            int finalCol = startCol;
            Optional<PlacedLetter> matchedLetter = mapLetters.stream()
                    .filter(l -> l.getRow() == finalRow && l.getCol() == finalCol)
                    .findFirst();

            if (!matchedLetter.isPresent()) break;

            usedLetters.add(matchedLetter.get());

            int letterPoint=matchedLetter.get().getPoint();

            word.append(matchedLetter.get().getLetter());

            String key = finalRow + "," + finalCol;



            Iterator<Mine> iterator = mines.iterator();
            while (iterator.hasNext()) {
                Mine mine = iterator.next();
                if (mine.getRow() == finalRow && mine.getCol() == finalCol) {
                    switch (mine.getType()) {
                        case "Puan Bölüm":
                            wordMul *= 0.3;
                            Toast.makeText(GameActivity.this, "Mayın: Puan bölündü %30 alındı!", Toast.LENGTH_SHORT).show();
                            break;
                        case "Puan Transfer":
                            puanTransferi = true;
                            Toast.makeText(GameActivity.this, "Mayın: Puan Transferi Rakibe geçti!", Toast.LENGTH_SHORT).show();
                            break;
                        case "Harf Kaybı":
                            harfHaybı = true;
                            Toast.makeText(GameActivity.this, "Mayın: Var olan harfler rastgele dağıtıldı!", Toast.LENGTH_SHORT).show();
                            break;
                        case "Ekstra Hamle Engel":
                            bonusAvailable = false;
                            wordMul = 1;
                            wordPoint = wordPoint - BonusPoint;
                            Toast.makeText(GameActivity.this, "Mayın: Kelimenin bonusları kapatıldı!", Toast.LENGTH_SHORT).show();
                            break;
                        case "Kelime İptal":
                            wordMul = 0;
                            Toast.makeText(GameActivity.this, "Mayın: Kelimeden 0 puan alındı!", Toast.LENGTH_SHORT).show();
                            break;
                    }
                    iterator.remove();
                }
            }


            if (bonusAvailable) {
                if (bonusStartCells.containsKey(key)) {
                    String bonus = bonusStartCells.get(key);
                    //cell.setText(bonus);
                    switch (bonus) {
                        case "H2":
                            letterPoint = letterPoint * 2;
                            BonusPoint += letterPoint;
                            break;
                        case "H3":
                            letterPoint = letterPoint * 3;
                            BonusPoint += letterPoint*2;
                            break;
                        case "K2":
                            wordMul = wordMul * 2;
                            break;
                        case "K3":
                            wordMul = wordMul * 3;
                            break;
                        default:

                    }
                }
            }




                wordPoint += letterPoint;


            if (isHorizontal) startCol++;
            else startRow++;
        }



        wordPoint= (int) (wordPoint*wordMul);

        if(isZero) wordPoint=0;

        if (puanTransferi) {
            puanTransferiMiktarı += wordPoint;
            wordPoint=0;
        }

        return new WordResult(word.toString(),wordPoint,usedLetters);
    }
    private WordResult getDiagonalWord(int row, int col) {
        StringBuilder word = new StringBuilder();
        double wordMul = 1;
        int wordPoint = 0;
        int BonusPoint = 0;
        List<PlacedLetter> usedLetters = new ArrayList<>();

        boolean bonusAvailable = true;
        boolean isZero=false;
        puanTransferi = false;


        int startRow = row;
        int startCol = col;

        while (startRow > 0 && startCol > 0) {
            int r = startRow - 1;
            int c = startCol - 1;
            boolean exists = placedLetters.stream().anyMatch(l -> l.getRow() == r && l.getCol() == c);
            if (!exists) break;
            startRow = r;
            startCol = c;
        }

        while (startRow < 15 && startCol < 15) {
            int finalRow = startRow;
            int finalCol = startCol;
            Optional<PlacedLetter> matchedLetter = placedLetters.stream()
                    .filter(l -> l.getRow() == finalRow && l.getCol() == finalCol)
                    .findFirst();

            if (!matchedLetter.isPresent()) break;

            usedLetters.add(matchedLetter.get());

            int letterPoint = matchedLetter.get().getPoint();

            word.append(matchedLetter.get().getLetter());

            String key = finalRow + "," + finalCol;

            Iterator<Mine> iterator = mines.iterator();
            while (iterator.hasNext()) {
                Mine mine = iterator.next();
                if (mine.getRow() == finalRow && mine.getCol() == finalCol) {
                    switch (mine.getType()) {
                        case "Puan Bölüm":
                            wordMul *= 0.3;
                            Toast.makeText(GameActivity.this, "Mayın: Puan bölündü %30 alındı!", Toast.LENGTH_SHORT).show();
                            break;
                        case "Puan Transfer":
                            puanTransferi = true;
                            Toast.makeText(GameActivity.this, "Mayın: Puan Transferi Rakibe geçti!", Toast.LENGTH_SHORT).show();
                            break;
                        case "Harf Kaybı":
                            harfHaybı = true;
                            Toast.makeText(GameActivity.this, "Mayın: Var olan harfler rastgele dağıtıldı!", Toast.LENGTH_SHORT).show();
                            break;
                        case "Ekstra Hamle Engel":
                            bonusAvailable = false;
                            wordMul = 1;
                            wordPoint = wordPoint - BonusPoint;
                            Toast.makeText(GameActivity.this, "Mayın: Kelimenin bonusları kapatıldı!", Toast.LENGTH_SHORT).show();
                            break;
                        case "Kelime İptal":
                            isZero=true;
                            Toast.makeText(GameActivity.this, "Mayın: Kelimeden 0 puan alındı!", Toast.LENGTH_SHORT).show();
                            break;
                    }
                    iterator.remove();
                }
            }

            if (bonusAvailable) {
                if (bonusStartCells.containsKey(key)) {
                    String bonus = bonusStartCells.get(key);
                    switch (bonus) {
                        case "H2":
                            letterPoint *= 2;
                            BonusPoint += letterPoint;
                            break;
                        case "H3":
                            letterPoint *= 3;
                            BonusPoint += letterPoint * 2;
                            break;
                        case "K2":
                            wordMul *= 2;
                            break;
                        case "K3":
                            wordMul *= 3;
                            break;
                    }
                }
            }


            wordPoint += letterPoint;


            startCol++;
            startRow++;
        }

        wordPoint = (int) (wordPoint * wordMul);

        if(isZero) wordPoint=0;

        if (puanTransferi) {
            puanTransferiMiktarı += wordPoint;
            wordPoint=0;
        }

        return new WordResult(word.toString(), wordPoint, usedLetters);
    }

    private Game hangleRewards(Game game,int player){
        
        Iterator<Reward> iterator = rewards.iterator();
        while (iterator.hasNext()) {
            Reward reward = iterator.next();

            for (PlacedLetter letter : placedLetters) {
                if (reward.getRow() == letter.getRow() && reward.getCol() == letter.getCol()) {


                    switch (reward.getType()) {
                        case "Bolge Yasak":

                            showRewardDialog("Bolge Yasak", () -> {

                                runOnUiThread(() -> {

                                    if (player == 1) {
                                        game.setBannedAreaPlayer(game.getPlayer2().getUsername());
                                        Random random=new Random();
                                        int r = random.nextInt(2);
                                        if(r==1)
                                        game.setBannedArea("RIGHT");
                                        else
                                            game.setBannedArea("LEFT");
                                    } else {
                                        game.setBannedAreaPlayer(game.getPlayer1().getUsername());
                                        Random random=new Random();
                                        int r = random.nextInt(2);
                                        if(r==1)
                                            game.setBannedArea("RIGHT");
                                        else
                                            game.setBannedArea("LEFT");
                                    }
                                    game.setRewardJson(gson.toJson(rewards));
                                    updateGame(game);
                                });
                            }, () -> {
                                runOnUiThread(() -> {
                                });
                            });
                            break;

                        case "Harf Yasak":

                            showRewardDialog("Harf Yasak", () -> {

                                runOnUiThread(() -> {

                                    if (player == 1) {
                                        game.setFrozenLetterPlayer(game.getPlayer2().getUsername());
                                        handleFrozenLetters(game,1);


                                    } else {
                                        game.setFrozenLetterPlayer(game.getPlayer1().getUsername());
                                        handleFrozenLetters(game,2);
                                    }

                                    game.setRewardJson(gson.toJson(rewards));
                                    updateGame(game);

                                });
                            }, () -> {
                                runOnUiThread(() -> {

                                });
                            });
                            break;

                        case "Ekstra Hamle":

                            showRewardDialog("Ekstra Hamle", () -> {

                                runOnUiThread(() -> {

                                    if (player == 1) {
                                        game.setCurrentPlayer(gameState.getPlayer1());

                                    } else {
                                        game.setCurrentPlayer(gameState.getPlayer2());
                                    }

                                    game.setRewardJson(gson.toJson(rewards));
                                    updateGame(game);

                                });
                            }, () -> {
                                runOnUiThread(() -> {
                                });
                            });
                            break;

                    }
                    iterator.remove();


                    break;
                }
            }
        }
        return game;
    }

    private void handleFrozenLetters(Game game,int player){

        String opponentLettersJson;
        if(player==1) {
            if (game.getPlayer2LettersJson() == null) return;
            opponentLettersJson = game.getPlayer2LettersJson();
        }
        else {
            if (game.getPlayer1LettersJson() == null) return;
            opponentLettersJson = game.getPlayer1LettersJson();
        }
        Type listType = new TypeToken<List<Letters>>() {}.getType();
        letters=gson.fromJson(opponentLettersJson, listType);


        int freezeCount = Math.min(2, letters.size());


        List<Integer> indexes = new ArrayList<>();
        for (int i = 0; i < letters.size(); i++) {
            indexes.add(i);
        }
        Collections.shuffle(indexes);

        List<Integer> frozenIndexes = indexes.subList(0, freezeCount);

        String frozenIndexStr = frozenIndexes.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));


        game.setFrozenLetterIndexes(frozenIndexStr);

    }


    private void showRewardDialog(String rewardType, Runnable onYes, Runnable onNo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ödül Kazandınız!");

        switch (rewardType) {
            case "Bolge Yasak":
                builder.setMessage("Bölge Yasağı ödülünü kullanmak ister misiniz?");
                break;
            case "Harf Yasak":
                builder.setMessage("Harf Yasağı ödülünü kullanmak ister misiniz?");
                break;
            case "Ekstra Hamle":
                builder.setMessage("Ekstra Hamle ödülünü kullanmak ister misiniz?");
                break;
        }
        builder.setPositiveButton("Evet", (dialog, which) -> {
            if (onYes != null) onYes.run();
        });
        builder.setNegativeButton("Hayır", (dialog, which) -> {
            if (onNo != null) onNo.run();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private Game handleShuffleLetters(Game game, int player) {

        int count=0;
        Map<String, LetterInfo> letterBag = gson.fromJson(
                game.getLetterBagJson(),
                new com.google.gson.reflect.TypeToken<Map<String, LetterInfo>>() {}.getType()
        );

        List<Letters> playerLetters = new ArrayList<>();

        if(player==1){

            if (game.getPlayer1LettersJson() != null) {
                playerLetters = gson.fromJson(
                        game.getPlayer1LettersJson(),
                        new com.google.gson.reflect.TypeToken<List<Letters>>() {}.getType()
                );
            }
        }else{
            if (game.getPlayer2LettersJson() != null) {
                playerLetters = gson.fromJson(
                        game.getPlayer2LettersJson(),
                        new com.google.gson.reflect.TypeToken<List<Letters>>() {}.getType()
                );
            }

        }
        for (Letters letter : playerLetters) {
            count++;
            LetterInfo letterInfo = letterBag.get(letter.getLetter());
            if (letterInfo != null) {

                letterInfo.setCount(letterInfo.getCount() + 1);
                letterBag.put(letter.getLetter(), letterInfo);
            } else {

                if(letter.getLetterInfo().getPoint()==0){
                    letter.setLetter("JOKER");
                }

                letterBag.put(letter.getLetter(), new LetterInfo(1, letter.getLetterInfo().getPoint()));
            }
        }

        int totalLetterCount = 0;

        for (LetterInfo letterInfo : letterBag.values()) {
            totalLetterCount += letterInfo.getCount();
        }

        int numberOfLettersToDraw = Math.min(totalLetterCount, 7);

        List<Letters> newPlayerLetters = drawLettersForPlayer(game, numberOfLettersToDraw, letterBag);

        if(player==1){
            game.setPlayer1LettersJson(gson.toJson(newPlayerLetters));
        }
        else {
            game.setPlayer2LettersJson(gson.toJson(newPlayerLetters));
        }

        game.setLetterBagJson(gson.toJson(letterBag));
        game.setRemainingLetters(game.getRemainingLetters()-numberOfLettersToDraw+count);

        return game;
    }


    private List<Letters> drawLettersForPlayer(Game game, int count, Map<String, LetterInfo> letterBag) {
        List<Letters> drawnLetters = new ArrayList<>();
        Random random = new Random();
        List<String> availableLetters = new ArrayList<>(letterBag.keySet());

        for (int i = 0; i < count; i++) {
            if (availableLetters.isEmpty()) break;

            String selectedLetter = availableLetters.get(random.nextInt(availableLetters.size()));
            LetterInfo selectedLetterInfo = letterBag.get(selectedLetter);

            Letters letter = new Letters(selectedLetter, selectedLetterInfo);
            drawnLetters.add(letter);

            if (selectedLetterInfo.getCount() > 1) {
                selectedLetterInfo.setCount(selectedLetterInfo.getCount() - 1);
                letterBag.put(selectedLetter, selectedLetterInfo);
            } else {
                letterBag.remove(selectedLetter);
                availableLetters.remove(selectedLetter);
            }
        }

        return drawnLetters;
    }





}
