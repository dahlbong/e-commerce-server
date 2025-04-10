package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.api.order.OrderRequest;
import kr.hhplus.be.server.application.point.PointService;
import kr.hhplus.be.server.application.product.ProductService;
import kr.hhplus.be.server.application.user.UserService;
import kr.hhplus.be.server.domain.BusinessException;
import kr.hhplus.be.server.domain.coupon.IssuedCouponRepository;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderEventSender;
import kr.hhplus.be.server.domain.order.OrderRepository;
import kr.hhplus.be.server.domain.order.PaymentRepository;
import kr.hhplus.be.server.domain.point.enums.PointErrorCode;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.enums.ProductErrorCode;
import kr.hhplus.be.server.domain.product.enums.SellingStatus;
import kr.hhplus.be.server.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock ProductService productService;
    @Mock PointService pointService;
    @Mock UserService userService;
    @Mock OrderRepository orderRepository;
    @Mock PaymentRepository paymentRepository;
    @Mock IssuedCouponRepository issuedCouponRepository;
    @Mock OrderEventSender orderEventSender;

    @InjectMocks
    OrderService orderService;
    final Product cola = Product.of("콜라", SellingStatus.SELLING, BigDecimal.valueOf(1500), 100);
    final Product cider = Product.of("사이다", SellingStatus.SELLING, BigDecimal.valueOf(1000), 50);

    @Test
    @DisplayName("단일 상품 주문 성공")
    void placeOrder_single_success() {
        OrderRequest request = requestOf(1L, List.of(item(1L, 2)), null);

        given(productService.getById(1L)).willReturn(cola);

        List<Order> result = orderService.placeOrder(request);

        assertThat(result).hasSize(1);
        verify(pointService).use(1L, BigDecimal.valueOf(3000));
        verify(orderRepository).save(any());
        verify(paymentRepository).save(any());
        verify(orderEventSender).send(any());
    }

    @Test
    @DisplayName("다수 상품 주문 성공")
    void placeOrder_multiple_products_success() {
        OrderRequest request = requestOf(1L, List.of(item(1L, 2), item(2L, 3)), null);
        given(productService.getById(1L)).willReturn(cola);
        given(productService.getById(2L)).willReturn(cider);

        List<Order> result = orderService.placeOrder(request);

        assertThat(result).hasSize(2);
        verify(pointService).use(1L, BigDecimal.valueOf(2 * 1500 + 3 * 1000));
        verify(orderRepository, times(2)).save(any());
        verify(orderEventSender).send(any());
    }

    @Test
    @DisplayName("상품이 존재하지 않으면 실패")
    void placeOrder_fails_when_product_not_found() {
        OrderRequest request = requestOf(1L, List.of(item(99L, 1)), null);
        given(productService.getById(99L)).willThrow(new BusinessException(ProductErrorCode.PRODUCT_NOT_FOUND));

        assertThatThrownBy(() -> orderService.placeOrder(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ProductErrorCode.PRODUCT_NOT_FOUND.message());

        verify(pointService, never()).use(any(), any());
        verify(orderRepository, never()).save(any());
        verify(paymentRepository, never()).save(any());
        verify(orderEventSender, never()).send(any());
    }

    @Test
    @DisplayName("포인트 잔액이 부족하면 결제 실패 처리된다")
    void placeOrder_fails_when_insufficient_balance() {
        OrderRequest request = requestOf(1L, List.of(item(1L, 3)), null); // 3 * 1500 = 4500

        Product cola = Product.of("콜라", SellingStatus.SELLING, BigDecimal.valueOf(1500), 100);
        given(productService.getById(1L)).willReturn(cola);
        given(userService.getOrCreateById(1L)).willReturn(User.of(1L, "홍길동"));
        willThrow(new BusinessException(PointErrorCode.INSUFFICIENT_BALANCE))
                .given(pointService).use(eq(1L), any());

        assertThatThrownBy(() -> orderService.placeOrder(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage(PointErrorCode.INSUFFICIENT_BALANCE.message());

        verify(orderRepository, never()).save(any());
        verify(paymentRepository, never()).save(any());
        verify(orderEventSender, never()).send(any());
    }


    private static OrderRequest requestOf(Long userId, List<OrderRequest.OrderItemRequest> items, Long couponId) {
        return new OrderRequest(userId, items, "POINT", couponId);
    }

    private static OrderRequest.OrderItemRequest item(Long productId, int quantity) {
        return new OrderRequest.OrderItemRequest(productId, quantity);
    }
}
