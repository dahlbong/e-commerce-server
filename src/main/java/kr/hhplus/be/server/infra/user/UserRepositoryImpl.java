package kr.hhplus.be.server.infra.user;

import kr.hhplus.be.server.domain.BusinessException;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserErrorCode;
import kr.hhplus.be.server.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    @Override
    public User findById(Long id) {
        return userJpaRepository.findById(id)
                .orElseThrow(() -> new BusinessException(UserErrorCode.NOT_FOUND_USER));
    }

    @Override
    public Optional<User> findOptionalById(Long id) {
        return userJpaRepository.findById(id);
    }

    @Override
    public User save(User user) {
        return userJpaRepository.save(user);
    }
}
