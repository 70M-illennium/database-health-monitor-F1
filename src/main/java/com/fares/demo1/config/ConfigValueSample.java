package com.fares.demo1.config;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * One tracked GLOBAL VARIABLE and its value, belonging to a {@link ConfigSnapshotEntity}.
 * Unique on (snapshot, name) - defensive: nothing today inserts the same variable twice
 * under one header, but nothing stopped it either before this constraint existed.
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"snapshot_id", "name"}))
@Getter
@Setter
public class ConfigValueSample {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @ManyToOne(optional = false)
    private ConfigSnapshotEntity snapshot;

    private String name;
    private String value;
}
