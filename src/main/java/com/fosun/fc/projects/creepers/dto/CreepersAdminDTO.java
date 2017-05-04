package com.fosun.fc.projects.creepers.dto;

/**
 *
 * <p>
 * description: T_CREEPERS_ADMIN 信用中国-行政公告信息
 * <p>
 * 
 * @author LiZhanPing
 * @since 2016-12-23 17:48:05
 * @see
 */

public class CreepersAdminDTO extends CreepersBaseDTO {
    private static final long serialVersionUID = 1L;
    // 信息类型（admin_license_list:行政许可、admin_saction_list:行政处罚）
    private String type;
    // 唯一标识号
    private String key;
    // 信息内容（JSON格式）
    private String content;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
