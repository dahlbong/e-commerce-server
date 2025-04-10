package kr.hhplus.be.server.api.point;

import kr.hhplus.be.server.application.point.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/points")
public class PointController {

    private final PointService pointService;

    @PostMapping("/charge")
    public PointResponse charge(@RequestBody PointRequest request) {
        return PointResponse.from(pointService.charge(request.getUserId(), request.getAmount()));
    }

    @GetMapping("/{userId}")
    public PointResponse get(@PathVariable Long userId) {
        return PointResponse.from(pointService.get(userId));
    }
}
