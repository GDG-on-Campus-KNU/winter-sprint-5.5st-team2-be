package gdgoc.be.controller;

import gdgoc.be.common.util.SecurityUtil;
import gdgoc.be.dto.user.UserResponse;
import gdgoc.be.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Test
    @DisplayName("내 정보 조회 테스트")
    @WithMockUser(username = "test@example.com")
    void getMeTest() throws Exception {
        UserResponse response = new UserResponse(
                1L,
                "test@example.com",
                "테스트유저",
                "USER",
                "010-1234-5678",
                "서울시 강남구"
        );

        try (MockedStatic<SecurityUtil> mockedSecurityUtil = Mockito.mockStatic(SecurityUtil.class)) {
            mockedSecurityUtil.when(SecurityUtil::getCurrentUserEmail).thenReturn("test@example.com");
            given(authService.getMe("test@example.com")).willReturn(response);

            mockMvc.perform(get("/api/users/me"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.email").value("test@example.com"))
                    .andExpect(jsonPath("$.data.userName").value("테스트유저"))
                    .andExpect(jsonPath("$.data.role").value("USER"));
        }
    }
}
