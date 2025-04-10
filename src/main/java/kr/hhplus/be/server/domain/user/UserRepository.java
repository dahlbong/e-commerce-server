package kr.hhplus.be.server.domain.user;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findOptionalById(Long id);

    User findById(Long id);
    User save(User user);
}
