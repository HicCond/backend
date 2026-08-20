package com.example.quiz.mapper;

import com.example.quiz.dto.QuestionDto;
import com.example.quiz.entity.QuestionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
		unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface QuestionMapper {

	QuestionDto toDto(QuestionEntity entity);

	List<QuestionDto> toDtoList(List<QuestionEntity> entities);
}
