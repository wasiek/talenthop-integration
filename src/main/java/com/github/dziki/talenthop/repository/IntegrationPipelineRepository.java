package com.github.dziki.talenthop.repository;

import com.github.dziki.talenthop.model.IntegrationPipeline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IntegrationPipelineRepository extends JpaRepository<IntegrationPipeline, Long> {

    Optional<IntegrationPipeline> findByName(String name);

    boolean existsByName(String name);

    List<IntegrationPipeline> findBySourceId(Long sourceId);

    List<IntegrationPipeline> findByIsActiveTrue();
}
