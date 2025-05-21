package kr.hhplus.be.server.supporters;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.application.order.OrderFacade;
import kr.hhplus.be.server.application.point.PointFacade;
import kr.hhplus.be.server.application.product.ProductFacade;
import kr.hhplus.be.server.application.rank.RankFacade;
import kr.hhplus.be.server.application.user.UserCouponFacade;
import kr.hhplus.be.server.interfaces.order.OrderController;
import kr.hhplus.be.server.interfaces.point.PointController;
import kr.hhplus.be.server.interfaces.product.ProductController;
import kr.hhplus.be.server.interfaces.rank.RankController;
import kr.hhplus.be.server.interfaces.user.UserCouponController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {
    PointController.class,
    UserCouponController.class,
    OrderController.class,
    ProductController.class,
    RankController.class,
})
public abstract class ControllerTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockitoBean
    protected PointFacade pointFacade;

    @MockitoBean
    protected OrderFacade orderFacade;

    @MockitoBean
    protected ProductFacade productFacade;

    @MockitoBean
    protected RankFacade rankFacade;

    @MockitoBean
    protected UserCouponFacade userCouponFacade;

}
