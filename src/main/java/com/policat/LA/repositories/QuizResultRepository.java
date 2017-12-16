package com.policat.LA.repositories;

import com.policat.LA.entities.QuizResult;
import com.policat.LA.entities.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuizResultRepository extends CrudRepository<QuizResult, Long> {
    @Query("select q from QuizResult q where q.user = :user order by q.date desc")
    List<QuizResult> findByUser(@Param("user") User user);
}
