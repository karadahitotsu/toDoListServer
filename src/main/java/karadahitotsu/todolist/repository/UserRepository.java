package karadahitotsu.todolist.repository;

import karadahitotsu.todolist.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<Users,Long> {
    List<Users> findByEmail(String email);
}
