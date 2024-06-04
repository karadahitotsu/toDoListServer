package karadahitotsu.todolist.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import karadahitotsu.todolist.entity.*;
import karadahitotsu.todolist.repository.*;
import karadahitotsu.todolist.service.AcessTokenGenerator;
import karadahitotsu.todolist.service.ConfirmationCodeGenerator;
import karadahitotsu.todolist.service.MailService;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public HashMap<String,Object> createUser(@RequestBody Users user){
        HashMap<String,Object> returnstatement = new HashMap<>();
        String salt = BCrypt.gensalt();
        String hashedPassword = BCrypt.hashpw(user.getPassword(), salt);
        if(!isValidEmail(user.getEmail())){
            returnstatement.put("status","put real email");
            return returnstatement;
        }
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
        returnstatement.put("status","completed");
        return returnstatement;


    }
    @GetMapping("/api/verification")
    public HashMap<String,Object> verificateUser(@RequestParam long user_id,@RequestParam String token){
        HashMap<String,Object> returnstatement = new HashMap<>();
        List<Token> userr_id = tokenRepository.findByUser_idAndToken(user_id,token);
        System.out.println(user_id+token);
        Long token_user_id = userr_id.get(0).getUser().getId();
        System.out.println(token_user_id);
        Users user= userRepository.getReferenceById(token_user_id);
        user.setVerificated(true);
        userRepository.save(user);
        returnstatement.put("status","completed");
        return returnstatement;

    }
    @PostMapping("/api/authorization")
    public HashMap<String,Object> authorization(@RequestBody Users user){
        HashMap<String,Object> returnstatement = new HashMap<>();
        List usersbyemail = userRepository.findByEmail(user.getEmail());
        if(!usersbyemail.isEmpty()){
            Users check_user = userRepository.findByEmail(user.getEmail()).get(0);

            if(BCrypt.hashpw(user.getPassword(),check_user.getSalt()).equals(check_user.getPassword())){
                if(check_user.getVerificated().equals(false)){
                    returnstatement.put("status","need verification");
                    return returnstatement;
                }
                Access_token accessToken = new Access_token();
                accessToken.setUser(check_user);
                String returntoken = acessTokenGenerator.generate();
                accessToken.setToken(returntoken);
                accessRepository.save(accessToken);
                returnstatement.put("status","completed");
                returnstatement.put("token",returntoken);
                return returnstatement;


            }
        }

        returnstatement.put("status","something wrong");
        return returnstatement;

    }
    @PostMapping("/api/createTask")
    public HashMap<String,Object> createTask(@RequestBody ObjectNode objectNode){
        HashMap<String,Object> returnstatement = new HashMap<>();
        returnstatement.put("status","something wrong");
        String taskTitle = objectNode.get("title").asText();
        String taskDescription = objectNode.get("description").asText();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date taskDueDate = new Date();
        java.sql.Date sqlDate = new java.sql.Date(1,1,1);
        try {
             taskDueDate = dateFormat.parse(objectNode.get("dueDate").asText()) ;
             sqlDate = new java.sql.Date(taskDueDate.getTime());

        }
        catch (Exception e){

        }

        Task task = new Task();
        task.setTitle(taskTitle);
        task.setDescription(taskDescription);
        task.setDueDate(sqlDate);
        String token = objectNode.get("token").asText();
        Access_token accessToken = accessRepository.findByToken(token).get(0);
        Users user = accessToken.getUser();
        task.setUser(user);
        taskRepository.save(task);
        returnstatement.replace("status","completed");
        return returnstatement;
    }
    @PostMapping("/api/updateTask")
    public HashMap<String,Object> updateTask(@RequestBody ObjectNode objectNode){
        HashMap<String,Object> returnstatement = new HashMap<>();
        returnstatement.put("status","something wrong");
        String token = objectNode.get("token").asText();
        Access_token accessToken = accessRepository.findByToken(token).get(0);
        Users user = accessToken.getUser();
        Long taskId = objectNode.get("id").asLong();
        //String taskTitle = objectNode.get("title").asText();
        //String taskDescription = objectNode.get("description").asText();
        //Boolean taskCompleted = objectNode.get("completed").asBoolean();
        //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //Date taskDueDate = new Date();
        //java.sql.Date sqlDate = new java.sql.Date(1,1,1);
        //try {
        //    taskDueDate = dateFormat.parse(objectNode.get("dueDate").asText()) ;
         //   sqlDate = new java.sql.Date(taskDueDate.getTime());

        //}
        //catch (Exception e){

        //}
        Task original_Task = taskRepository.getReferenceById(taskId);
        //original_Task.setTitle(taskTitle);
        //original_Task.setDescription(taskDescription);
        //original_Task.setDueDate(sqlDate);
        original_Task.setCompleted(true);
        //if(!original_Task.getCompleted().equals(taskCompleted)){
            Level level = levelRepository.findByUser(user).get(0);
            if(level.getExperience()+50>=1000){
                level.setExperience(level.getExperience()+50-1000);
                level.setLevel(level.getLevel()+1);
            }
            else {
                level.setExperience(level.getExperience()+50);
            }
            levelRepository.save(level);
        //}
        //original_Task.setCompleted(taskCompleted);
        returnstatement.replace("status","completed");
        return returnstatement;
    }
    @DeleteMapping("/api/deleteTask")
    public HashMap<String,Object> deleteTask(@RequestBody ObjectNode objectNode){
        HashMap<String,Object> returnstatement = new HashMap<>();
        returnstatement.put("status","something wrong");
        String token = objectNode.get("token").asText();
        Access_token accessToken = accessRepository.findByToken(token).get(0);
        Users user = accessToken.getUser();
        Task original_Task = taskRepository.getReferenceById(objectNode.get("id").asLong());
        taskRepository.delete(original_Task);
        returnstatement.replace("status","completed");
        return returnstatement;
    }
    @PostMapping("/api/createHabit")
    public HashMap<String,Object> createHabit(@RequestBody ObjectNode objectNode){
        HashMap<String,Object> returnstatement = new HashMap<>();
        returnstatement.put("status","something wrong");
        String token = objectNode.get("token").asText();
        Access_token accessToken = accessRepository.findByToken(token).get(0);
        Users user = accessToken.getUser();
        String taskTitle = objectNode.get("title").asText();
        String taskDescription = objectNode.get("description").asText();
        Time taskFrequency = Time.valueOf(objectNode.get("frequency").asText());
        Habit habit = new Habit();
        habit.setFrequency(taskFrequency);
        habit.setTitle(taskTitle);
        habit.setDescription(taskDescription);
        habit.setUser(user);
        habitRepository.save(habit);
        returnstatement.replace("status","completed");
        return returnstatement;

    }
    @PostMapping("/api/updateHabit")
    public HashMap<String,Object> updateHabit(@RequestBody ObjectNode objectNode){
        HashMap<String,Object> returnstatement = new HashMap<>();
        returnstatement.put("status","something wrong");
        String token = objectNode.get("token").asText();
        Access_token accessToken = accessRepository.findByToken(token).get(0);
        Users user = accessToken.getUser();
        Long habitId = objectNode.get("id").asLong();
        Habit original_habit = habitRepository.getReferenceById(habitId);
        original_habit.setTitle(objectNode.get("title").asText());
        original_habit.setDescription(objectNode.get("description").asText());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date habitlactcompleted = new Date();
        java.sql.Date sqlDate = new java.sql.Date(1,1,1);
        try {
            habitlactcompleted = dateFormat.parse(objectNode.get("lastCompleted").asText()) ;
            sqlDate = new java.sql.Date(habitlactcompleted.getTime());

        }
        catch (Exception e){

        }
        try{
        if(!original_habit.getLastCompleted().equals(sqlDate)){
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
        }catch (Exception e){

        }
        original_habit.setLastCompleted(sqlDate);
        habitRepository.save(original_habit);

        returnstatement.replace("status","completed");
        return returnstatement;
    }
    @DeleteMapping("/api/deleteHabit")
    public HashMap<String,Object> deleteHabit(@RequestBody ObjectNode objectNode){
        HashMap<String,Object> returnstatement = new HashMap<>();
        returnstatement.put("status","something wrong");
        String token = objectNode.get("token").asText();
        Access_token accessToken = accessRepository.findByToken(token).get(0);
        Users user = accessToken.getUser();

        Habit original_Habit=habitRepository.getReferenceById(objectNode.get("id").asLong());
        habitRepository.delete(original_Habit);
        returnstatement.replace("status","completed");
        return returnstatement;
    }
    @PostMapping("/api/getTask")
    public HashMap<String,Object> getTask(@RequestBody ObjectNode objectNode){
        HashMap<String,Object> returnstatement = new HashMap<>();
        returnstatement.put("status","something wrong");
        String token = objectNode.get("token").asText();
        Access_token accessToken = accessRepository.findByToken(token).get(0);
        Users user = accessToken.getUser();
        List<Task> tasks = taskRepository.findByUserAndCompletedFalse(user);


        returnstatement.replace("status","completed");
        returnstatement.put("tasks",tasks);
        return returnstatement;
    }
    @PostMapping("/api/getHabit")
    public HashMap<String,Object> getHabit(@RequestBody ObjectNode objectNode) {
        HashMap<String,Object> returnstatement = new HashMap<>();
        returnstatement.put("status","something wrong");
        String token = objectNode.get("token").asText();
        Access_token accessToken = accessRepository.findByToken(token).get(0);
        Users user = accessToken.getUser();
        List<Habit> habits = habitRepository.findByUser(user);
        returnstatement.replace("status","completed");
        returnstatement.put("habits",habits);
        return returnstatement;
    }
    @PostMapping("/api/getProfile")
    public HashMap<String,Object> getProfile(@RequestBody ObjectNode objectNode){
        HashMap<String,Object> returnstatement = new HashMap<>();
        returnstatement.put("status","something wrong");
        String token = objectNode.get("token").asText();
        Access_token accessToken = accessRepository.findByToken(token).get(0);
        Users user = accessToken.getUser();
        Level level = levelRepository.findByUser(user).get(0);
        returnstatement.replace("status","completed");
        returnstatement.put("email",user.getEmail());
        returnstatement.put("username",user.getUsername());
        returnstatement.put("level",level);
        return returnstatement;
    }
    public static boolean isValidEmail(String email) {
        // Паттерн для проверки email-адреса
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
