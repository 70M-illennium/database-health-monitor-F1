package com.fares.demo1.config;

import com.fares.demo1.config.ConfigSnapshotEntity;
import com.fares.demo1.config.ConfigValueSample;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConfigValueSampleRepo extends JpaRepository<ConfigValueSample, Long> {

    List<ConfigValueSample> findBySnapshotOrderByName(ConfigSnapshotEntity snapshot);
}
