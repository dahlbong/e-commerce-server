package kr.hhplus.be.server.domain.user;

import org.springframework.stereotype.Component;

@Component
public interface UserRepository {

    User save(User user);

    User findById(Long userId);
}