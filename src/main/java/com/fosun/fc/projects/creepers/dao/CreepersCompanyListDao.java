package com.fosun.fc.projects.creepers.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fosun.fc.projects.creepers.entity.TCreepersCompanyList;

/**
*
* <p>
* description:
* T_CREEPERS_COMPANY_LIST	企业名录
* <p>
* @author MaXin
* @since 2017-02-10 12:58:31
* @see
*/

public interface CreepersCompanyListDao extends JpaRepository<TCreepersCompanyList, Long>, JpaSpecificationExecutor<TCreepersCompanyList> {

    TCreepersCompanyList findTop1ByName(String name);
    
    @Query("select t.name from TCreepersCompanyList t where t.id > :startId and t.id <= :endId")
    List<String> findByIdGreaterThanAndIdLessThanEqual(@Param("startId")long id, @Param("endId")long endId);
    
    @Query("select max(t.id) from TCreepersCompanyList t")
    int findMaxId();
    
    @Query("select count(t) from TCreepersCompanyList t where t.id> :startId and t.id<= :endId")
    int countByIdGreaterThanAndIdLessThanEqual(@Param("startId")long startId, @Param("endId")long endId);

    List<TCreepersCompanyList> findByName(String name);
}