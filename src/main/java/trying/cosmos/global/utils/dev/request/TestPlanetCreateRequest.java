package trying.cosmos.global.utils.dev.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import trying.cosmos.domain.planet.entity.PlanetImageType;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TestPlanetCreateRequest {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Pattern(regexp = "^[A-Za-z0-9]{8,12}", message = "비밀번호는 영어와 숫자로 이루어진 8~12자리 문자열입니다.")
    private String password;

    @NotBlank
    @Pattern(regexp = "^[가-힣A-Za-z0-9]{2,8}", message = "닉네임은 한글, 영어, 숫자로 이루어진 2~8자리 문자열입니다.")
    private String name;

    @NotBlank
    private String planetName;

    @NotNull
    private PlanetImageType image;
}
