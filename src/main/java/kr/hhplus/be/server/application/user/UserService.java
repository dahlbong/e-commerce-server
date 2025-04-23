package kr.hhplus.be.server.application.user;

import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User getOrCreateById(Long id) {
        return userRepository.findOptionalById(id)
                .orElseGet(() -> userRepository.save(User.of(id, "임시 이름")));
    }

    public User getById(Long id) {
        return userRepository.findById(id);
    }
}
