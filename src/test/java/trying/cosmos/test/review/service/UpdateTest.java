package trying.cosmos.test.review.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import trying.cosmos.domain.course.entity.Course;
import trying.cosmos.domain.course.repository.CourseRepository;
import trying.cosmos.domain.coursereview.entity.CourseReview;
import trying.cosmos.domain.coursereview.repository.CourseReviewRepository;
import trying.cosmos.domain.coursereview.service.CourseReviewService;
import trying.cosmos.domain.planet.entity.Planet;
import trying.cosmos.domain.planet.repository.PlanetRepository;
import trying.cosmos.domain.user.entity.User;
import trying.cosmos.domain.user.repository.UserRepository;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static trying.cosmos.test.TestVariables.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("코스 리뷰 수정")
public class UpdateTest {

    @Autowired
    CourseReviewService reviewService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PlanetRepository planetRepository;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    CourseReviewRepository courseReviewRepository;

    @Autowired
    EntityManager em;
    
    @Nested
    @DisplayName("실패")
    class fail {
        
        @Test
        @DisplayName("사용자의 리뷰가 아니라면 NO_DATA 오류를 발생시킨다.")
        void others_course() throws Exception {
            // GIVEN
            User user = userRepository.save(User.createEmailUser(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN, true));
            User mate = userRepository.save(User.createEmailUser(EMAIL2, PASSWORD, NAME2, DEVICE_TOKEN, true));
            Planet planet = planetRepository.save(new Planet(user, NAME1, IMAGE, INVITE_CODE));
            planet.join(mate);

            User other = userRepository.save(User.createEmailUser(EMAIL3, PASSWORD, NAME3, DEVICE_TOKEN, true));
            Planet othersPlanet = planetRepository.save(new Planet(other, NAME2, IMAGE, INVITE_CODE));
            Course othersCourse = courseRepository.save(new Course(othersPlanet, TITLE, LocalDate.now()));

            em.persist(new CourseReview(other, othersCourse, BODY));

            // WHEN THEN
            assertThatThrownBy(() -> reviewService.update(user.getId(), othersCourse.getId(), BODY, null))
                    .isInstanceOf(NoSuchElementException.class);
        }
        
        @Test
        @DisplayName("리뷰가 존재하지 않는다면 NO_DATA 오류를 발생시킨다.")
        void not_reviewed() throws Exception {
            // GIVEN
            User user = userRepository.save(User.createEmailUser(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN, true));
            User mate = userRepository.save(User.createEmailUser(EMAIL2, PASSWORD, NAME2, DEVICE_TOKEN, true));
            Planet planet = planetRepository.save(new Planet(user, NAME1, IMAGE, INVITE_CODE));
            planet.join(mate);

            Course course = courseRepository.save(new Course(planet, TITLE, LocalDate.now()));

            // WHEN THEN
            assertThatThrownBy(() -> reviewService.update(user.getId(), NOT_EXIST, BODY, null))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }
    
    @Nested
    @DisplayName("성공")
    class success {
        
        @Test
        @DisplayName("코스 리뷰를 수정한다.")
        void update() throws Exception {
            // GIVEN
            User user = userRepository.save(User.createEmailUser(EMAIL1, PASSWORD, NAME1, DEVICE_TOKEN, true));
            User mate = userRepository.save(User.createEmailUser(EMAIL2, PASSWORD, NAME2, DEVICE_TOKEN, true));
            Planet planet = planetRepository.save(new Planet(user, NAME1, IMAGE, INVITE_CODE));
            planet.join(mate);

            Course course = courseRepository.save(new Course(planet, TITLE, LocalDate.now()));
            CourseReview review = courseReviewRepository.save(new CourseReview(user, course, BODY));

            // WHEN
            reviewService.update(user.getId(), review.getId(), "UPDATED", null);

            // THEN
            assertThat(course.getReviews().size())
                    .isEqualTo(1);
            assertThat(course.getReview(user).orElseThrow().getContent())
                    .isEqualTo("UPDATED");
        }
    }
}
