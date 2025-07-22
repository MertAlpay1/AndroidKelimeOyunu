package com.kelimeOyun.Sunucu.repository;

import com.kelimeOyun.Sunucu.model.Game;
import com.kelimeOyun.Sunucu.model.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GameRepository extends JpaRepository<Game, Long> {
    
    
    @Query("SELECT g FROM Game g WHERE g.gameState =:state AND (g.player1=:user OR g.player2=:user)")
    
    List<Game> findActiveGamesByUser(@Param("state") String gameState ,@Param("user") User user);
    
    List<Game> findByGameState(String string);
    
    Game findById(long id);

}
