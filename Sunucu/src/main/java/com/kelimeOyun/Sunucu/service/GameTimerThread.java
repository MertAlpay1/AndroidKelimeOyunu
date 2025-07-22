package com.kelimeOyun.Sunucu.service;

import com.kelimeOyun.Sunucu.model.Game;
import com.kelimeOyun.Sunucu.repository.GameRepository;
import jakarta.annotation.PostConstruct;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GameTimerThread implements Runnable {
    
    @Autowired
    private GameRepository gameRepository;

    private volatile boolean running = true;
    
    @PostConstruct
    public void init() {
        Thread thread = new Thread(this);
        thread.setDaemon(true); 
        thread.start();
    }

    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(1000);

                List<Game> activeGames = gameRepository.findByGameState("active");

                for (Game game : activeGames) {
                    int remaining = game.getMoveTimeLeft();

                    if (remaining > 0) {
                        game.setMoveTimeLeft(remaining - 1);
                    }else{
                        game.setGameState("ended");
                        
                        if(game.getCurrentPlayer().getUsername().equals(game.getPlayer1().getUsername())){
                            
                            game.setWinner(game.getPlayer2().getUsername());
                           
                        }else{
                            game.setWinner(game.getPlayer1().getUsername());
                        }
                        
                    }
                    
                    gameRepository.save(game); 
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); 
                
            } catch (Exception e) {
                e.printStackTrace();
                
            }
        }
    }

    public void stop() {
        running = false;
    }
    
}
