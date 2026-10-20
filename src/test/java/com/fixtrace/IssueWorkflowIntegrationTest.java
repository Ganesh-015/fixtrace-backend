package com.fixtrace;

import com.fasterxml.jackson.databind.JsonNode;
import com.fixtrace.dto.auth.LoginRequest;
import com.fixtrace.dto.auth.RegisterRequest;
import com.fixtrace.dto.equipment.CreateEquipmentRequest;
import com.fixtrace.dto.issue.AssignIssueRequest;
import com.fixtrace.dto.issue.CreateIssueRequest;
import com.fixtrace.dto.issue.UpdateIssueStatusRequest;
import com.fixtrace.dto.lab.CreateLabRequest;
import com.fixtrace.dto.repair.RepairRecordRequest;
import com.fixtrace.enums.EquipmentCategory;
import com.fixtrace.enums.EquipmentStatus;
import com.fixtrace.enums.IssuePriority;
import com.fixtrace.enums.IssueStatus;
import com.fixtrace.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class IssueWorkflowIntegrationTest extends BaseIntegrationTest {

    private String adminToken;
    private String techToken;
    private Long techId;
    private String studentToken;
    private Long labId;
    private Long equipmentId;

    @BeforeEach
    @Override
    public void cleanupDatabase() {
        super.cleanupDatabase();

        try {
            // 1. Create Admin
            mockMvc.perform(post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(new RegisterRequest("Admin", "admin@wf.com", "Admin@123", null, Role.ADMIN))));
            MvcResult adminLogin = mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(new LoginRequest("admin@wf.com", "Admin@123")))).andReturn();
            adminToken = objectMapper.readTree(adminLogin.getResponse().getContentAsString()).get("data").get("accessToken").asText();

            // 2. Create Technician
            mockMvc.perform(post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(new RegisterRequest("Tech Bob", "tech.bob@wf.com", "Tech@123", null, Role.TECHNICIAN))));
            MvcResult techLogin = mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(new LoginRequest("tech.bob@wf.com", "Tech@123")))).andReturn();
            JsonNode techData = objectMapper.readTree(techLogin.getResponse().getContentAsString()).get("data");
            techToken = techData.get("accessToken").asText();
            techId = techData.get("userId").asLong();

            // 3. Create Student
            mockMvc.perform(post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(new RegisterRequest("Student Sam", "sam@wf.com", "Sam@123", null, Role.STUDENT))));
            MvcResult studentLogin = mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(new LoginRequest("sam@wf.com", "Sam@123")))).andReturn();
            studentToken = objectMapper.readTree(studentLogin.getResponse().getContentAsString()).get("data").get("accessToken").asText();

            // 4. Create Lab
            MvcResult labResult = mockMvc.perform(post("/api/labs")
                    .header("Authorization", "Bearer " + adminToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(new CreateLabRequest("LAB-CS-101", "Computer Lab", "Block A", "1", "101", "CS")))).andReturn();
            labId = objectMapper.readTree(labResult.getResponse().getContentAsString()).get("data").get("id").asLong();

            // 5. Create Equipment
            CreateEquipmentRequest eqReq = new CreateEquipmentRequest(
                    "EQ-CS-001", "Dell OptiPlex", EquipmentCategory.COMPUTER, "Dell", "7090",
                    "SN-OPT-1001", LocalDate.now().minusMonths(6), LocalDate.now().plusMonths(18),
                    EquipmentStatus.WORKING, labId, "Lab Workstation"
            );
            MvcResult eqResult = mockMvc.perform(post("/api/equipment")
                    .header("Authorization", "Bearer " + adminToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(eqReq))).andReturn();
            equipmentId = objectMapper.readTree(eqResult.getResponse().getContentAsString()).get("data").get("id").asLong();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Complete Issue Lifecycle: Report -> Assign -> In Progress -> Repair Record -> Resolve -> Close")
    void testCompleteIssueLifecycle() throws Exception {
        // Step 1: Student reports issue
        CreateIssueRequest reportReq = new CreateIssueRequest(equipmentId, "Mouse and keyboard unresponsive", "USB ports not detecting peripherals", IssuePriority.HIGH);
        MvcResult reportRes = mockMvc.perform(post("/api/issues")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reportReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.issueCode", startsWith("FT-")))
                .andExpect(jsonPath("$.data.status", is("REPORTED")))
                .andExpect(jsonPath("$.data.reportedByName", is("Student Sam")))
                .andReturn();

        Long issueId = objectMapper.readTree(reportRes.getResponse().getContentAsString()).get("data").get("id").asLong();

        // Step 2: Admin assigns technician
        mockMvc.perform(put("/api/issues/" + issueId + "/assign")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AssignIssueRequest(techId))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status", is("ASSIGNED")))
                .andExpect(jsonPath("$.data.assignedTechnicianName", is("Tech Bob")));

        // Verify equipment is now UNDER_REPAIR
        mockMvc.perform(get("/api/equipment/" + equipmentId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(jsonPath("$.data.status", is("UNDER_REPAIR")));

        // Step 3: Technician updates status to IN_PROGRESS
        mockMvc.perform(put("/api/issues/" + issueId + "/status")
                        .header("Authorization", "Bearer " + techToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UpdateIssueStatusRequest(IssueStatus.IN_PROGRESS, "Diagnosing motherboard"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status", is("IN_PROGRESS")));

        // Step 4: Technician creates repair record
        RepairRecordRequest repairReq = new RepairRecordRequest(
                "Front USB header cable loose",
                "Reseated USB 3.0 motherboard header connector firmly",
                "None",
                "Tested with USB flash drive and optical mouse, working properly",
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now()
        );

        mockMvc.perform(post("/api/issues/" + issueId + "/repair-records")
                        .header("Authorization", "Bearer " + techToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(repairReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.technicianName", is("Tech Bob")))
                .andExpect(jsonPath("$.data.diagnosis", containsString("USB header")));

        // Step 5: Technician marks issue RESOLVED
        mockMvc.perform(put("/api/issues/" + issueId + "/status")
                        .header("Authorization", "Bearer " + techToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UpdateIssueStatusRequest(IssueStatus.RESOLVED, "Repaired and verified"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status", is("RESOLVED")))
                .andExpect(jsonPath("$.data.resolvedAt", notNullValue()));

        // Verify equipment returned to WORKING status
        mockMvc.perform(get("/api/equipment/" + equipmentId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(jsonPath("$.data.status", is("WORKING")));

        // Step 6: Student closes resolved issue
        mockMvc.perform(put("/api/issues/" + issueId + "/close")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status", is("CLOSED")));

        // Step 7: Verify maintenance history contains this issue and repair record
        mockMvc.perform(get("/api/equipment/" + equipmentId + "/maintenance-history")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalFailures", is(1)))
                .andExpect(jsonPath("$.data.totalRepairs", is(1)))
                .andExpect(jsonPath("$.data.issues[0].repairRecords", hasSize(1)));
    }

    @Test
    @DisplayName("Invalid status transition: REPORTED cannot jump directly to RESOLVED")
    void testInvalidStatusTransition() throws Exception {
        CreateIssueRequest reportReq = new CreateIssueRequest(equipmentId, "Screen black", "No display", IssuePriority.LOW);
        MvcResult reportRes = mockMvc.perform(post("/api/issues")
                .header("Authorization", "Bearer " + studentToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reportReq))).andReturn();
        Long issueId = objectMapper.readTree(reportRes.getResponse().getContentAsString()).get("data").get("id").asLong();

        // Attempting to jump directly from REPORTED to RESOLVED
        mockMvc.perform(put("/api/issues/" + issueId + "/status")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UpdateIssueStatusRequest(IssueStatus.RESOLVED, "Invalid skip"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Invalid Status Transition")));
    }
}
