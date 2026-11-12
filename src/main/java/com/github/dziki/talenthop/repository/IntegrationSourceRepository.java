package com.github.dziki.talenthop.repository;

import com.github.dziki.talenthop.model.IntegrationSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IntegrationSourceRepository extends JpaRepository<IntegrationSource, Long> {

    Optional<IntegrationSource> findByName(String name);

    Optional<IntegrationSource> findByApiKey(String apiKey);

    boolean existsByName(String name);
}
