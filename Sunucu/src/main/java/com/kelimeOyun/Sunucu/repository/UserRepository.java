
package com.kelimeOyun.Sunucu.repository;

import com.kelimeOyun.Sunucu.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
    
    Optional<User> findByUsername(String username);
    

    User findByUsernameAndPassword(String username, String password);
    
}
