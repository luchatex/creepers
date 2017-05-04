package com.fosun.fc.projects.creepers.dto;


/**
*
* <p>
* description:
* T_CREEPERS_ADMIN_BEIJING	信用北京行政处罚表
* <p>
* @author Luxin
* @since 2017-02-08 10:30:51
* @see
*/

public class CreepersAdminBeijingDTO extends CreepersBaseDTO {
private static final long serialVersionUID = 1L;

//信息类型（admin_license_list:行政许可、admin_saction_list:行政处罚）
private String type;
//唯一标识号
private String key;
//信息内容（json格式）
private String content;
//备注
private String memo;
//逻辑删除标志，0：未删除，1：已删除
private String flag;
//创建者
private String createdBy;
//修改者
private String updatedBy;


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
public String getMemo() {
	return memo;
}
public void setMemo(String memo) {
	this.memo = memo;
}
public String getFlag() {
	return flag;
}
public void setFlag(String flag) {
	this.flag = flag;
}
public String getCreatedBy() {
	return createdBy;
}
public void setCreatedBy(String createdBy) {
	this.createdBy = createdBy;
}
public String getUpdatedBy() {
	return updatedBy;
}
public void setUpdatedBy(String updatedBy) {
	this.updatedBy = updatedBy;
}

}