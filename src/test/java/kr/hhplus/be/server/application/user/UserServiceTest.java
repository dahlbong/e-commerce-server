package kr.hhplus.be.server.application.user;

import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserService userService;

    @Test
    @DisplayName("유저 ID로 유저를 조회할 수 있다")
    void getById_returns_user() {
        User user = User.of("홍길동");
        given(userRepository.findById(1L)).willReturn(user);

        User found = userService.getOrCreateById(1L);

        assertThat(found).isEqualTo(user);
        assertThat(found.getName()).isEqualTo("홍길동");
    }

    @Test
    @DisplayName("존재하지 않는 사용자일 경우 새 사용자로 저장한다")
    void getOrCreateByName_creates_and_saves_if_not_exists() {
        // given
        Long userId = 123L;
        given(userRepository.findById(userId)).willReturn(null);
        given(userRepository.save(any())).willAnswer(invocation -> invocation.getArgument(0));

        // when
        User result = userService.getOrCreateById(userId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("임시 이름");
        verify(userRepository).save(result);
    }

    @Test
    @DisplayName("사용자가 이미 존재하면 새로 저장하지 않는다")
    void getOrCreateById_returns_existing_user() {
        // given
        Long userId = 123L;
        User existing = User.of("기존 사용자");
        given(userRepository.findById(userId)).willReturn(existing);

        // when
        User result = userService.getOrCreateById(userId);

        // then
        assertThat(result).isEqualTo(existing);
        verify(userRepository, never()).save(any());
    }
}
