package kr.hhplus.be.server.interfaces.point;

import jakarta.validation.Valid;
import kr.hhplus.be.server.application.point.PointFacade;
import kr.hhplus.be.server.application.point.PointResult;
import kr.hhplus.be.server.interfaces.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class PointController {

    private final PointFacade pointFacade;

    @GetMapping("/api/v1/users/{id}/point")
    public ApiResponse<PointResponse.Point> getPoint(@PathVariable("id") Long id) {
        PointResult.Point point = pointFacade.getPoint(id);
        return ApiResponse.success(PointResponse.Point.of(point));
    }

    @PostMapping("/api/v1/users/{id}/point/charge")
    public ApiResponse<Void> chargePoint(@PathVariable("id") Long id,
                                           @Valid @RequestBody PointRequest.Charge request) {
        pointFacade.chargePoint(request.toCriteria(id));
        return ApiResponse.success();
    }
}