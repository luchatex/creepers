package com.fosun.fc.projects.creepers.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


/**
 * The persistent class for the T_CREEPERS_TASK_TEMPLATE database table.
 * 
 */
@Entity
@Table(name="T_CREEPERS_TASK_TEMPLATE")
@NamedQuery(name="TCreepersTaskTemplate.findAll", query="SELECT t FROM TCreepersTaskTemplate t")
public class TCreepersTaskTemplate extends com.fosun.fc.modules.entity.BaseEntity implements Serializable {

	/**
     * 
     */
    private static final long serialVersionUID = 4875933687190584396L;

    @Id
	@SequenceGenerator(name="T_CREEPERS_TASK_TEMPLATE_ID_GENERATOR", sequenceName="SEQ_CREEPERS_BUSINESS_TAG")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="T_CREEPERS_TASK_TEMPLATE_ID_GENERATOR")
	private long id;

	@Column(name="HTTP_TYPE")
	private String httpType;

	private String memo;

	@Column(name="PARAM_MAP")
	private String paramMap;

	@Column(name="TASK_TYPE")
	private String taskType;

	private String url;

	public TCreepersTaskTemplate() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getHttpType() {
		return this.httpType;
	}

	public void setHttpType(String httpType) {
		this.httpType = httpType;
	}

	public String getMemo() {
		return this.memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getParamMap() {
		return this.paramMap;
	}

	public void setParamMap(String paramMap) {
		this.paramMap = paramMap;
	}

	public String getTaskType() {
		return this.taskType;
	}

	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}