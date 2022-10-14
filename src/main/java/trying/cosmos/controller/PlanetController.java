package trying.cosmos.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import trying.cosmos.auth.AuthKey;
import trying.cosmos.auth.AuthorityOf;
import trying.cosmos.controller.request.PlanetCreateRequest;
import trying.cosmos.controller.request.PlanetJoinRequest;
import trying.cosmos.controller.response.PlanetFindResponse;
import trying.cosmos.controller.response.PlanetInviteCodeResponse;
import trying.cosmos.controller.response.PlanetListFindResponse;
import trying.cosmos.entity.Planet;
import trying.cosmos.entity.component.Authority;
import trying.cosmos.service.PlanetService;

@RestController
@RequestMapping("/planets")
@RequiredArgsConstructor
public class PlanetController {

    private final PlanetService planetService;

    @AuthorityOf(Authority.USER)
    @PostMapping
    public PlanetInviteCodeResponse create(@RequestBody @Validated PlanetCreateRequest request) {
        Planet planet = planetService.create(AuthKey.get(), request.getName(), request.getPlanetImageType());
        return new PlanetInviteCodeResponse(planetService.getInviteCode(AuthKey.get(), planet.getId()));
    }

    @AuthorityOf(Authority.USER)
    @GetMapping("/{id}/code")
    public PlanetInviteCodeResponse getInviteCode(@PathVariable Long id) {
        return new PlanetInviteCodeResponse(planetService.getInviteCode(AuthKey.get(), id));
    }

    @AuthorityOf(Authority.USER)
    @PostMapping("/join")
    public void joinPlanet(@RequestBody @Validated PlanetJoinRequest request) {
        planetService.joinPlanet(AuthKey.get(), request.getCode());
    }

    @GetMapping("/{id}")
    public PlanetFindResponse findPlanet(@PathVariable Long id) {
        return new PlanetFindResponse(planetService.find(id));
    }

    @GetMapping
    public PlanetListFindResponse findPlanets(@RequestParam String query, Pageable pageable) {
        return new PlanetListFindResponse(planetService.findPlanets(query, pageable));
    }
}