package com.fosun.fc.projects.creepers.dto;

/**
 *
 * <p>
 * description: T_CREEPERS_DISHONEST_BAIDU 百度-失信被执行人信息
 * <p>
 * 
 * @author LiZhanPing
 * @since 2017-02-17 18:25:24
 * @see
 */

public class CreepersDishonestBaiduDTO extends CreepersBaseDTO {
    /**
     * 
     */
    private static final long serialVersionUID = 9093280906298070768L;
    // 名字
    private String name;
    // 证件号码
    private String certNo;
    // 案号
    private String caseCode;
    // 信息内容（JSON格式）
    private String content;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCertNo() {
        return certNo;
    }

    public void setCertNo(String certNo) {
        this.certNo = certNo;
    }

    public String getCaseCode() {
        return caseCode;
    }

    public void setCaseCode(String caseCode) {
        this.caseCode = caseCode;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
