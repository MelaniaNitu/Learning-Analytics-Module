package com.policat.LA.repositories;

import com.policat.LA.entities.QuestionTag;
import com.policat.LA.entities.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionTagRepository extends CrudRepository<QuestionTag, Long> {
}
