package trying.cosmos.domain.course.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import trying.cosmos.domain.course.Course;
import trying.cosmos.domain.course.CourseImage;
import trying.cosmos.domain.planet.response.PlanetFindResponse;
import trying.cosmos.global.utils.date.DateUtils;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CourseFindContent {

    private Long courseId;
    private PlanetFindResponse planet;
    private String title;
    private String createdDate;
    private boolean liked;
    private List<String> images;

    public CourseFindContent(Course course, boolean liked) {
        this.courseId = course.getId();
        this.planet = new PlanetFindResponse(course.getPlanet());
        this.title = course.getTitle();
        this.createdDate = DateUtils.getFormattedDate(course.getCreatedDate());
        this.liked = liked;
        this.images = course.getImages().stream().map(CourseImage::getName).collect(Collectors.toList());
    }
}
