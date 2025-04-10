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
        User user = userRepository.findById(id);
        if (user == null) {
            user = userRepository.save(User.of("임시 이름"));
        }
        return user;
    }
}