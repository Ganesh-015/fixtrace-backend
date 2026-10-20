package com.fixtrace;

import com.fixtrace.dto.auth.LoginRequest;
import com.fixtrace.dto.auth.RegisterRequest;
import com.fixtrace.dto.equipment.CreateEquipmentRequest;
import com.fixtrace.dto.equipment.UpdateEquipmentRequest;
import com.fixtrace.dto.lab.CreateLabRequest;
import com.fixtrace.enums.EquipmentCategory;
import com.fixtrace.enums.EquipmentStatus;
import com.fixtrace.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class EquipmentControllerIntegrationTest extends BaseIntegrationTest {

    private String adminToken;
    private String studentToken;
    private Long labId;

    @BeforeEach
    @Override
    public void cleanupDatabase() {
        super.cleanupDatabase();

        try {
            // Register & login Admin
            mockMvc.perform(post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(new RegisterRequest("Admin", "admin@eqtest.com", "Admin@123", null, Role.ADMIN))));
            MvcResult adminLogin = mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(new LoginRequest("admin@eqtest.com", "Admin@123")))).andReturn();
            adminToken = objectMapper.readTree(adminLogin.getResponse().getContentAsString()).get("data").get("accessToken").asText();

            // Register & login Student
            mockMvc.perform(post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(new RegisterRequest("Student", "student@eqtest.com", "Student@123", null, Role.STUDENT))));
            MvcResult studentLogin = mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(new LoginRequest("student@eqtest.com", "Student@123")))).andReturn();
            studentToken = objectMapper.readTree(studentLogin.getResponse().getContentAsString()).get("data").get("accessToken").asText();

            // Create Lab
            MvcResult labRes = mockMvc.perform(post("/api/labs")
                    .header("Authorization", "Bearer " + adminToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(new CreateLabRequest("LAB-CS-1", "CS Lab 1", "Block A", "1", "101", null)))).andReturn();
            labId = objectMapper.readTree(labRes.getResponse().getContentAsString()).get("data").get("id").asLong();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("POST /api/equipment - Admin can register equipment successfully")
    void testAdminCreateEquipment() throws Exception {
        CreateEquipmentRequest request = new CreateEquipmentRequest(
                "EQ-001", "Workstation Pro", EquipmentCategory.COMPUTER, "Dell", "Precision",
                "SN-10099", LocalDate.now(), LocalDate.now().plusYears(3), EquipmentStatus.WORKING,
                labId, "Developer workstation"
        );

        mockMvc.perform(post("/api/equipment")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.assetCode", is("EQ-001")))
                .andExpect(jsonPath("$.data.category", is("COMPUTER")))
                .andExpect(jsonPath("$.data.status", is("WORKING")));
    }

    @Test
    @DisplayName("POST /api/equipment - Student cannot register equipment (403 Forbidden)")
    void testStudentCannotCreateEquipment() throws Exception {
        CreateEquipmentRequest request = new CreateEquipmentRequest(
                "EQ-002", "Monitor", EquipmentCategory.MONITOR, "HP", "24F",
                "SN-HP-201", LocalDate.now(), LocalDate.now().plusYears(1), EquipmentStatus.WORKING,
                labId, null
        );

        mockMvc.perform(post("/api/equipment")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status", is(403)));
    }

    @Test
    @DisplayName("GET /api/equipment - Filter equipment by status, category, and search query")
    void testFilterAndSearchEquipment() throws Exception {
        mockMvc.perform(post("/api/equipment")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new CreateEquipmentRequest(
                        "EQ-SRCH-1", "Laser Printer", EquipmentCategory.PRINTER, "HP", "LaserJet",
                        "SN-PRN-001", LocalDate.now(), LocalDate.now().plusYears(1), EquipmentStatus.WORKING, labId, null))));

        mockMvc.perform(post("/api/equipment")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new CreateEquipmentRequest(
                        "EQ-SRCH-2", "Projector High Lumens", EquipmentCategory.PROJECTOR, "Epson", "EB-X",
                        "SN-PRJ-002", LocalDate.now(), LocalDate.now().plusYears(1), EquipmentStatus.WORKING, labId, null))));

        // Search for 'Printer'
        mockMvc.perform(get("/api/equipment?search=printer")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(1)))
                .andExpect(jsonPath("$.data.content[0].assetCode", is("EQ-SRCH-1")));

        // Filter by category PROJECTOR
        mockMvc.perform(get("/api/equipment?category=PROJECTOR")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(1)))
                .andExpect(jsonPath("$.data.content[0].assetCode", is("EQ-SRCH-2")));
    }

    @Test
    @DisplayName("DELETE /api/equipment/{id} - Retires equipment without deleting database records")
    void testRetireEquipment() throws Exception {
        MvcResult eqRes = mockMvc.perform(post("/api/equipment")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new CreateEquipmentRequest(
                        "EQ-RET-1", "Old Laptop", EquipmentCategory.COMPUTER, "Lenovo", "T480",
                        "SN-LEN-099", LocalDate.now().minusYears(5), LocalDate.now().minusYears(2), EquipmentStatus.WORKING, labId, null)))).andReturn();
        Long eqId = objectMapper.readTree(eqRes.getResponse().getContentAsString()).get("data").get("id").asLong();

        mockMvc.perform(delete("/api/equipment/" + eqId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));

        // Verify status changed to RETIRED
        mockMvc.perform(get("/api/equipment/" + eqId)
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status", is("RETIRED")));
    }
}
