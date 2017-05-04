package com.fosun.fc.projects.creepers.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


/**
 * The persistent class for the T_CREEPERS_TYC_BASE_INFO database table.
 * 
 */
@Entity
@Table(name="T_CREEPERS_TYC_BASE_INFO")
@NamedQuery(name="TCreepersTycBaseInfo.findAll", query="SELECT t FROM TCreepersTycBaseInfo t")
public class TCreepersTycBaseInfo extends com.fosun.fc.modules.entity.BaseEntity  {
	private static final long serialVersionUID = 1L;

	@Column(name="APPROVED_DATE")
	private String approvedDate;

	@Column(name="BUSI_ID")
	private String busiId;

	@Column(name="BUSINESS_SCOPE")
	private String businessScope;

	@Column(name="ENTERPRISE_TYPE")
	private String enterpriseType;

	@Column(name="FILE_NAME")
	private String fileName;

	@Id
	@SequenceGenerator(name="T_CREEPERS_TYC_BASE_INFO_ID_GENERATOR", sequenceName="SEQ_CREEPERS_TYC_BASE_INFO")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="T_CREEPERS_TYC_BASE_INFO_ID_GENERATOR")
	private long id;

	@Column(name="LEGAL_REPRESENTATIVE")
	private String legalRepresentative;

	private String memo;

	private String name;

	@Column(name="OPENING_STATE")
	private String openingState;

	@Column(name="OPERATING_PERIOD")
	private String operatingPeriod;

	@Column(name="ORGANIZATION_CODE")
	private String organizationCode;

	@Column(name="REGISTERED_ADDRESS")
	private String registeredAddress;

	@Column(name="REGISTERED_CAPITAL")
	private String registeredCapital;

	@Column(name="REGISTRATION_AUTHORITY")
	private String registrationAuthority;

	@Column(name="REGISTRATION_TIME")
	private String registrationTime;

	private String trade;

	@Column(name="UNIFORM_CREDIT_CODE")
	private String uniformCreditCode;

	public TCreepersTycBaseInfo() {
	}

	public String getApprovedDate() {
		return this.approvedDate;
	}

	public void setApprovedDate(String approvedDate) {
		this.approvedDate = approvedDate;
	}

	public String getBusiId() {
		return this.busiId;
	}

	public void setBusiId(String busiId) {
		this.busiId = busiId;
	}

	public String getBusinessScope() {
		return this.businessScope;
	}

	public void setBusinessScope(String businessScope) {
		this.businessScope = businessScope;
	}

	public String getEnterpriseType() {
		return this.enterpriseType;
	}

	public void setEnterpriseType(String enterpriseType) {
		this.enterpriseType = enterpriseType;
	}

	public String getFileName() {
		return this.fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getLegalRepresentative() {
		return this.legalRepresentative;
	}

	public void setLegalRepresentative(String legalRepresentative) {
		this.legalRepresentative = legalRepresentative;
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

	public String getOpeningState() {
		return this.openingState;
	}

	public void setOpeningState(String openingState) {
		this.openingState = openingState;
	}

	public String getOperatingPeriod() {
		return this.operatingPeriod;
	}

	public void setOperatingPeriod(String operatingPeriod) {
		this.operatingPeriod = operatingPeriod;
	}

	public String getOrganizationCode() {
		return this.organizationCode;
	}

	public void setOrganizationCode(String organizationCode) {
		this.organizationCode = organizationCode;
	}

	public String getRegisteredAddress() {
		return this.registeredAddress;
	}

	public void setRegisteredAddress(String registeredAddress) {
		this.registeredAddress = registeredAddress;
	}

	public String getRegisteredCapital() {
		return this.registeredCapital;
	}

	public void setRegisteredCapital(String registeredCapital) {
		this.registeredCapital = registeredCapital;
	}

	public String getRegistrationAuthority() {
		return this.registrationAuthority;
	}

	public void setRegistrationAuthority(String registrationAuthority) {
		this.registrationAuthority = registrationAuthority;
	}

	public String getRegistrationTime() {
		return this.registrationTime;
	}

	public void setRegistrationTime(String registrationTime) {
		this.registrationTime = registrationTime;
	}

	public String getTrade() {
		return this.trade;
	}

	public void setTrade(String trade) {
		this.trade = trade;
	}

	public String getUniformCreditCode() {
		return this.uniformCreditCode;
	}

	public void setUniformCreditCode(String uniformCreditCode) {
		this.uniformCreditCode = uniformCreditCode;
	}

}