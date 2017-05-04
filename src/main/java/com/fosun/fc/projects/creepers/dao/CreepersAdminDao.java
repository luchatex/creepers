package com.fosun.fc.projects.creepers.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.fosun.fc.projects.creepers.entity.TCreepersAdmin;

/**
 *
 * <p>
 * description: T_CREEPERS_ADMIN 信用中国-行政公告信息
 * <p>
 * 
 * @author LiZhanPing
 * @since 2016-12-23 17:48:15
 * @see
 */

public interface CreepersAdminDao
        extends JpaRepository<TCreepersAdmin, Long>, JpaSpecificationExecutor<TCreepersAdmin> {

    TCreepersAdmin findTop1ByKeyAndType(String key, String type);

    List<TCreepersAdmin> findByKeyAndType(String key, String type);
}
