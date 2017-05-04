package com.fosun.fc.projects.creepers.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fosun.fc.projects.creepers.entity.TCreepersTaskTemplate;

/**
 *
 * <p>
 * description: T_CREEPERS_BUSINESS_TAG 爬虫任务队列模板信息表
 * <p>
 * 
 * @author LiZhanPing
 * @since 2017-03-08 17:14:24
 * @see
 */

public interface CreepersTaskTemplateDao
        extends JpaRepository<TCreepersTaskTemplate, Long>, JpaSpecificationExecutor<TCreepersTaskTemplate> {

    @Query("select t from TCreepersTaskTemplate t where t.taskType = :taskType")
    List<TCreepersTaskTemplate> findByTaskType(@Param("taskType")String taskType);
}
