package com.fosun.fc.projects.creepers.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * The persistent class for the T_CREEPERS_TOTAL_NAME_LIST database table.
 * 
 */
@Entity
@Table(name = "T_CREEPERS_TOTAL_NAME_LIST")
@NamedQuery(name = "TCreepersTotalNameList.findAll", query = "SELECT t FROM TCreepersTotalNameList t")
public class TCreepersTotalNameList extends com.fosun.fc.modules.entity.BaseEntity implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 9007980905925122074L;

    @Id
    @SequenceGenerator(name = "T_CREEPERS_TOTAL_NAME_LIST_ID_GENERATOR", sequenceName = "SEQ_CREEPERS_TOTAL_NAME_LIST")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "T_CREEPERS_TOTAL_NAME_LIST_ID_GENERATOR")
    private long id;

    private String area;

    @Column(name = "BUSINESS_TAG")
    private BigDecimal businessTag;

    private String memo;

    private String name;

    @Column(name = "USC_CODE")
    private String uscCode;

    public TCreepersTotalNameList() {
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

    public BigDecimal getBusinessTag() {
        return this.businessTag;
    }

    public void setBusinessTag(BigDecimal businessTag) {
        this.businessTag = businessTag;
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

    public String getUscCode() {
        return this.uscCode;
    }

    public void setUscCode(String uscCode) {
        this.uscCode = uscCode;
    }

}
