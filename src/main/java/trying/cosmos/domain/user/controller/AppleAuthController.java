package trying.cosmos.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import trying.cosmos.domain.user.dto.request.AppleJoinRequest;
import trying.cosmos.domain.user.dto.request.AppleLoginRequest;
import trying.cosmos.domain.user.dto.response.UserLoginResponse;
import trying.cosmos.domain.user.service.SocialAccountService;

@RestController
@RequestMapping("/oauth/apple")
@RequiredArgsConstructor
public class AppleAuthController {

    private final SocialAccountService socialAccountService;

    @PostMapping
    public UserLoginResponse join(@RequestBody @Validated AppleJoinRequest request) {
        return new UserLoginResponse(socialAccountService.join(
                request.getIdentifier(),
                request.getEmail(),
                request.getName(),
                request.getDeviceToken())
        );
    }

    @PostMapping("/login")
    public UserLoginResponse login(@RequestBody @Validated AppleLoginRequest request) {
        return new UserLoginResponse(socialAccountService.login(
                request.getIdentifier(),
                request.getDeviceToken())
        );
    }
}