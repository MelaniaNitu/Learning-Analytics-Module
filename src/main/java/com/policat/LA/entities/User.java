package com.policat.LA.entities;

import com.policat.LA.enums.Role;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
public class User {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    @Column
    private BigDecimal scoreTp;

    @Column
    private BigDecimal scorePaper;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<QuizResult> quizResults = new ArrayList<>();


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        role = role;
    }

    public BigDecimal getScoreTp() {
        return scoreTp;
    }

    public void setScoreTp(BigDecimal scoreTp) {
        this.scoreTp = scoreTp;
    }

    public BigDecimal getScorePaper() {
        return scorePaper;
    }

    public void setScorePaper(BigDecimal scorePaper) {
        this.scorePaper = scorePaper;
    }

    public List<QuizResult> getQuizResults() {
        return quizResults;
    }

    public void setQuizResults(List<QuizResult> quizResults) {
        this.quizResults = quizResults;
    }
}
