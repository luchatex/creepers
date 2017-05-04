package com.fosun.fc.projects.creepers.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.fosun.fc.projects.creepers.entity.TCreepersBusinessTag;

/**
 *
 * <p>
 * description: T_CREEPERS_BUSINESS_TAG 爬虫业务标签定义表
 * <p>
 * 
 * @author LiZhanPing
 * @since 2017-03-08 17:14:24
 * @see
 */

public interface CreepersBusinessTagDao
        extends JpaRepository<TCreepersBusinessTag, Long>, JpaSpecificationExecutor<TCreepersBusinessTag> {

    TCreepersBusinessTag findTop1ByTaskType(String taskType);
}
