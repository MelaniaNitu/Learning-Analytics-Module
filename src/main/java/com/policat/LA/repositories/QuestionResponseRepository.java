package com.policat.LA.repositories;

import com.policat.LA.entities.QuestionResponse;
import com.policat.LA.entities.QuestionTag;
import com.policat.LA.entities.QuizResult;
import com.policat.LA.entities.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionResponseRepository extends CrudRepository<QuestionResponse, Long> {
    @Query("select q from QuestionResponse q where q.quizResult.user = :user")
    List<QuestionResponse> findByUser(@Param("user") User user);
}
