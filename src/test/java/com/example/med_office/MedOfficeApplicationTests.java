package com.example.med_office;

import com.example.med_office.dto.RowboatChatResponse;
import com.example.med_office.dto.RowboatMessage;
import com.example.med_office.repository.DoctorMealDishRepository;
import com.example.med_office.service.RowboatService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MedOfficeApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DoctorMealDishRepository doctorMealDishRepository;

    @MockitoBean
    private RowboatService rowboatService;

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
    void createDoctorMealRegistrationNormalizesRequesterAndPersistsData() throws Exception {
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
        long dishCountBefore = doctorMealDishRepository.count();

        mockMvc.perform(post("/api/doctor-meals/registrations")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "week": {
                                    "year": 2026,
                                    "number": 20
                                  },
                                  "requester": "legacy-client-value",
                                  "items": [
                                    {
                                      "dayOfWeek": "Thu 2",
                                      "mealId": "lunch",
                                      "dishes": [
                                        {
                                          "dishId": "dish-1",
                                          "name": "Com tam",
                                          "unitPrice": 40000,
                                          "quantity": 1
                                        }
                                      ]
                                    }
                                  ],
                                  "summary": {
                                    "totalAmount": 40000
                                  }
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Luu dang ky thanh cong"))
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.weekYear").value(2026))
                .andExpect(jsonPath("$.data.weekNumber").value(20));

        long dishCountAfter = doctorMealDishRepository.count();
        assertEquals(dishCountBefore, dishCountAfter);
    }

    @Test
    void createDoctorMealRegistrationRejectsInvalidWeek() throws Exception {
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

        mockMvc.perform(post("/api/doctor-meals/registrations")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "week": {
                                    "year": 2026
                                  },
                                  "items": []
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("week.number must be a positive integer"));
    }

    @Test
    void linkedHoSoNhanVienProvidesProfileInfoInLoginResponse() throws Exception {
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

        mockMvc.perform(post("/api/ho-so-nhan-vien")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nguoiDungId": 2,
                                  "code": "HSNV-ADMIN",
                                  "name": "Nguyen Van A",
                                  "email": "nguyenvana@example.com",
                                  "phone": "0900000000",
                                  "active": true
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nguoiDungId").value(2))
                .andExpect(jsonPath("$.data.code").value("HSNV-ADMIN"));

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "admin",
                                  "password": "clinic123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("admin"))
                .andExpect(jsonPath("$.data.hoSoNhanVienId").exists())
                .andExpect(jsonPath("$.data.fullName").value("Nguyen Van A"))
                .andExpect(jsonPath("$.data.email").value("nguyenvana@example.com"))
                .andExpect(jsonPath("$.data.phoneNumber").value("0900000000"));
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
                                  "phongBanXuLyId": 10,
                                  "nguoiXuLyId": 2,
                                  "nguonNhan": "EMAIL",
                                  "hanXuLy": "2026-04-10",
                                  "doKhanXuLy": "CAO",
                                  "loaiVanBanId": 3,
                                  "linhVucId": 4,
                                  "hoSoId": 5,
                                  "soTrang": 12,
                                  "soBan": 2,
                                  "trichYeu": "Trich yeu van ban",
                                  "ghiChu": "Ghi chu xu ly",
                                  "yKienChiDao": "Xu ly gap",
                                  "tepDinhKemChinh": "huong-dan.pdf",
                                  "daDoc": true,
                                  "daXuLy": false,
                                  "isDeleted": false,
                                  "nguoiTaoId": 1,
                                  "nguoiCapNhatId": 1,
                                  "trangThai": "DANG_XU_LY"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Tao cong van den thanh cong"))
                .andExpect(jsonPath("$.data.soCongVan").value("130/SYT"))
                .andExpect(jsonPath("$.data.soDen").value("456"))
                .andExpect(jsonPath("$.data.donViGuiId").value(1))
                .andExpect(jsonPath("$.data.donViGui").value("So Y Te"))
                .andExpect(jsonPath("$.data.phongBanXuLyId").value(10))
                .andExpect(jsonPath("$.data.nguoiXuLyId").value(2))
                .andExpect(jsonPath("$.data.hanXuLy").value("2026-04-10"))
                .andExpect(jsonPath("$.data.doKhanXuLy").value("CAO"))
                .andExpect(jsonPath("$.data.loaiVanBanId").value(3))
                .andExpect(jsonPath("$.data.linhVucId").value(4))
                .andExpect(jsonPath("$.data.hoSoId").value(5))
                .andExpect(jsonPath("$.data.soTrang").value(12))
                .andExpect(jsonPath("$.data.soBan").value(2))
                .andExpect(jsonPath("$.data.trichYeu").value("Trich yeu van ban"))
                .andExpect(jsonPath("$.data.ghiChu").value("Ghi chu xu ly"))
                .andExpect(jsonPath("$.data.ykienChiDao").doesNotExist())
                .andExpect(jsonPath("$.data.yKienChiDao").value("Xu ly gap"))
                .andExpect(jsonPath("$.data.tepDinhKemChinh").value("huong-dan.pdf"))
                .andExpect(jsonPath("$.data.daDoc").value(true))
                .andExpect(jsonPath("$.data.daXuLy").value(false))
                .andExpect(jsonPath("$.data.isDeleted").value(false))
                .andExpect(jsonPath("$.data.nguoiTaoId").value(1))
                .andExpect(jsonPath("$.data.nguoiCapNhatId").value(1))
                .andExpect(jsonPath("$.data.trangThai").value("DANG_XU_LY"));
    }

    @Test
    void listCongVanDenRequiresAuthenticatedSessionAndReturnsCreatedRecords() throws Exception {
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
                                  "soCongVan": "132/SYT",
                                  "soDen": "464",
                                  "tieuDe": "Van ban danh sach",
                                  "noiDungTomTat": "Du lieu de kiem tra list",
                                  "donViGuiId": 1,
                                  "nguoiKy": "Nguyen Van B",
                                  "ngayVanBan": "2026-04-10",
                                  "ngayNhan": "2026-04-11",
                                  "mucDoKhan": "THUONG",
                                  "mucDoMat": "THUONG",
                                  "nguonNhan": "EMAIL"
                                }
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/cong-van-den/danh-sach-cong-van-den")
                        .session(session)
                        .param("keyword", "132/SYT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Lay danh sach cong van den thanh cong"))
                .andExpect(jsonPath("$.data.page").value(0))
                .andExpect(jsonPath("$.data.size").value(20))
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.items[0].soCongVan").value("132/SYT"))
                .andExpect(jsonPath("$.data.items[0].soDen").value("464"))
                .andExpect(jsonPath("$.data.items[0].donViGui").value("So Y Te"));
    }

    @Test
    void listCongVanDenSupportsPaginationSearchAndFilters() throws Exception {
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
                                  "soCongVan": "140/SYT",
                                  "soDen": "500",
                                  "tieuDe": "Thong bao khan",
                                  "noiDungTomTat": "Van ban 1",
                                  "donViGuiId": 1,
                                  "nguoiKy": "Nguyen Van C",
                                  "ngayVanBan": "2026-04-05",
                                  "ngayNhan": "2026-04-06",
                                  "mucDoKhan": "KHAN",
                                  "mucDoMat": "THUONG",
                                  "nguonNhan": "EMAIL",
                                  "trangThai": "DANG_XU_LY",
                                  "daDoc": true,
                                  "daXuLy": false
                                }
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/cong-van-den")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "soCongVan": "141/SYT",
                                  "soDen": "501",
                                  "tieuDe": "Thong bao thuong",
                                  "noiDungTomTat": "Van ban 2",
                                  "donViGuiId": 1,
                                  "nguoiKy": "Nguyen Van D",
                                  "ngayVanBan": "2026-04-09",
                                  "ngayNhan": "2026-04-10",
                                  "mucDoKhan": "THUONG",
                                  "mucDoMat": "THUONG",
                                  "nguonNhan": "EMAIL",
                                  "trangThai": "HOAN_THANH",
                                  "daDoc": false,
                                  "daXuLy": true
                                }
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/cong-van-den/danh-sach-cong-van-den")
                        .session(session)
                        .param("page", "0")
                        .param("size", "1")
                        .param("keyword", "Thong bao")
                        .param("trangThai", "HOAN_THANH")
                        .param("daXuLy", "true")
                        .param("daDoc", "false")
                        .param("ngayNhanFrom", "2026-04-09")
                        .param("ngayNhanTo", "2026-04-11"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.page").value(0))
                .andExpect(jsonPath("$.data.size").value(1))
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.totalPages").value(1))
                .andExpect(jsonPath("$.data.hasNext").value(false))
                .andExpect(jsonPath("$.data.hasPrevious").value(false))
                .andExpect(jsonPath("$.data.items[0].soCongVan").value("141/SYT"))
                .andExpect(jsonPath("$.data.items[0].trangThai").value("HOAN_THANH"))
                .andExpect(jsonPath("$.data.items[0].daXuLy").value(true))
                .andExpect(jsonPath("$.data.items[0].daDoc").value(false));
    }

    @Test
    void listCongVanDenRejectsInvalidDateRange() throws Exception {
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

        mockMvc.perform(get("/api/cong-van-den/danh-sach-cong-van-den")
                        .session(session)
                        .param("ngayNhanFrom", "2026-04-12")
                        .param("ngayNhanTo", "2026-04-10"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("Khoang ngay nhan khong hop le"));
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
                                  "nguon_nhan": "EMAIL",
                                  "han_xu_ly": "2026-04-12",
                                  "do_khan_xu_ly": "TRUNG_BINH",
                                  "phong_ban_xu_ly_id": 8,
                                  "nguoi_xu_ly_id": 2,
                                  "trich_yeu": "Tom tat ngan"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.soCongVan").value("124/SYT"))
                .andExpect(jsonPath("$.data.soDen").value("459"))
                .andExpect(jsonPath("$.data.donViGuiId").value(1))
                .andExpect(jsonPath("$.data.hanXuLy").value("2026-04-12"))
                .andExpect(jsonPath("$.data.doKhanXuLy").value("TRUNG_BINH"))
                .andExpect(jsonPath("$.data.phongBanXuLyId").value(8))
                .andExpect(jsonPath("$.data.nguoiXuLyId").value(2))
                .andExpect(jsonPath("$.data.trichYeu").value("Tom tat ngan"));
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

    @Test
    void rowboatChatRequiresAuthenticatedSession() throws Exception {
        mockMvc.perform(post("/api/rowboat/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "messages": [
                                    {
                                      "role": "user",
                                      "content": "Xin chao"
                                    }
                                  ],
                                  "state": null
                                }
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").value("Authentication is required"));
    }

    @Test
    void rowboatChatReturnsStructuredResponse() throws Exception {
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

        RowboatMessage assistantMessage = new RowboatMessage();
        assistantMessage.setRole("assistant");
        assistantMessage.setContent("Chao ban, toi co the ho tro.");
        assistantMessage.setAgenticResponseType("external");

        RowboatChatResponse response = new RowboatChatResponse();
        response.setMessages(List.of(assistantMessage));
        response.setState(Map.of("last_agent_name", "MainAgent"));

        when(rowboatService.chat(any())).thenReturn(response);

        mockMvc.perform(post("/api/rowboat/chat")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "messages": [
                                    {
                                      "role": "user",
                                      "content": "Xin chao"
                                    }
                                  ],
                                  "state": null
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("AI chat completed successfully"))
                .andExpect(jsonPath("$.data.messages[0].role").value("assistant"))
                .andExpect(jsonPath("$.data.messages[0].content").value("Chao ban, toi co the ho tro."))
                .andExpect(jsonPath("$.data.messages[0].agenticResponseType").value("external"))
                .andExpect(jsonPath("$.data.state.last_agent_name").value("MainAgent"));
    }
}
