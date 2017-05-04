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
 * The persistent class for the T_CREEPERS_TEMP_SHIXIN database table.
 * 
 */
@Entity
@Table(name="T_CREEPERS_TEMP_SHIXIN")
@NamedQuery(name="TCreepersTempShixin.findAll", query="SELECT t FROM TCreepersTempShixin t")
public class TCreepersTempShixin extends com.fosun.fc.modules.entity.BaseEntity implements Serializable {

	/**
     * 
     */
    private static final long serialVersionUID = -7380997266199485680L;

    @Id
	@SequenceGenerator(name="T_CREEPERS_TEMP_SHIXIN_ID_GENERATOR", sequenceName="SEQ_CREEPERS_TEMP_SHIXIN")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="T_CREEPERS_TEMP_SHIXIN_ID_GENERATOR")
	private long id;

	@Column(name="CARD_NAME")
	private String cardName;

	@Column(name="CARD_NO")
	private String cardNo;

	private String memo;

	@Column(name="PHONE_NUM")
	private String phoneNum;

	public TCreepersTempShixin() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCardName() {
		return this.cardName;
	}

	public void setCardName(String cardName) {
		this.cardName = cardName;
	}

	public String getCardNo() {
		return this.cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public String getMemo() {
		return this.memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getPhoneNum() {
		return this.phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}

}