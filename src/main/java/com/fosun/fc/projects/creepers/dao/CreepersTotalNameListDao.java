package com.fosun.fc.projects.creepers.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fosun.fc.projects.creepers.entity.TCreepersTotalNameList;

/**
 *
 * <p>
 * description: T_CREEPERS_TOTAL_NAME_LIST 企业全名录
 * <p>
 * 
 * @author LiZhanPing
 * @since 2017-03-21 16:14:33
 * @see
 */

public interface CreepersTotalNameListDao
        extends JpaRepository<TCreepersTotalNameList, Long>, JpaSpecificationExecutor<TCreepersTotalNameList> {

    @Query("select t.name from TCreepersTotalNameList t where t.id>= :startId and t.id<= :endId")
    List<String> findByIdGreaterThanEqualAndIdLessThanEqual(@Param("startId")long startId, @Param("endId")long endId);
    
    @Query("select max(t.id) from TCreepersTotalNameList t")
    int findMaxId();
    
    @Query("select count(t) from TCreepersTotalNameList t where t.id> :startId and t.id<= :endId")
    int countByIdGreaterThanAndIdLessThanEqual(@Param("startId")long startId, @Param("endId")long endId);

    List<TCreepersTotalNameList> findByName(String name);
}
