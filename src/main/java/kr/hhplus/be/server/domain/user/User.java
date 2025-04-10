package kr.hhplus.be.server.domain.user;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.BusinessException;
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

    private String name;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public static User of(String name) {
        if (name == null || name.isBlank()) {
            throw new BusinessException(UserErrorCode.BLANK_NAME);
        }
        LocalDateTime now = LocalDateTime.now();
        User user = new User();
        user.name = name;
        user.createdAt = now;
        user.updatedAt = now;

        return user;
    }
}
