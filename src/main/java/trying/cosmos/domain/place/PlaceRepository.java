package trying.cosmos.domain.place;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlaceRepository extends JpaRepository<Place, Long> {

    Optional<Place> findByPlaceNumber(Long number);
}
