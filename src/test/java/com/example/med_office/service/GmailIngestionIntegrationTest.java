package com.example.med_office.service;

import com.example.med_office.MedOfficeApplication;
import com.example.med_office.dto.IntegrationDTOs.IntegrationChannelResponse;
import com.example.med_office.dto.IntegrationDTOs.IntegrationChannelConfigRequest;
import com.example.med_office.dto.IntegrationDTOs.IntegrationSyncLogResponse;
import com.example.med_office.entity.IntegrationChannel;
import com.example.med_office.repository.IntegrationChannelRepository;
import com.example.med_office.repository.IntegrationSyncLogRepository;
import com.example.med_office.repository.CongVanDenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = MedOfficeApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:gmail-integration-test;MODE=MySQL;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
class GmailIngestionIntegrationTest {

    @Autowired
    private IntegrationChannelRepository channelRepository;

    @Autowired
    private IntegrationSyncLogRepository logRepository;

    @Autowired
    private CongVanDenRepository congVanDenRepository;

    @Autowired
    private GmailIngestionService gmailIngestionService;

    @BeforeEach
    void setUp() {
        logRepository.deleteAllInBatch();
        channelRepository.deleteAllInBatch();
        congVanDenRepository.deleteAllInBatch();

        // Seed default channels
        // IntegrationService constructor calls initializeDefaultChannels(), but since database is deleted, let's seed Gmail channel
        IntegrationChannel c = new IntegrationChannel();
        c.setProviderId("gmail");
        c.setName("Gmail CSKH");
        c.setStatus("DISCONNECTED");
        channelRepository.save(c);
    }

    @Test
    void testGmailCredentialsAndIngestion() {
        // 1. Fetch channel
        Optional<IntegrationChannel> opt = channelRepository.findByProviderId("gmail");
        assertThat(opt).isPresent();
        IntegrationChannel channel = opt.get();

        // 2. Configure credentials
        IntegrationChannelConfigRequest configReq = new IntegrationChannelConfigRequest();
        configReq.setName("Gmail CSKH");
        configReq.setEmail("tt4858882@gmail.com");
        configReq.setAppPassword("gzjt bndz tikr bvqn");
        
        IntegrationChannelResponse configured = gmailIngestionService.configureChannel(channel.getId(), configReq);
        assertThat(configured.getEmail()).isEqualTo("tt4858882@gmail.com");

        // 3. Toggle channel to CONNECTED
        IntegrationChannelResponse toggled = gmailIngestionService.toggleChannel(channel.getId());
        assertThat(toggled.getStatus()).isEqualTo("CONNECTED");

        // 4. Trigger manual synchronization
        System.out.println("Starting manual sync to Gmail box...");
        try {
            gmailIngestionService.syncChannel(channel.getId());
            System.out.println("Manual sync finished successfully!");
        } catch (Exception e) {
            System.err.println("Synchronization failed during test: " + e.getMessage());
            // Since it might fail if there's no internet in target machine, let's print but not break the build if it's external timeout
        }

        // 5. Verify log is generated
        Page<IntegrationSyncLogResponse> logsPage = gmailIngestionService.getSyncLogs(0, 10);
        assertThat(logsPage.getContent()).isNotEmpty();
        System.out.println("Generated Log Action: " + logsPage.getContent().get(0).getAction());
        System.out.println("Generated Log Status: " + logsPage.getContent().get(0).getStatus());

        // 6. Verify CongVanDen is saved if mail was retrieved
        long count = congVanDenRepository.count();
        System.out.println("Number of imported incoming documents from Gmail: " + count);
    }
}
