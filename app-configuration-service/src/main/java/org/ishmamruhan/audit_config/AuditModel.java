package org.ishmamruhan.audit_config;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditModel<U> {


    @CreatedBy
    protected U createdBy;

    @LastModifiedBy
    protected U lastModifiedBy;

    @CreatedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    protected LocalDateTime creationDate;

    @LastModifiedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    protected LocalDateTime lastModifiedDate;

    @JsonFormat(shape=JsonFormat.Shape.NUMBER, pattern="s")
    public Long getCreationDateTimeStamp() {
        if (creationDate == null) {
            return 0L;
        } else {
            return this.creationDate.toEpochSecond(OffsetDateTime.now().getOffset());
        }
    }

    @JsonFormat(shape=JsonFormat.Shape.NUMBER, pattern="s")
    public Long getLastModifiedDateTimeStamp() {
        if (lastModifiedDate == null) return 0L;
        return this.lastModifiedDate.toEpochSecond(OffsetDateTime.now().getOffset());
    }

    public U getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(U createdBy) {
        this.createdBy = createdBy;
    }

    public U getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(U lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getCreatedDate(){
        DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm aa");

        String date = dateFormat.format(Date.from(this.creationDate.toInstant(ZoneOffset.UTC)));

        return date.substring(0,11)+"  at  "+date.substring(12);
    }
}
