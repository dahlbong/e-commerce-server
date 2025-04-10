package kr.hhplus.be.server.api.point;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.application.point.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "포인트", description = "포인트 잔액 및 충전 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/points")
public class PointController {

    private final PointService pointService;

    @Operation(summary = "포인트 잔액 조회", description = "해당 사용자의 잔액을 조회합니다.")
    @PostMapping("/charge")
    public PointResponse charge(@RequestBody PointRequest request) {
        return PointResponse.from(pointService.charge(request.getUserId(), request.getAmount()));
    }

    @Operation(summary = "포인트 충전", description = "해당 사용자에게 포인트를 충전합니다.")
    @GetMapping("/{userId}")
    public PointResponse get(@PathVariable Long userId) {
        return PointResponse.from(pointService.get(userId));
    }
}
