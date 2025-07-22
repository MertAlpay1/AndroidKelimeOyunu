
package com.kelimeOyun.Sunucu.service;

import com.google.gson.Gson;
import com.kelimeOyun.Sunucu.model.CreateGame;
import com.kelimeOyun.Sunucu.model.Game;
import com.kelimeOyun.Sunucu.model.LetterInfo;
import com.kelimeOyun.Sunucu.model.Letters;
import com.kelimeOyun.Sunucu.model.Mine;
import com.kelimeOyun.Sunucu.model.Reward;
import com.kelimeOyun.Sunucu.model.User;
import com.kelimeOyun.Sunucu.repository.CreateGameRepository;
import com.kelimeOyun.Sunucu.repository.GameRepository;
import com.kelimeOyun.Sunucu.repository.UserRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServerService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CreateGameRepository createGameRepository;

    @Autowired
    private GameRepository gameRepository;


    
    public User register(User user) {
        Optional<User> existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser.isPresent()) {
            return null; 
        }
        
        user.setTotalGames(0);
        user.setWonGames(0);
        
        userRepository.save(user);
        return user;
    }

    public User loginUser(User user) {
        User dbUser = userRepository.findByUsernameAndPassword(user.getUsername(), user.getPassword());
        return dbUser; 
    }
    
    public Game handleCreateGame(CreateGame newCreateGame) {

        CreateGame existingCreateGame = createGameRepository.findBySelectedDuration(newCreateGame.getSelectedDuration());

        if (existingCreateGame == null) {
            // Yoksa bu oyuncuyu beklemeye al
            createGameRepository.save(newCreateGame);
            return null; 
            
        } else {
            
            if (existingCreateGame.getPlayer().equals(newCreateGame.getPlayer())) {

            return null;
            }

            
            // Varsa Game oluştur
            User player1 = userRepository.findByUsername(existingCreateGame.getPlayer()).orElse(null);
            User player2 = userRepository.findByUsername(newCreateGame.getPlayer()).orElse(null);

            if (player1 == null || player2 == null) {
                return null; 
            }

            Game game = new Game();
            game.setPlayer1(player1);
            game.setPlayer2(player2);
            game.setSelectedDuration(newCreateGame.getSelectedDuration());

            int durationInSeconds = mapDurationToSeconds(newCreateGame.getSelectedDuration());
            game.setDuration(durationInSeconds);
            

            Random random = new Random();
            int randomStartPlayer = random.nextInt(2);
            if (randomStartPlayer == 0) {
            game.setCurrentPlayer(player1);
            }
            else {
            game.setCurrentPlayer(player2);
            }
             
            game.setMoveTimeLeft(3600); 
            
            game.setPlayer1Point(0);
            
            game.setPlayer2Point(0);
            
            game.setGameState("active");
            
            Map<String, LetterInfo> letterBag = new HashMap<>();
            letterBag.put("A", new LetterInfo(12, 1));
            letterBag.put("B", new LetterInfo(2, 3));
            letterBag.put("C", new LetterInfo(2, 4));
            letterBag.put("Ç", new LetterInfo(2, 4));
            letterBag.put("D", new LetterInfo(2, 3));
            letterBag.put("E", new LetterInfo(8, 1));
            letterBag.put("F", new LetterInfo(1, 7));
            letterBag.put("G", new LetterInfo(1, 5));
            letterBag.put("Ğ", new LetterInfo(1, 8));
            letterBag.put("H", new LetterInfo(1, 5));
            letterBag.put("I", new LetterInfo(4, 2));
            letterBag.put("İ", new LetterInfo(7, 1));
            letterBag.put("J", new LetterInfo(1, 10));
            letterBag.put("K", new LetterInfo(7, 1));
            letterBag.put("L", new LetterInfo(7, 1));
            letterBag.put("M", new LetterInfo(4, 2));
            letterBag.put("N", new LetterInfo(5, 1));
            letterBag.put("O", new LetterInfo(3, 2));
            letterBag.put("Ö", new LetterInfo(1, 7));
            letterBag.put("P", new LetterInfo(1, 5));
            letterBag.put("R", new LetterInfo(6, 1));
            letterBag.put("S", new LetterInfo(3, 2));
            letterBag.put("Ş", new LetterInfo(2, 4));
            letterBag.put("T", new LetterInfo(5, 1));
            letterBag.put("U", new LetterInfo(3, 2));
            letterBag.put("Ü", new LetterInfo(2, 3));
            letterBag.put("V", new LetterInfo(1, 7));
            letterBag.put("Y", new LetterInfo(2, 3));
            letterBag.put("Z", new LetterInfo(2, 10));
            letterBag.put("JOKER", new LetterInfo(2, 0));

            Gson gson = new Gson();
            String letterBagJson = gson.toJson(letterBag);
            
            game.setLetterBagJson(letterBagJson);
            game.setRemainingLetters(100);
            
            List<Mine> mines=new ArrayList<>();
            Set<String> occupiedPositions = new HashSet<>();
            mines=handleMines(mines,occupiedPositions);
            List<Reward> rewards=new ArrayList<>();
            rewards=handleRewards(rewards,occupiedPositions);
            
            String minesJson=gson.toJson(mines);
            String rewardsJson=gson.toJson(rewards);
            
            game.setMineJson(minesJson);
            game.setRewardJson(rewardsJson);

            gameRepository.save(game);
            
            drawLettersForPlayer1(game, 7);
            drawLettersForPlayer2(game, 7);
            
            createGameRepository.delete(existingCreateGame);

            return game;
        }
    }
    
    private List<Mine> handleMines(List<Mine> mines,Set<String> occupiedPositions){
        
        placeRandomMines(mines, occupiedPositions, 5, "Puan Bölüm");
        placeRandomMines(mines, occupiedPositions, 4, "Puan Transfer");
        placeRandomMines(mines, occupiedPositions, 3, "Harf Kaybı");
        placeRandomMines(mines, occupiedPositions, 2, "Ekstra Hamle Engel");
        placeRandomMines(mines, occupiedPositions, 2, "Kelime İptal");
        
        return mines;
    }
    
    private void placeRandomMines(List<Mine> mines, Set<String> occupiedPositions, int count, String type) {
        Random random = new Random();

          while (count > 0) {
             int row = random.nextInt(15);
             int col = random.nextInt(15);
           String key = row + "-" + col;

           if (!occupiedPositions.contains(key)) {
               mines.add(new Mine(row, col, type));
               occupiedPositions.add(key);
              count--;
            }
        }
    }
    private List<Reward> handleRewards(List<Reward> rewards,Set<String> occupiedPositions){
        Random random = new Random();
        
        placeRandomRewards(rewards, occupiedPositions, 2, "Bolge Yasak");
        placeRandomRewards(rewards, occupiedPositions, 3, "Harf Yasak");
        placeRandomRewards(rewards, occupiedPositions, 2, "Ekstra Hamle");

        
        return rewards;
    }
    
    private void placeRandomRewards(List<Reward> rewards, Set<String> occupiedPositions, int count, String type){
        Random random = new Random();

        while (count > 0) {
        int row = random.nextInt(15);
        int col = random.nextInt(15);
        String key = row + "-" + col;

        if (!occupiedPositions.contains(key)) {
            rewards.add(new Reward(row, col, type));
            occupiedPositions.add(key);
            count--;
          }
       }
    }
    

    private int mapDurationToSeconds(String selectedDuration) {
        switch (selectedDuration) {
            case "2 Dakika":
                return 120;
            case "5 Dakika":
                return 300;
            case "12 Saat":
                return 43200;
            case "24 Saat":
                return 86400;
            default:
                return 300; 
        }
    }
    private void drawLettersForPlayer1(Game game, int count) {
    Gson gson = new Gson();

    Map<String, LetterInfo> letterBag = gson.fromJson(
        game.getLetterBagJson(),
        new com.google.gson.reflect.TypeToken<Map<String, LetterInfo>>() {}.getType()
    );

    List<Letters> player1Letters = new ArrayList<>();
    if (game.getPlayer1LettersJson() != null) {
        player1Letters = gson.fromJson(
            game.getPlayer1LettersJson(),
            new com.google.gson.reflect.TypeToken<Map<String, Double>>() {}.getType()
        );
    }

    Random random = new Random();
    List<String> availableLetters = new ArrayList<>(letterBag.keySet());

    for (int i = 0; i < count; i++) {
        if (availableLetters.isEmpty()) break;

        String selectedLetter = availableLetters.get(random.nextInt(availableLetters.size()));
        LetterInfo selectedLetterInfo = letterBag.get(selectedLetter);

        Letters letter= new Letters(selectedLetter, selectedLetterInfo);

        player1Letters.add(letter);


        if (selectedLetterInfo.getCount() > 1) {
            selectedLetterInfo.setCount(selectedLetterInfo.getCount() - 1);
            letterBag.put(selectedLetter, selectedLetterInfo);
        } else {
            letterBag.remove(selectedLetter);
            availableLetters.remove(selectedLetter);
        }
    }

    game.setRemainingLetters(game.getRemainingLetters() - count);
    game.setPlayer1LettersJson(gson.toJson(player1Letters));
    game.setLetterBagJson(gson.toJson(letterBag));
    gameRepository.save(game);
    }

    private void drawLettersForPlayer2(Game game, int count) {
    Gson gson = new Gson();

    Map<String, LetterInfo> letterBag = gson.fromJson(
        game.getLetterBagJson(),
        new com.google.gson.reflect.TypeToken<Map<String, LetterInfo>>() {}.getType()
    );

    List<Letters> player2Letters = new ArrayList<>();
    if (game.getPlayer2LettersJson() != null) {
        player2Letters = gson.fromJson(
            game.getPlayer2LettersJson(),
            new com.google.gson.reflect.TypeToken<Map<String, Double>>() {}.getType()
        );
    }

    Random random = new Random();
    List<String> availableLetters = new ArrayList<>(letterBag.keySet());

    for (int i = 0; i < count; i++) {
        if (availableLetters.isEmpty()) break;

        String selectedLetter = availableLetters.get(random.nextInt(availableLetters.size()));
        LetterInfo selectedLetterInfo = letterBag.get(selectedLetter);
        Letters letter= new Letters(selectedLetter, selectedLetterInfo);

        player2Letters.add(letter);

        if (selectedLetterInfo.getCount() > 1) {
            selectedLetterInfo.setCount(selectedLetterInfo.getCount() - 1);
            letterBag.put(selectedLetter, selectedLetterInfo);
        } else {
            letterBag.remove(selectedLetter);
            availableLetters.remove(selectedLetter);
        }
    }

    game.setRemainingLetters(game.getRemainingLetters() - count);
    game.setPlayer2LettersJson(gson.toJson(player2Letters));
    game.setLetterBagJson(gson.toJson(letterBag));
    gameRepository.save(game);
}
 
    
    public List<Game> getActiveGamesOfUser(User user){
        
         return gameRepository.findActiveGamesByUser("active",user);
    }
    
    public Game getGame(long id){
        
       
        return gameRepository.findById(id);
    }
    
    public Game updateGame(Game game){
        
        return gameRepository.save(game);
    }
    
    public List<Game> getEndedGamesOfUser(User user){
        
         return gameRepository.findActiveGamesByUser("ended",user);
    }
   

}