package kr.hhplus.be.server.domain.order.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderPayEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public void publishOrderPayEvent(OrderPayEvent event) {
        applicationEventPublisher.publishEvent(event);
    }
}