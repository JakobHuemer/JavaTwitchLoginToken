package at.jakobhuemer.sociallogintestspring.repository;

import at.jakobhuemer.sociallogintestspring.models.user.AccessScope;
import at.jakobhuemer.sociallogintestspring.models.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

//    Optional<User> findByTwitchLogin( String twitchLogin );

    Optional<User> findDistinctFirstByTwitchId( Long twitchId );

    Optional<User> findDistinctFirstByToken( String token );

}
