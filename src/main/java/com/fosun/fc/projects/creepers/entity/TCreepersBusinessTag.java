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
 * The persistent class for the T_CREEPERS_BUSINESS_TAG database table.
 * 
 */
@Entity
@Table(name = "T_CREEPERS_BUSINESS_TAG")
@NamedQuery(name = "TCreepersBusinessTag.findAll", query = "SELECT t FROM TCreepersBusinessTag t")
public class TCreepersBusinessTag extends com.fosun.fc.modules.entity.BaseEntity implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 3689593969150579339L;

    @Id
    @SequenceGenerator(name = "T_CREEPERS_BUSINESS_TAG_ID_GENERATOR", sequenceName = "SEQ_CREEPERS_BUSINESS_TAG")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "T_CREEPERS_BUSINESS_TAG_ID_GENERATOR")
    private long id;

    @Column(name = "BUSINESS_TAG")
    private BigDecimal businessTag;

    private String memo;

    @Column(name = "TASK_TYPE")
    private String taskType;

    public TCreepersBusinessTag() {
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getTaskType() {
        return this.taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

}
