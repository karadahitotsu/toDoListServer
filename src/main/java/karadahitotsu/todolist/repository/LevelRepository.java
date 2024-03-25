package karadahitotsu.todolist.repository;

import karadahitotsu.todolist.entity.Level;
import karadahitotsu.todolist.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LevelRepository extends JpaRepository<Level,Long> {
    List<Level>findByUser(Users user);
}
