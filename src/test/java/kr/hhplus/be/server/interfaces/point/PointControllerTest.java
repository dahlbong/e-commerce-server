package kr.hhplus.be.server.interfaces.point;

import kr.hhplus.be.server.application.point.PointResult;
import kr.hhplus.be.server.supporters.ControllerTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.MediaType;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PointControllerTest extends ControllerTestSupport {

    @DisplayName("잔액을 조회한다.")
    @Test
    void getPoint() throws Exception {
        // given
        when(pointFacade.getPoint(1L))
            .thenReturn(PointResult.Point.of(1_000L));

        // when & then
        mockMvc.perform(
            get("/api/v1/users/{id}/point", 1L)
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("OK"))
            .andExpect(jsonPath("$.data.amount").value(1_000L));
    }

    @DisplayName("잔액 충전 시, 잔액은 필수이다.")
    @Test
    void chargePointWithoutAmount() throws Exception {
        // given
        PointRequest.Charge request = new PointRequest.Charge();

        // when & then
        mockMvc.perform(
            post("/api/v1/users/{id}/point/charge", 1L)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(400))
            .andExpect(jsonPath("$.message").value("잔액은 필수 입니다."));
    }

    @DisplayName("잔액 충전 시, 잔액은 양수여야 한다.")
    @ParameterizedTest
    @ValueSource(longs = {-1_000L, 0})
    void chargePointWithNegativeOrZeroAmount(Long amount) throws Exception {
        // given
        PointRequest.Charge request = PointRequest.Charge.of(amount);

        // when & then
        mockMvc.perform(
            post("/api/v1/users/{id}/point/charge", 1L)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(400))
            .andExpect(jsonPath("$.message").value("잔액은 양수여야 합니다."));
    }

    @DisplayName("잔액을 충전한다.")
    @Test
    void chargePoint() throws Exception {
        // given
        PointRequest.Charge request = PointRequest.Charge.of(10_000L);

        // when & then
        mockMvc.perform(
                post("/api/v1/users/{id}/point/charge", 1L)
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.message").value("OK"));
    }
}