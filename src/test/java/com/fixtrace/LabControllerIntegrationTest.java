package com.fixtrace;

import com.fixtrace.dto.auth.LoginRequest;
import com.fixtrace.dto.auth.RegisterRequest;
import com.fixtrace.dto.lab.CreateLabRequest;
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

public class LabControllerIntegrationTest extends BaseIntegrationTest {

    private String adminToken;
    private String studentToken;

    @BeforeEach
    @Override
    public void cleanupDatabase() {
        super.cleanupDatabase();

        try {
            // Register & login Admin
            mockMvc.perform(post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(new RegisterRequest("Admin User", "admin@labtest.com", "Admin@123", null, Role.ADMIN))));

            MvcResult adminLogin = mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(new LoginRequest("admin@labtest.com", "Admin@123"))))
                    .andReturn();
            adminToken = objectMapper.readTree(adminLogin.getResponse().getContentAsString()).get("data").get("accessToken").asText();

            // Register & login Student
            mockMvc.perform(post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(new RegisterRequest("Student User", "student@labtest.com", "Student@123", null, Role.STUDENT))));

            MvcResult studentLogin = mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(new LoginRequest("student@labtest.com", "Student@123"))))
                    .andReturn();
            studentToken = objectMapper.readTree(studentLogin.getResponse().getContentAsString()).get("data").get("accessToken").asText();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("POST /api/labs - Admin can create lab successfully")
    void testAdminCreateLab() throws Exception {
        CreateLabRequest request = new CreateLabRequest("LAB-CS-101", "Computer Science Lab", "Science Block", "1", "101", "Main CS lab");

        mockMvc.perform(post("/api/labs")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.labCode", is("LAB-CS-101")))
                .andExpect(jsonPath("$.data.name", is("Computer Science Lab")));
    }

    @Test
    @DisplayName("POST /api/labs - Student role cannot create lab (403 Forbidden)")
    void testStudentCannotCreateLab() throws Exception {
        CreateLabRequest request = new CreateLabRequest("LAB-BIO-201", "Biology Lab", "Bio Block", "2", "201", "Bio Lab");

        mockMvc.perform(post("/api/labs")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status", is(403)));
    }

    @Test
    @DisplayName("GET /api/labs - Authenticated user can view all labs")
    void testGetAllLabs() throws Exception {
        mockMvc.perform(post("/api/labs")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new CreateLabRequest("LAB-1", "Lab One", "Block A", "1", "101", null))));

        mockMvc.perform(get("/api/labs")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].labCode", is("LAB-1")));
    }
}
