package com.fosun.fc.projects.creepers.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


/**
 * The persistent class for the T_CREEPERS_ADMIN database table.
 * 
 */
@Entity
@Table(name="T_CREEPERS_ADMIN")
@NamedQuery(name="TCreepersAdmin.findAll", query="SELECT t FROM TCreepersAdmin t")
public class TCreepersAdmin extends com.fosun.fc.modules.entity.BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="T_CREEPERS_ADMIN_ID_GENERATOR", sequenceName="SEQ_CREEPERS_ADMIN_INFO")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="T_CREEPERS_ADMIN_ID_GENERATOR")
	private long id;

	@Lob
	private String content;

	private String key;

	private String memo;

	private String type;

	public TCreepersAdmin() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getKey() {
		return this.key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getMemo() {
		return this.memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

}