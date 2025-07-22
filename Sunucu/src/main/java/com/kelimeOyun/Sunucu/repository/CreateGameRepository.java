
package com.kelimeOyun.Sunucu.repository;

import com.kelimeOyun.Sunucu.model.CreateGame;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CreateGameRepository extends JpaRepository <CreateGame,Long> {
    
    CreateGame findBySelectedDuration(String selectedDuration);
    
}
