package karadahitotsu.todolist.repository;

import karadahitotsu.todolist.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TokenRepository extends JpaRepository<Token,Long> {
    List<Token> findByUser_idAndToken(Long userId, String token);
}
