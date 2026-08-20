package com.example.quiz.repository;

import com.example.quiz.entity.QuestionEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<QuestionEntity, String> {

	@EntityGraph(attributePaths = "options")
	List<QuestionEntity> findAllByOrderByIdAsc();
}
