package com.fosun.fc.projects.creepers.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fosun.fc.modules.entity.BaseEntity;


/**
 * The persistent class for the T_CREEPERS_ADMIN_BEIJING database table.
 */
@Entity
@Table(name = "T_CREEPERS_ADMIN_BEIJING")
@NamedQuery(name = "TCreepersAdminBeijing.findAll", query = "SELECT t FROM TCreepersAdminBeijing t")
public class TCreepersAdminBeijing extends BaseEntity implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -6750593303707730565L;

    @Lob
    private String content;

    @Id
    @SequenceGenerator(name = "T_CREEPERS_ADMIN_ID_GENERATOR", sequenceName = "SEQ_CREEPERS_ADMIN_INFO")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "T_CREEPERS_ADMIN_ID_GENERATOR")
    private BigDecimal id;

    private String key;

    private String memo;

    private String type;

    public TCreepersAdminBeijing() {
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public BigDecimal getId() {
        return this.id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
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