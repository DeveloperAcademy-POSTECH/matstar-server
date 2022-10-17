package trying.cosmos.domain.course;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import trying.cosmos.domain.course.request.TagCreateRequest;
import trying.cosmos.domain.place.PlaceService;
import trying.cosmos.domain.planet.Planet;
import trying.cosmos.domain.planet.PlanetRepository;
import trying.cosmos.domain.user.User;
import trying.cosmos.domain.user.UserRepository;
import trying.cosmos.global.exception.CustomException;
import trying.cosmos.global.exception.ExceptionType;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CourseService {

    private final UserRepository userRepository;
    private final PlanetRepository planetRepository;
    private final CourseRepository courseRepository;
    private final PlaceService placeService;
    private final CourseLikeRepository courseLikeRepository;

    @Transactional
    public Course create(Long userId, Long planetId, String title, String body, Access access, List<TagCreateRequest> tagDto) {
        Planet planet = planetRepository.findById(planetId).orElseThrow();
        planet.authorize(userId);

        Course course = courseRepository.save(new Course(planet, title, body, access));
        List<Tag> tags = tagDto.stream().map(tag ->
                new Tag(course, placeService.create(tag.getPlace().getId(), tag.getPlace().getName(), tag.getPlace().getLatitude(), tag.getPlace().getLongitude()), tag.getName())
        ).collect(Collectors.toList());
        return course;
    }

    public Course find(Long userId, Long courseId) {
        Course course = courseRepository.findByIdWithTagPlace(courseId).orElseThrow();
        User user = userRepository.findById(userId).orElseThrow();
        if (course.getAccess().equals(Access.PRIVATE) && !course.getPlanet().beOwnedBy(user)) {
            throw new CustomException(ExceptionType.NO_PERMISSION);
        }
        return course;
    }

    public Slice<Course> findFeeds(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId).orElseThrow();
        Planet planet = user.hasPlanet() ? user.getPlanet() : null;
        return courseRepository.findPublicByPlanet(planet, pageable);
    }

    public boolean isLiked(Long userId, Long courseId) {
        return courseLikeRepository.existsByUserIdAndCourseId(userId, courseId);
    }

    @Transactional
    public void like(Long userId, Long courseId) {
        if (courseLikeRepository.existsByUserIdAndCourseId(userId, courseId)) {
            throw new CustomException(ExceptionType.DUPLICATED);
        }
        courseLikeRepository.save(new CourseLike(userRepository.findById(userId).orElseThrow(), courseRepository.findById(courseId).orElseThrow()));
    }

    @Transactional
    public void dislike(Long userId, Long courseId) {
        if (!courseLikeRepository.existsByUserIdAndCourseId(userId, courseId)) {
            throw new CustomException(ExceptionType.NO_DATA);
        }
        courseLikeRepository.delete(courseLikeRepository.findByUserIdAndCourseId(userId, courseId).orElseThrow());
    }
}