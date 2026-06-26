package com.example.med_office.repository;

import com.example.med_office.entity.IntegrationChannel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface IntegrationChannelRepository extends JpaRepository<IntegrationChannel, String> {
    Optional<IntegrationChannel> findByProviderId(String providerId);
}
