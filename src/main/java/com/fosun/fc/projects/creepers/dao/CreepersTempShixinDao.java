package com.fosun.fc.projects.creepers.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fosun.fc.projects.creepers.entity.TCreepersTempShixin;

/**
 *
 * <p>
 * description: T_CREEPERS_TOUR_BLACK_LIST 信用中国-旅游黑名单明细
 * <p>
 * 
 * @author MaXin
 * @since 2016-10-31 17:22:04
 * @see
 */

public interface CreepersTempShixinDao extends JpaRepository<TCreepersTempShixin, Long>, JpaSpecificationExecutor<TCreepersTempShixin> {

    @Query("select max(t.id) from TCreepersTempShixin t")
    int findMaxId();

    @Query("select count(t) from TCreepersTempShixin t where t.id> :startId and t.id<= :endId")
    int countByIdGreaterThanAndIdLessThanEqual(@Param("startId")long id, @Param("endId")long endId);

    @Query("select t from TCreepersTempShixin t where t.id > :startId and t.id <= :endId")
    List<TCreepersTempShixin> findByIdGreaterThanAndIdLessThanEqual(@Param("startId")long id, @Param("endId")long endId);

}
