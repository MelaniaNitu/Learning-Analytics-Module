package com.policat.LA.repositories;

import com.policat.LA.entities.QuizResult;
import com.policat.LA.entities.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface QuizResultRepository extends CrudRepository<QuizResult, Long> {
    List<QuizResult> findByUser(User user);
}
