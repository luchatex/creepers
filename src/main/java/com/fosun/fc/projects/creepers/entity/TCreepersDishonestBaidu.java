package com.fosun.fc.projects.creepers.entity;

import java.io.Serializable;

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
 * The persistent class for the T_CREEPERS_DISHONEST_BAIDU database table.
 * 
 */
@Entity
@Table(name = "T_CREEPERS_DISHONEST_BAIDU")
@NamedQuery(name = "TCreepersDishonestBaidu.findAll", query = "SELECT t FROM TCreepersDishonestBaidu t")
public class TCreepersDishonestBaidu extends com.fosun.fc.modules.entity.BaseEntity implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -1153361658647553594L;

    @Id
    @SequenceGenerator(name = "T_CREEPERS_DISHONEST_BAIDU_ID_GENERATOR", sequenceName = "SEQ_CREEPERS_DISHONEST_BAIDU")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "T_CREEPERS_DISHONEST_BAIDU_ID_GENERATOR")
    private long id;

    @Column(name = "CASE_CODE")
    private String caseCode;

    @Column(name = "CERT_NO")
    private String certNo;

    @Lob
    private String content;

    private String memo;

    private String name;

    public TCreepersDishonestBaidu() {
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCaseCode() {
        return this.caseCode;
    }

    public void setCaseCode(String caseCode) {
        this.caseCode = caseCode;
    }

    public String getCertNo() {
        return this.certNo;
    }

    public void setCertNo(String certNo) {
        this.certNo = certNo;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
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
