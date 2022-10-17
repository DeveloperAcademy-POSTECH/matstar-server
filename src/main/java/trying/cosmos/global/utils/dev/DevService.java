package trying.cosmos.global.utils.dev;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import trying.cosmos.domain.planet.Planet;
import trying.cosmos.domain.planet.PlanetImageType;
import trying.cosmos.domain.planet.PlanetRepository;
import trying.cosmos.domain.planet.PlanetService;
import trying.cosmos.domain.user.User;
import trying.cosmos.domain.user.UserRepository;
import trying.cosmos.domain.user.UserStatus;
import trying.cosmos.global.auth.Authority;

import static org.apache.commons.lang3.RandomStringUtils.random;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Profile({"local", "dev"})
public class DevService {

    private final UserRepository userRepository;
    private final PlanetRepository planetRepository;
    private final PlanetService planetService;

    private static final String MOCK_DEVICE_TOKEN = "deviceToken";

    @Transactional
    public User createUser() {
        String str = randomName();
        return userRepository.save(new User(str + "@gmail.com", "password", str, UserStatus.LOGOUT, Authority.USER));
    }

    @Transactional
    public Planet createPlanet() {
        return planetRepository.save(new Planet(createUser(), randomName(), PlanetImageType.EARTH));
    }

    private String randomName() {
        return random(6, true, true);
    }
}