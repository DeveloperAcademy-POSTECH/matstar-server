package trying.cosmos.domain.course.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import trying.cosmos.domain.course.Course;
import trying.cosmos.domain.planet.planet.PlanetFindResponse;
import trying.cosmos.global.utils.date.DateUtils;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CourseFindContent {

    private Long id;
    private PlanetFindResponse planet;
    private String title;
    private String createdDate;
    private boolean liked;

    public CourseFindContent(Course course, boolean liked) {
        this.id = course.getId();
        this.planet = new PlanetFindResponse(course.getPlanet());
        this.title = course.getTitle();
        this.createdDate = DateUtils.getFormattedDate(course.getCreatedDate());
        this.liked = liked;
    }
}
