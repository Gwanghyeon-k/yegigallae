package com.umc.yeogi_gal_lae.global.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {

    @CreatedDate
    @JsonFormat(timezone = "Asia/Seoul")
    private LocalDateTime createdAt = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);

    @LastModifiedDate
    @JsonFormat(timezone = "Asia/Seoul")
    private LocalDateTime updatedAt = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);

    @Column(name = "deleted_at")
    @JsonFormat(timezone = "Asia/Seoul")
    private LocalDateTime deletedAt;

}
