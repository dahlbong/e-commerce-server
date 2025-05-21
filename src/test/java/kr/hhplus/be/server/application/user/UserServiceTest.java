package kr.hhplus.be.server.application.user;

import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.domain.user.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserService userService;

    @Test
    @DisplayName("유저가 존재하면 해당 유저를 반환한다")
    void getOrCreateById_returns_existing_user() {
        // given
        Long userId = 1L;
        User existingUser = User.of(userId, "기존 유저");
        given(userRepository.findOptionalById(userId)).willReturn(Optional.of(existingUser));

        // when
        User result = userService.getOrCreateById(userId);

        // then
        assertThat(result).isEqualTo(existingUser);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("유저가 없으면 새 유저를 생성 후 저장한다")
    void getOrCreateById_creates_new_user_if_not_found() {
        // given
        Long userId = 2L;
        given(userRepository.findOptionalById(userId)).willReturn(Optional.empty());
        given(userRepository.save(any())).willAnswer(invocation -> invocation.getArgument(0));

        // when
        User result = userService.getOrCreateById(userId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(userId);
        assertThat(result.getUsername()).isEqualTo("임시 이름");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("getById는 무조건 유저를 반환한다")
    void getById_returns_user_directly() {
        // given
        Long userId = 3L;
        User user = User.of(userId, "정상 유저");
        given(userRepository.findById(userId)).willReturn(user);

        // when
        User result = userService.getById(userId);

        // then
        assertThat(result).isEqualTo(user);
        assertThat(result.getUsername()).isEqualTo("정상 유저");
    }
}
