package trying.cosmos.domain.user.dto.request;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SocialJoinRequest {

    @NotBlank
    private String identifier;

//    @NotBlank
    @Email(message = "이메일 형식이 아닙니다.")
    private String email;

    @NotBlank
    @Pattern(regexp = "^[가-힣A-Za-z0-9]{2,8}", message = "닉네임은 한글, 영어, 숫자로 이루어진 2~8자리 문자열입니다.")
    private String name;

    @NotBlank
    private String deviceToken;
}
