package com.kelimeOyun.Sunucu.controller;

import com.google.gson.Gson;
import com.kelimeOyun.Sunucu.model.CreateGame;
import com.kelimeOyun.Sunucu.model.Game;
import com.kelimeOyun.Sunucu.model.User; 
import com.kelimeOyun.Sunucu.service.ServerService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

    @RestController
    @RequestMapping("/api")
    public class Server {
        @Autowired
        private ServerService serverService;
        int a=0;
        

        @PostMapping("/login")
        public ResponseEntity<Map<String, Object>> login(@RequestBody User user) {
            Map<String, Object> response = new HashMap<>();
            User loggedInUser = serverService.loginUser(user);
            if (loggedInUser!=null) {
                response.put("status", "success");
                response.put("message", "Giriş başarılı");
                response.put("username", loggedInUser.getUsername());
                response.put("totalGames", loggedInUser.getTotalGames());
                response.put("wonGames", loggedInUser.getWonGames());
                Gson gson = new Gson();
                String userJson = gson.toJson(loggedInUser);
                response.put("userjson", userJson);
                
                System.out.println(userJson);
                

                return ResponseEntity.ok(response);  
            } else {
                response.put("status", "fail");
                response.put("message", "Giriş başarısız");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        }

        @PostMapping("/register")
        public ResponseEntity<Map<String, Object>> register(@RequestBody User user) {
            Map<String, Object> response = new HashMap<>();
            User registeredUser=serverService.register(user);
            if (registeredUser!=null) {
                response.put("status", "success");
                response.put("message", "Kayıt başarılı");
                response.put("username", registeredUser.getUsername());
                response.put("totalGames", registeredUser.getTotalGames());
                response.put("wonGames", registeredUser.getWonGames());
                Gson gson = new Gson();
                String userJson = gson.toJson(registeredUser);
                response.put("user", userJson);
                
                
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "fail");
                response.put("message", "Kayıt başarısız.Kullanıcı adı mevcut.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        }
        
        @PostMapping("/creategame")
        public ResponseEntity<Map<String, Object>> createGame(@RequestBody CreateGame createGame) {
            Game game = serverService.handleCreateGame(createGame);
            Map<String, Object> response = new HashMap<>();
            if (game == null) {
               response.put("message", "Oyun isteği kaydedildi, oyuncu bekleniyor.");
               return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);

            } else {
              response.put("message", "Eşleşme bulundu.");
              response.put("game", game);
              return ResponseEntity.ok(response);
            }
         }        
        
        @PostMapping("/getactivegames")
        public ResponseEntity<List<Game>> getUsersGame(@RequestBody User user){
            
            //aktif olan oyunları al şuanlık tüm oyunları alıyor
            
            List<Game> games=serverService.getActiveGamesOfUser(user);
            
            //yeni oyunlar en başta gösterilsin
            //games.reversed();
            
           return ResponseEntity.status(HttpStatus.ACCEPTED).body(games);
            
        }
        
        @PostMapping("/getgame")
        public ResponseEntity<Game> getGame(@RequestBody long id){
            Game game=serverService.getGame(id);
            
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(game);
        }
        
        @PostMapping("/updategame")
        public ResponseEntity<Game> updateGame(@RequestBody Game game){
            
            Game updatedGame = serverService.updateGame(game);            
            
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(updatedGame);
        }
        
        
         
    }
