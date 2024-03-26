package karadahitotsu.todolist.controller;

import karadahitotsu.todolist.entity.*;
import karadahitotsu.todolist.repository.*;
import karadahitotsu.todolist.service.AcessTokenGenerator;
import karadahitotsu.todolist.service.ConfirmationCodeGenerator;
import karadahitotsu.todolist.service.MailService;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class Controller {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private MailService mailService;
    @Autowired
    private AccessRepository accessRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private LevelRepository levelRepository;
    @Autowired
    private HabitRepository habitRepository;

    private ConfirmationCodeGenerator codeGenerator = new ConfirmationCodeGenerator();
    private AcessTokenGenerator acessTokenGenerator = new AcessTokenGenerator();

    @PostMapping("/api/create_user")
    public Users createUser(@RequestBody Users user){
        String salt = BCrypt.gensalt();
        String hashedPassword = BCrypt.hashpw(user.getPassword(), salt);
        user.setPassword(hashedPassword);
        user.setSalt(salt);
        String Verification = codeGenerator.generate();
        Users saved = userRepository.save(user);
        Level level = new Level();
        level.setUser(saved);
        levelRepository.save(level);
        Token token = new Token();
        token.setUser(saved);
        token.setToken(Verification);
        tokenRepository.save(token);
        mailService.sendToken(saved.getEmail(),saved.getId(),token.getToken());
        return saved;


    }
    @GetMapping("/api/verification")
    public Users verificateUser(@RequestParam long user_id,@RequestParam String token){
        List<Token> userr_id = tokenRepository.findByUser_idAndToken(user_id,token);
        System.out.println(user_id+token);
        Long token_user_id = userr_id.get(0).getUser().getId();
        System.out.println("");
        Users user= userRepository.getReferenceById(token_user_id);
        user.setVerificated(true);
       return userRepository.save(user);

    }
    @PostMapping("/api/authorization")
    public String authorization(@RequestBody Users user){
        List usersbyemail = userRepository.findByEmail(user.getEmail());
        if(usersbyemail.size()>0){
            Users check_user = userRepository.findByEmail(user.getEmail()).get(0);

            if(BCrypt.hashpw(user.getPassword(),check_user.getSalt()).equals(check_user.getPassword())){
                if(check_user.getVerificated().equals(false)){
                    return "need verification";
                }
                Access_token accessToken = new Access_token();
                accessToken.setUser(check_user);
                String returntoken = acessTokenGenerator.generate();
                accessToken.setToken(returntoken);
                accessRepository.save(accessToken);
                return returntoken;

            }
        }


        return "authorization problem";

    }
    @PostMapping("/api/createTask")
    public Task createTask(@RequestBody Task task,@RequestBody String token){
        Access_token accessToken = accessRepository.findByToken(token).get(0);
        Users user = accessToken.getUser();
        task.setUser(user);
        taskRepository.save(task);

        return task;
    }
    @PostMapping("/api/updateTask")
    public Task updateTask(@RequestBody Task task,@RequestBody String token){
        Access_token accessToken = accessRepository.findByToken(token).get(0);
        Users user = accessToken.getUser();
        Task original_Task = taskRepository.getReferenceById(task.getId());
        original_Task.setTitle(task.getTitle());
        original_Task.setDescription(task.getDescription());
        original_Task.setDueDate(task.getDueDate());
        if(!original_Task.getCompleted().equals(task.getCompleted())){
            Level level = levelRepository.findByUser(user).get(0);
            if(level.getExperience()+50>=1000){
                level.setExperience(level.getExperience()+50-1000);
                level.setLevel(level.getLevel()+1);
            }
            else {
                level.setExperience(level.getExperience()+50);
            }
            levelRepository.save(level);
        }
        original_Task.setCompleted(task.getCompleted());
        taskRepository.save(original_Task);
        return original_Task;
    }
    @DeleteMapping("/api/deleteTask")
    public Task deleteTask(@RequestBody Task task,@RequestBody String token){
        Access_token accessToken = accessRepository.findByToken(token).get(0);
        Users user = accessToken.getUser();
        Task original_Task = taskRepository.getReferenceById(task.getId());
        taskRepository.delete(original_Task);
        return original_Task;
    }
    @PostMapping("/api/createHabit")
    public Habit createHabit(@RequestBody Habit habit,@RequestBody String token){
        Access_token accessToken = accessRepository.findByToken(token).get(0);
        Users user = accessToken.getUser();
        habit.setUser(user);
        habitRepository.save(habit);
        return habit;

    }
    @PostMapping("/api/updateHabit")
    public Habit updateHabit(@RequestBody Habit habit,@RequestBody String token){
        Access_token accessToken = accessRepository.findByToken(token).get(0);
        Users user = accessToken.getUser();
        Habit original_habit = habitRepository.getReferenceById(habit.getId());
        original_habit.setTitle(habit.getTitle());
        original_habit.setDescription(habit.getDescription());
        if(!original_habit.getLastCompleted().equals(habit.getLastCompleted())){
            Level level = levelRepository.findByUser(user).get(0);
            if(level.getExperience()+30>=1000){
                level.setExperience(level.getExperience()+30-1000);
                level.setLevel(level.getLevel()+1);
            }
            else {
                level.setExperience(level.getExperience()+30);
            }
            levelRepository.save(level);
        }
        original_habit.setLastCompleted(habit.getLastCompleted());
        habitRepository.save(original_habit);

        return habit;
    }
    @DeleteMapping("/api/deleteHabit")
    public Habit deleteHabit(@RequestBody Habit habit,@RequestBody String token){
        Access_token accessToken = accessRepository.findByToken(token).get(0);
        Users user = accessToken.getUser();
        Habit original_Habit=habitRepository.getReferenceById(habit.getId());
        habitRepository.delete(original_Habit);
        return original_Habit;
    }



}
