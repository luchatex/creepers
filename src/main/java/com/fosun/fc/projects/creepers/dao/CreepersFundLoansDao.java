package com.fosun.fc.projects.creepers.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fosun.fc.projects.creepers.entity.TCreepersFundLoans;

/**
 *
 * <p>
 * description: T_CREEPERS_FUND_LOANS 公积金账户贷款明细表
 * <p>
 * 
 * @author LiZhanPing
 * @since 2017-03-22 15:36:25
 * @see
 */

public interface CreepersFundLoansDao
        extends JpaRepository<TCreepersFundLoans, Long>, JpaSpecificationExecutor<TCreepersFundLoans> {
    @Query("select t from TCreepersFundLoans t where t.loginName =:loginName")
    List<TCreepersFundLoans> findByLoginName(@Param("loginName") String loginName);

    @Modifying
    @Query("delete  from TCreepersFundLoans t where t.loginName = :loginName")
    void deleteByLoginName(@Param("loginName") String loginName);
}
