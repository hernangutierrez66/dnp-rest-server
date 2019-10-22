package com.kverchi.diary.model.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Date;

@Data
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name="notifications")
public class Notification extends NamedEntityModel implements Serializable {

    @Column(name="user_id")
    @CreatedBy
    private int userId;

    @Column(name = "observation")
    private String observation;

    @Column(name = "aditional_information")
    private String aditionalInformation;

    @Column(name = "city")
    private int city;

    @Column(name = "register_id")
    private int registerId;

    @Column(name = "table_name")
    private String tableName;

    @Column(name = "state_change")
    private int state_change;

    @Column(name = "state")
    private int state;

    @Column(name = "start_date")
    private Date startDate;

    @Column(name = "end_date")
    private Date endDate;

    @Column(name = "notify_control_boss")
    private boolean notifyControlBoss;

    @Column(name = "created_at")
    @CreatedDate
    private Date created_at;

    @Column(name = "updated_at")
    @LastModifiedDate
    private Date updated_at;

    public Notification(String name, int userId, String observation, String aditionalInformation, int city, int registerId, String tableName, int state_change, int state, Date startDate, Date endDate, boolean notifyControlBoss) {
        this.name = name;
        this.userId = userId;
        this.observation = observation;
        this.aditionalInformation = aditionalInformation;
        this.city = city;
        this.registerId = registerId;
        this.tableName = tableName;
        this.state_change = state_change;
        this.state = state;
        this.startDate = startDate;
        this.endDate = endDate;
        this.notifyControlBoss = notifyControlBoss;
    }
}
