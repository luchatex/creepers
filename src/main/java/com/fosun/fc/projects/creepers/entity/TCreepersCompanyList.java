package com.fosun.fc.projects.creepers.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


/**
 * The persistent class for the T_CREEPERS_COMPANY_LIST database table.
 * 
 */
@Entity
@Table(name="T_CREEPERS_COMPANY_LIST")
@NamedQuery(name="TCreepersCompanyList.findAll", query="SELECT t FROM TCreepersCompanyList t")
public class TCreepersCompanyList extends com.fosun.fc.modules.entity.BaseEntity implements Serializable {

	/**
     * 
     */
    private static final long serialVersionUID = 5510110391114723367L;

    @Id
	@SequenceGenerator(name="T_CREEPERS_COMPANY_LIST_ID_GENERATOR", sequenceName="SEQ_CREEPERS_COMPANY_LIST")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="T_CREEPERS_COMPANY_LIST_ID_GENERATOR")
	private long id;

	private String area;

	@Lob
	@Column(name="BASE_INFO")
	private String baseInfo;

	@Column(name="BUSINESS_TAG")
	private BigDecimal businessTag;

	@Column(name="INDUSTRY_CLASS")
	private String industryClass;

	@Column(name="INDUSTRY_TYPE")
	private String industryType;

	private String memo;

	private String name;

	public TCreepersCompanyList() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getArea() {
		return this.area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getBaseInfo() {
		return this.baseInfo;
	}

	public void setBaseInfo(String baseInfo) {
		this.baseInfo = baseInfo;
	}

	public BigDecimal getBusinessTag() {
		return this.businessTag;
	}

	public void setBusinessTag(BigDecimal businessTag) {
		this.businessTag = businessTag;
	}

	public String getIndustryClass() {
		return this.industryClass;
	}

	public void setIndustryClass(String industryClass) {
		this.industryClass = industryClass;
	}

	public String getIndustryType() {
		return this.industryType;
	}

	public void setIndustryType(String industryType) {
		this.industryType = industryType;
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

}