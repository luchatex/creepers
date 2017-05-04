package com.fosun.fc.projects.creepers.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.fosun.fc.projects.creepers.entity.TCreepersAdminBeijing;

/**
*
* <p>
* description:
* T_CREEPERS_ADMIN_BEIJING	信用北京行政处罚表
* <p>
* @author luxin
* @since 2017-02-08 10:44:12
* @see
*/

public interface CreepersAdminBeijingDao extends JpaRepository<TCreepersAdminBeijing, Long>, JpaSpecificationExecutor<TCreepersAdminBeijing> {
	
	List<TCreepersAdminBeijing> findByKey(String key);
	
}