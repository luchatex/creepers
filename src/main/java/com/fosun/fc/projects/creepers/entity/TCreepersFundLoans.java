package com.fosun.fc.projects.creepers.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 * The persistent class for the T_CREEPERS_FUND_LOANS database table.
 * 
 */
@Entity
@Table(name="T_CREEPERS_FUND_LOANS")
@NamedQuery(name="TCreepersFundLoans.findAll", query="SELECT t FROM TCreepersFundLoans t")
public class TCreepersFundLoans extends com.fosun.fc.modules.entity.BaseEntity implements Serializable {

	/**
     * 
     */
    private static final long serialVersionUID = -2030787950991467163L;

    @Id
	@SequenceGenerator(name="T_CREEPERS_FUND_LOANS_ID_GENERATOR", sequenceName="SEQ_CREEPERS_FUND_LOANS")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="T_CREEPERS_FUND_LOANS_ID_GENERATOR")
	private long id;

	@Temporal(TemporalType.DATE)
	@Column(name="LAST_REPAYMENT_DT")
	private Date lastRepaymentDt;

	@Column(name="LEFT_AMOUNT")
	private String leftAmount;

	@Column(name="LOAN_ACCOUNT_NO")
	private String loanAccountNo;

	@Column(name="LOAN_ACCOUNT_STATUS")
	private String loanAccountStatus;

	@Column(name="LOAN_AMOUNT")
	private String loanAmount;

	@Temporal(TemporalType.DATE)
	@Column(name="LOAN_DT")
	private Date loanDt;

	@Column(name="LOGIN_NAME")
	private String loginName;

	private String memo;

	private String period;

	@Column(name="REPAYMENT_METHOD")
	private String repaymentMethod;

	public TCreepersFundLoans() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getLastRepaymentDt() {
		return this.lastRepaymentDt;
	}

	public void setLastRepaymentDt(Date lastRepaymentDt) {
		this.lastRepaymentDt = lastRepaymentDt;
	}

	public String getLeftAmount() {
		return this.leftAmount;
	}

	public void setLeftAmount(String leftAmount) {
		this.leftAmount = leftAmount;
	}

	public String getLoanAccountNo() {
		return this.loanAccountNo;
	}

	public void setLoanAccountNo(String loanAccountNo) {
		this.loanAccountNo = loanAccountNo;
	}

	public String getLoanAccountStatus() {
		return this.loanAccountStatus;
	}

	public void setLoanAccountStatus(String loanAccountStatus) {
		this.loanAccountStatus = loanAccountStatus;
	}

	public String getLoanAmount() {
		return this.loanAmount;
	}

	public void setLoanAmount(String loanAmount) {
		this.loanAmount = loanAmount;
	}

	public Date getLoanDt() {
		return this.loanDt;
	}

	public void setLoanDt(Date loanDt) {
		this.loanDt = loanDt;
	}

	public String getLoginName() {
		return this.loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getMemo() {
		return this.memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getPeriod() {
		return this.period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public String getRepaymentMethod() {
		return this.repaymentMethod;
	}

	public void setRepaymentMethod(String repaymentMethod) {
		this.repaymentMethod = repaymentMethod;
	}

}