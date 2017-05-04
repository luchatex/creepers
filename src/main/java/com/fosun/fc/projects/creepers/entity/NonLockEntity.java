package com.fosun.fc.projects.creepers.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fosun.fc.modules.utils.Clock;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by luxin on 3/16/17.
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class NonLockEntity implements Serializable{

    private static final long serialVersionUID = 8968972584033203030L;

    private static final Clock clock = Clock.DEFAULT;

    @Version
    private Long version = 0L;

    private String flag = "0";

    @Column(name = "CREATED_BY",updatable = false)
    @CreatedBy
    protected String createdBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @CreatedDate
    @Column(name = "CREATED_DT", updatable = false)
    protected Date createdDt = clock.getCurrentDate();

    @NotBlank
    @Column(name = "UPDATED_BY")
    @LastModifiedBy
    protected String updatedBy;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "UPDATED_DT")
    @LastModifiedDate
    protected Date updatedDt = clock.getCurrentDate();

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedDt() {
        return createdDt;
    }

    public void setCreatedDt(Date createdDt) {
        this.createdDt = createdDt;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Date getUpdatedDt() {
        return updatedDt;
    }

    public void setUpdatedDt(Date updatedDt) {
        this.updatedDt = updatedDt;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getCurrentAuditor() {
        Subject subject = SecurityUtils.getSubject();
        if(subject == null) {
            return "admin";
        }
        return subject.getPrincipal().toString();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
