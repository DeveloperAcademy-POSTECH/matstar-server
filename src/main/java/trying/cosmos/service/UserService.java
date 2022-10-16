package trying.cosmos.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import trying.cosmos.auth.TokenProvider;
import trying.cosmos.entity.Certification;
import trying.cosmos.entity.User;
import trying.cosmos.exception.CustomException;
import trying.cosmos.exception.ExceptionType;
import trying.cosmos.repository.CertificationRepository;
import trying.cosmos.repository.UserRepository;
import trying.cosmos.utils.cipher.BCryptUtils;
import trying.cosmos.utils.email.EmailType;
import trying.cosmos.utils.email.EmailUtils;

import java.util.HashMap;
import java.util.Map;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final CertificationRepository certificationRepository;

    private final EmailUtils emailUtils;
    private final TokenProvider tokenProvider;

    public boolean isExist(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional
    public User join(String email, String password, String name) {
        Certification certification = certificationRepository.findByEmail(email).orElseThrow(() -> new CustomException(ExceptionType.CERTIFICATION_FAILED));
        if (!certification.isCertified()) {
            throw new CustomException(ExceptionType.CERTIFICATION_FAILED);
        }

        certificationRepository.delete(certification);
        return userRepository.save(new User(email, password, name));
    }

    @Transactional
    public String login(String email, String password, String deviceToken) {
        User user = userRepository.findByEmail(email).orElseThrow();
        checkPassword(password, user);
        user.login(deviceToken);
        return tokenProvider.getAccessToken(user);
    }

    public User find(Long id) {
        return userRepository.findById(id).orElseThrow();
    }

    private static void checkPassword(String password, User user) {
        if (!BCryptUtils.isMatch(password, user.getPassword())) {
            throw new CustomException(ExceptionType.INVALID_PASSWORD);
        }
    }

    @Transactional
    public void resetPassword(String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        String password = createRandomStringNumber(10);
        user.resetPassword(BCryptUtils.encrypt(password));
        sendResetPasswordEmail(email, password);
    }

    private String createRandomStringNumber(int length) {
        return RandomStringUtils.random(length, true, true);
    }

    private void sendResetPasswordEmail(String email, String password) {
        Map<String, String> model = new HashMap<>();
        model.put("password", password);
        model.put("body1", "임시 비밀번호가 발급되었습니다.");
        model.put("body2", "보안을 위해 로그인 후 비밀번호를 변경해주세요.");
        emailUtils.send(email, "임시 비밀번호가 발급되었습니다.", "email-template", EmailType.RESET_PASSWORD, model);
    }

    @Transactional
    public void updateName(Long id, String name) {
        User user = userRepository.findById(id).orElseThrow();
        user.setName(name);
    }

    @Transactional
    public void updatePassword(Long id, String password) {
        User user = userRepository.findById(id).orElseThrow();
        user.setPassword(BCryptUtils.encrypt(password));
    }

    @Transactional
    public void logout(Long id) {
        User user = userRepository.findById(id).orElseThrow();
        user.logout();
    }

    @Transactional
    public void withdraw(Long id) {
        User user = userRepository.findById(id).orElseThrow();
        user.withdraw();
    }
}
