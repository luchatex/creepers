package com.fosun.fc.projects.creepers.dto;

/**
 *
 * <p>
 * description: ﻿T_CREEPERS_CREDIT_BLACK_LIST 信用中国其他黑名单表
 * <p>
 * 
 * @author LiZhanPing
 * @since 2017-02-06 11:11:11
 * @see
 */

public class CreepersCreditBlackListDTO extends CreepersBaseDTO {

    /**
     * 
     */
    private static final long serialVersionUID = -1056308173574950L;
    
    // 信息类型
    private String type;
    // 企业名称
    private String name;
    // 组织机构代码
    private String code;
    // 归属区域
    private String province;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

}
