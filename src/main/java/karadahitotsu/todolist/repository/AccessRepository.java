package karadahitotsu.todolist.repository;

import karadahitotsu.todolist.entity.Access_token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccessRepository extends JpaRepository<Access_token,Long> {
    List <Access_token> findByToken(String token);
}
