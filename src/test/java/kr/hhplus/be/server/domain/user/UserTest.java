package kr.hhplus.be.server.domain.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class UserTest {

    @Test
    @DisplayName("사용자 생성 시 이름이 올바르게 설정된다")
    void createUser_success() {
        User user = User.of("홍길동");

        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getName()).isEqualTo("홍길동");
        assertThat(user.getCreatedAt()).isNotNull();
        assertThat(user.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("사용자 이름이 비어 있으면 예외가 발생한다")
    void createUser_fail_blankName() {
        assertThatThrownBy(() -> User.of(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("사용자 이름은 비어 있을 수 없습니다.");

        assertThatThrownBy(() -> User.of("   "))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
