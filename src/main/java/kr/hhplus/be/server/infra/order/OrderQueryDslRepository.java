package kr.hhplus.be.server.infra.order;


import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.hhplus.be.server.domain.order.OrderCommand;
import kr.hhplus.be.server.domain.order.OrderInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static kr.hhplus.be.server.domain.order.QOrder.order;
import static kr.hhplus.be.server.domain.order.QOrderProduct.orderProduct;

@Repository
@RequiredArgsConstructor
public class OrderQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    public List<OrderInfo.PaidProduct> findPaidProducts(OrderCommand.PaidProducts command) {
        return queryFactory.select(
                        Projections.constructor(
                                OrderInfo.PaidProduct.class,
                                orderProduct.productId,
                                orderProduct.quantity.sum().as("quantity")
                        )
                )
                .from(order)
                .join(order.orderProducts, orderProduct)
                .where(
                        order.orderStatus.eq(command.getStatus()),
                        order.paidAt.between(
                                command.getPaidAt().minusDays(1).atStartOfDay(),
                                command.getPaidAt().atStartOfDay()
                        )
                )
                .groupBy(orderProduct.productId)
                .fetch();
    }
}