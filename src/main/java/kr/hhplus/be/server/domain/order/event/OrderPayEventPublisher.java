package kr.hhplus.be.server.domain.order.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public void publishOrderEvent(OrderEvent event) {
        applicationEventPublisher.publishEvent(event);
    }
}