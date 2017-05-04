package com.fosun.fc.projects.creepers.dao;

import java.util.List;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;

import com.fosun.fc.projects.creepers.entity.TCreepersCreditBlackList;

/**
 *
 * <p>
 * description: ﻿T_CREEPERS_CREDIT_BLACK_LIST 信用中国其他黑名单表
 * <p>
 * 
 * @author LiZhanPing
 * @since 2017-02-06 11:10:57
 * @see
 */

public interface CreepersCreditBlackListDao
        extends JpaRepository<TCreepersCreditBlackList, Long>, JpaSpecificationExecutor<TCreepersCreditBlackList> {

    List<TCreepersCreditBlackList> findByCode(String code);

    void deleteByName(String name);

    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    List<TCreepersCreditBlackList> findByName(String name);
    
}
