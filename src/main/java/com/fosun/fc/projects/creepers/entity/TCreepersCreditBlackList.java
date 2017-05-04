package com.fosun.fc.projects.creepers.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


/**
 * The persistent class for the T_CREEPERS_CREDIT_BLACK_LIST database table.
 * 
 */
@Entity
@Table(name="T_CREEPERS_CREDIT_BLACK_LIST")
@NamedQuery(name="TCreepersCreditBlackList.findAll", query="SELECT t FROM TCreepersCreditBlackList t")
public class TCreepersCreditBlackList extends com.fosun.fc.modules.entity.BaseEntity implements Serializable {
    
    /**
     * 
     */
    private static final long serialVersionUID = 8137498256434232140L;

    @Id
	@SequenceGenerator(name="T_CREEPERS_CREDIT_BLACK_LIST_ID_GENERATOR", sequenceName="SEQ_CREEPERS_CREDIT_BLACK_LIST")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="T_CREEPERS_CREDIT_BLACK_LIST_ID_GENERATOR")
	private long id;

	private String code;

	private String memo;

	private String name;

	private String province;

	private String type;

	public TCreepersCreditBlackList() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMemo() {
		return this.memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProvince() {
		return this.province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

}