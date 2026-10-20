package com.fixtrace;

import com.fixtrace.dto.auth.LoginRequest;
import com.fixtrace.dto.auth.RegisterRequest;
import com.fixtrace.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class DashboardControllerIntegrationTest extends BaseIntegrationTest {

    private String adminToken;
    private String studentToken;

    @BeforeEach
    @Override
    public void cleanupDatabase() {
        super.cleanupDatabase();

        try {
            // Register Admin
            mockMvc.perform(post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(new RegisterRequest("Admin", "admin@dash.com", "Admin@123", null, Role.ADMIN))));
            MvcResult adminLogin = mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(new LoginRequest("admin@dash.com", "Admin@123")))).andReturn();
            adminToken = objectMapper.readTree(adminLogin.getResponse().getContentAsString()).get("data").get("accessToken").asText();

            // Register Student
            mockMvc.perform(post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(new RegisterRequest("Student", "student@dash.com", "Student@123", null, Role.STUDENT))));
            MvcResult studentLogin = mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(new LoginRequest("student@dash.com", "Student@123")))).andReturn();
            studentToken = objectMapper.readTree(studentLogin.getResponse().getContentAsString()).get("data").get("accessToken").asText();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("GET /api/dashboard/summary - Admin can view summary metrics")
    void testDashboardSummary() throws Exception {
        mockMvc.perform(get("/api/dashboard/summary")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.totalEquipment", notNullValue()))
                .andExpect(jsonPath("$.data.totalIssues", notNullValue()))
                .andExpect(jsonPath("$.data.openIssues", notNullValue()));
    }

    @Test
    @DisplayName("GET /api/dashboard/summary - Student access is forbidden (403)")
    void testDashboardStudentForbidden() throws Exception {
        mockMvc.perform(get("/api/dashboard/summary")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status", is(403)));
    }

    @Test
    @DisplayName("GET /api/dashboard/issues-by-status - Returns chart data grouped by status")
    void testIssuesByStatus() throws Exception {
        mockMvc.perform(get("/api/dashboard/issues-by-status")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", notNullValue()));
    }

    @Test
    @DisplayName("GET /api/dashboard/issues-by-priority - Returns chart data grouped by priority")
    void testIssuesByPriority() throws Exception {
        mockMvc.perform(get("/api/dashboard/issues-by-priority")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", notNullValue()));
    }

    @Test
    @DisplayName("GET /api/dashboard/failure-prone-equipment - Returns ranked equipment list")
    void testFailureProneEquipment() throws Exception {
        mockMvc.perform(get("/api/dashboard/failure-prone-equipment?limit=5")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", notNullValue()));
    }
}
