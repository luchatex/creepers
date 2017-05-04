package com.fosun.fc.projects.creepers.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.fosun.fc.projects.creepers.entity.TCreepersTycBaseInfo;

/**
*
* <p>
* description:
* T_CREEPERS_TYC_BASE_INFO	天眼查工商基础信息
* <p>
* @author MaXin
* @since 2017-02-08 22:30:25
* @see
*/

public interface CreepersTycBaseInfoDao extends JpaRepository<TCreepersTycBaseInfo, Long>, JpaSpecificationExecutor<TCreepersTycBaseInfo> {

    TCreepersTycBaseInfo findTop1ByName(String name);
}