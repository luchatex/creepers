package com.fosun.fc.projects.creepers.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fosun.fc.projects.creepers.entity.TCreepersDishonestBaidu;

/**
 *
 * <p>
 * description: T_CREEPERS_DISHONEST_BAIDU 百度-失信被执行人信息
 * <p>
 * 
 * @author LiZhanPing
 * @since 2017-02-17 18:23:42
 * @see
 */
public interface CreepersDishonestBaiduDao
        extends JpaRepository<TCreepersDishonestBaidu, Long>, JpaSpecificationExecutor<TCreepersDishonestBaidu> {

    List<TCreepersDishonestBaidu> findByNameAndCertNoAndCaseCode(String name, String certNo, String caseCode);

    List<TCreepersDishonestBaidu> findByNameAndCertNo(String name, String certNo);

    @Query("select count(*) from TCreepersDishonestBaidu where name = :name and certNo = :certNo")
    int countByNameAndCertNo(@Param("name")String name, @Param("certNo")String certNo);
}
