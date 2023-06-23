package io.github.jendker.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import java.time.LocalDateTime;

@Embeddable
class Audit {

    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;

    @PrePersist
    void PrePersist(){
        createdOn=LocalDateTime.now();
    }

    @PreUpdate
    void PreMerge(){
        updatedOn = LocalDateTime.now();
    }
}
