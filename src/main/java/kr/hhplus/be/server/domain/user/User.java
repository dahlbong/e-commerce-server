package kr.hhplus.be.server.domain.user;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.BusinessException;
import kr.hhplus.be.server.domain.user.enums.UserErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public static User of(Long id, String name) {
        if (name == null || name.isBlank()) {
            throw new BusinessException(UserErrorCode.BLANK_NAME);
        }
        LocalDateTime now = LocalDateTime.now();
        return new User(id, name, now, now);
    }

    public static User of(String name) {
        return of(null, name);
    }
}
