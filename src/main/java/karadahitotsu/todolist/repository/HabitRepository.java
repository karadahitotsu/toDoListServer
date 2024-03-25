package karadahitotsu.todolist.repository;

import karadahitotsu.todolist.entity.Habit;
import karadahitotsu.todolist.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HabitRepository extends JpaRepository<Habit,Long> {
    List<Habit>findByUser(Users users);
}
