package trying.cosmos.test.notification.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import trying.cosmos.domain.notification.entity.Notification;
import trying.cosmos.domain.notification.entity.NotificationTarget;
import trying.cosmos.domain.notification.repository.NotificationRepository;
import trying.cosmos.domain.notification.service.NotificationService;
import trying.cosmos.domain.planet.entity.Planet;
import trying.cosmos.domain.planet.repository.PlanetRepository;
import trying.cosmos.domain.user.entity.User;
import trying.cosmos.domain.user.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static trying.cosmos.test.TestVariables.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("알림 전체 삭제")
public class DeleteAllTest {

    @Autowired
    NotificationService notificationService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PlanetRepository planetRepository;

    @Autowired
    NotificationRepository notificationRepository;

    @Nested
    @DisplayName("성공")
    class success {

        @Test
        @DisplayName("알림을 최근 순서대로 반환")
        void find() throws Exception {
            // GIVEN
            User user = userRepository.save(User.createEmailUser(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN, true));
            User mate = userRepository.save(User.createEmailUser(EMAIL2, PASSWORD, NAME2, DEVICE_TOKEN, true));
            Planet planet = planetRepository.save(new Planet(user, NAME1, IMAGE, INVITE_CODE));
            planet.join(mate);

            Notification notification1 = notificationRepository.save(new Notification(user, TITLE, BODY, NotificationTarget.COURSE, 1L));
            Notification notification2 = notificationRepository.save(new Notification(user, TITLE, BODY, NotificationTarget.PLANET, null));
            Notification notification3 = notificationRepository.save(new Notification(user, TITLE, BODY, NotificationTarget.REVIEW, 1L));

            // WHEN
            notificationService.deleteAll(user.getId());

            // THEN
            assertThat(notificationRepository.findByUser(user).size()).isEqualTo(0);
        }
    }
}
