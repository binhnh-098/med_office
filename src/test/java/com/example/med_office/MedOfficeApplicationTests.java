package com.example.med_office;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MedOfficeApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads() {
    }

    @Test
    void protectedApiReturnsStructuredUnauthorizedError() throws Exception {
        mockMvc.perform(get("/api/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").value("Authentication is required"));
    }

    @Test
    void apiLoginAuthenticatesConfiguredUser() throws Exception {
        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "reception",
                                  "password": "clinic123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.data.username").value("reception"))
                .andExpect(jsonPath("$.data.fullName").value("Reception Staff"))
                .andExpect(jsonPath("$.data.status").value("ACTIVE"))
                .andExpect(jsonPath("$.data.email").value("reception@med-office.local"))
                .andExpect(jsonPath("$.data.phoneNumber").value("0901234567"))
                .andExpect(jsonPath("$.data.departmentId").value(1))
                .andExpect(jsonPath("$.data.positionId").value(1))
                .andExpect(jsonPath("$.data.positionName").value("Le tan"))
                .andExpect(jsonPath("$.data.lastLoginAt").exists());
    }

    @Test
    void apiLoginRejectsInvalidPassword() throws Exception {
        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "reception",
                                  "password": "wrong-password"
                                }
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").value("Invalid username or password"));
    }

    @Test
    void apiLoginRejectsInvalidRequestBody() throws Exception {
        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "reception"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.data.password").value("Password must not be blank"));
    }

    @Test
    void swaggerUiIsPublic() throws Exception {
        mockMvc.perform(get("/swagger-ui.html"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void apiLogoutClearsCurrentSession() throws Exception {
        MvcResult loginResult = mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "reception",
                                  "password": "clinic123"
                                }
                                """))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpSession session = (MockHttpSession) loginResult.getRequest().getSession(false);

        mockMvc.perform(get("/api/me").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("reception"))
                .andExpect(jsonPath("$.data.phoneNumber").value("0901234567"))
                .andExpect(jsonPath("$.data.departmentId").value(1))
                .andExpect(jsonPath("$.data.positionId").value(1))
                .andExpect(jsonPath("$.data.positionName").value("Le tan"));

        mockMvc.perform(post("/api/logout").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Logout successful"));

        mockMvc.perform(get("/api/me").session(session))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401));
    }

    @Test
    void createCongVanDenRequiresAuthenticatedSessionAndPersistsData() throws Exception {
        MvcResult loginResult = mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "reception",
                                  "password": "clinic123"
                                }
                                """))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpSession session = (MockHttpSession) loginResult.getRequest().getSession(false);

        mockMvc.perform(post("/api/cong-van-den")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "soCongVan": "130/SYT",
                                  "soDen": "456",
                                  "tieuDe": "Huong dan phong chong dich",
                                  "noiDungTomTat": "Noi dung...",
                                  "donViGuiId": 1,
                                  "nguoiKy": "Nguyen Van A",
                                  "ngayVanBan": "2026-04-01",
                                  "ngayNhan": "2026-04-02",
                                  "mucDoKhan": "KHAN",
                                  "mucDoMat": "THUONG",
                                  "nguonNhan": "EMAIL"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Tao cong van den thanh cong"))
                .andExpect(jsonPath("$.data.soCongVan").value("130/SYT"))
                .andExpect(jsonPath("$.data.soDen").value("456"))
                .andExpect(jsonPath("$.data.donViGuiId").value(1))
                .andExpect(jsonPath("$.data.donViGui").value("So Y Te"))
                .andExpect(jsonPath("$.data.trangThai").value("MOI_TIEP_NHAN"));
    }

    @Test
    void createCongVanDenAcceptsSnakeCasePayload() throws Exception {
        MvcResult loginResult = mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "reception",
                                  "password": "clinic123"
                                }
                                """))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpSession session = (MockHttpSession) loginResult.getRequest().getSession(false);

        mockMvc.perform(post("/api/cong-van-den")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "so_cong_van": "124/SYT",
                                  "so_den": "459",
                                  "tieu_de": "Huong dan kham benh",
                                  "noi_dung_tom_tat": "Noi dung huong dan...",
                                  "don_vi_gui_id": 1,
                                  "nguoi_ky": "Nguyen Van A",
                                  "ngay_van_ban": "2026-04-06",
                                  "ngay_nhan": "2026-04-07",
                                  "muc_do_khan": "KHAN",
                                  "muc_do_mat": "THUONG",
                                  "nguon_nhan": "EMAIL"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.soCongVan").value("124/SYT"))
                .andExpect(jsonPath("$.data.soDen").value("459"))
                .andExpect(jsonPath("$.data.donViGuiId").value(1));
    }

    @Test
    void createCongVanDenRejectsNonPositiveDonViGuiId() throws Exception {
        MvcResult loginResult = mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "reception",
                                  "password": "clinic123"
                                }
                                """))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpSession session = (MockHttpSession) loginResult.getRequest().getSession(false);

        mockMvc.perform(post("/api/cong-van-den")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "soCongVan": "130/SYT",
                                  "soDen": "460",
                                  "tieuDe": "Du lieu sai",
                                  "noiDungTomTat": "Noi dung...",
                                  "donViGuiId": 0,
                                  "nguoiKy": "Nguyen Van A",
                                  "ngayVanBan": "2026-04-08",
                                  "ngayNhan": "2026-04-09",
                                  "mucDoKhan": "KHAN",
                                  "mucDoMat": "THUONG",
                                  "nguonNhan": "EMAIL"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.data.donViGuiId").value("Don vi gui id must be greater than 0"));
    }

    @Test
    void createCongVanDenRejectsTooLongSoCongVan() throws Exception {
        MvcResult loginResult = mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "reception",
                                  "password": "clinic123"
                                }
                                """))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpSession session = (MockHttpSession) loginResult.getRequest().getSession(false);

        String tooLongSoCongVan = "A".repeat(101);

        mockMvc.perform(post("/api/cong-van-den")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "soCongVan": "%s",
                                  "soDen": "461",
                                  "tieuDe": "Du lieu qua dai",
                                  "noiDungTomTat": "Noi dung...",
                                  "donViGuiId": 1,
                                  "nguoiKy": "Nguyen Van A",
                                  "ngayVanBan": "2026-04-08",
                                  "ngayNhan": "2026-04-09",
                                  "mucDoKhan": "KHAN",
                                  "mucDoMat": "THUONG",
                                  "nguonNhan": "EMAIL"
                                }
                                """.formatted(tooLongSoCongVan)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.data.soCongVan").value("So cong van must not exceed 100 characters"));
    }

    @Test
    void createCongVanDenRejectsDuplicateSoCongVan() throws Exception {
        MvcResult loginResult = mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "reception",
                                  "password": "clinic123"
                                }
                                """))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpSession session = (MockHttpSession) loginResult.getRequest().getSession(false);

        mockMvc.perform(post("/api/cong-van-den")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "soCongVan": "131/SYT",
                                  "soDen": "462",
                                  "tieuDe": "Van ban goc",
                                  "noiDungTomTat": "Noi dung...",
                                  "donViGuiId": 1,
                                  "nguoiKy": "Nguyen Van A",
                                  "ngayVanBan": "2026-04-08",
                                  "ngayNhan": "2026-04-09",
                                  "mucDoKhan": "KHAN",
                                  "mucDoMat": "THUONG",
                                  "nguonNhan": "EMAIL"
                                }
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/cong-van-den")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "soCongVan": "131/SYT",
                                  "soDen": "463",
                                  "tieuDe": "Van ban bi trung",
                                  "noiDungTomTat": "Noi dung...",
                                  "donViGuiId": 1,
                                  "nguoiKy": "Nguyen Van A",
                                  "ngayVanBan": "2026-04-08",
                                  "ngayNhan": "2026-04-09",
                                  "mucDoKhan": "KHAN",
                                  "mucDoMat": "THUONG",
                                  "nguonNhan": "EMAIL"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(409))
                .andExpect(jsonPath("$.message").value("So cong van da ton tai"));
    }
}
