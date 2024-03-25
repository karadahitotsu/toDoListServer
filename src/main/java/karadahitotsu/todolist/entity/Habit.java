package karadahitotsu.todolist.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

@Entity
public class Habit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Column(nullable = false)
    private String title;

    private String description;

    @Temporal(TemporalType.TIME)
    private Time frequency;

    private Integer streak = 0; // Initialize streak to 0
    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp nextComplete;
    @Temporal(TemporalType.DATE)
    private Date lastCompleted;

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp createdAt;

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Time getFrequency() {
        return frequency;
    }

    public void setFrequency(Time frequency) {
        this.frequency = frequency;
    }

    public Integer getStreak() {
        return streak;
    }

    public void setStreak(Integer streak) {
        this.streak = streak;
    }

    public Timestamp getNextComplete() {
        return nextComplete;
    }

    public void setNextComplete(Timestamp nextComplete) {
        this.nextComplete = nextComplete;
    }

    public Date getLastCompleted() {
        return lastCompleted;
    }

    public void setLastCompleted(Date lastCompleted) {
        this.lastCompleted = lastCompleted;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}
