package com.fixtrace;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fixtrace.repository.*;
import com.fixtrace.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected NotificationRepository notificationRepository;

    @Autowired
    protected RepairRecordRepository repairRecordRepository;

    @Autowired
    protected IssueRepository issueRepository;

    @Autowired
    protected EquipmentRepository equipmentRepository;

    @Autowired
    protected LabRepository labRepository;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected JwtService jwtService;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @BeforeEach
    public void cleanupDatabase() {
        notificationRepository.deleteAll();
        repairRecordRepository.deleteAll();
        issueRepository.deleteAll();
        equipmentRepository.deleteAll();
        labRepository.deleteAll();
        userRepository.deleteAll();
    }
}
