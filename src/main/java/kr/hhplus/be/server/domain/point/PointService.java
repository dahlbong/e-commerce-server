package kr.hhplus.be.server.domain.point;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;
    private static final long NONE = 0L;

    @Transactional
    public void chargePoint(PointCommand.Charge command) {
        Point balance = pointRepository.findOptionalByUserId(command.getUserId())
                .orElseGet(() -> pointRepository.save(Point.create(command.getUserId())));

        balance.charge(command.getAmount());

        PointTransaction transaction = PointTransaction.ofCharge(balance, command.getAmount());
        pointRepository.saveTransaction(transaction);
    }

    @Transactional
    public void usePoint(PointCommand.Use command) {
        Point balance = pointRepository.findOptionalByUserId(command.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("잔고가 존재하지 않습니다."));

        balance.use(command.getAmount());

        PointTransaction transaction = PointTransaction.ofUse(balance, command.getAmount());
        pointRepository.saveTransaction(transaction);
    }

    public PointInfo.Point getPoint(Long userId) {
        Long amount = pointRepository.findOptionalByUserId(userId)
                .map(Point::getAmount)
                .orElse(NONE);

        return PointInfo.Point.of(amount);
    }
}