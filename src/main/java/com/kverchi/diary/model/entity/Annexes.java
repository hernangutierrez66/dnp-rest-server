package com.kverchi.diary.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.Date;

@Entity
@Table(name="anexos")
@Data
@EqualsAndHashCode(callSuper = true)
public class Annexes extends EntityModel implements Serializable {



        public static final DateFormat DATE_FORMAT= new SimpleDateFormat("yyyy-mm-dd");

        @ManyToOne(fetch = FetchType.EAGER)
        @JoinColumn(name = "trace_id")
        private TraceChangeValue traceChangeValue;


        @Column(name = "description")
        private String description;

        @Column(name = "fecha_inicial")
        @Temporal(TemporalType.DATE)
        private Date dateStart;

        @Column(name = "fecha_final")
        @Temporal(TemporalType.DATE)
        private Date dateFinal;

        @Column(name = "state")
        private int state;

        @Column(name = "created_at")
        @CreatedDate
        private ZonedDateTime createdDate;

        @Column(name = "updated_at")
        @LastModifiedDate
        private ZonedDateTime modifiedDate;

        public Annexes(TraceChangeValue traceChangeValue, String description, Date dateStart, Date dateFinal, int state, ZonedDateTime createdDate) {
                this.traceChangeValue = traceChangeValue;
                this.description = description;
                this.dateStart = dateStart;
                this.dateFinal = dateFinal;
                this.state = state;
                this.createdDate = createdDate;
                //this.modifiedDate = modifiedDate;
        }
}
