package karadahitotsu.todolist.repository;

import karadahitotsu.todolist.entity.Task;
import karadahitotsu.todolist.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task,Long> {
    List<Task> findByUserAndCompletedFalse(Users user);
}
