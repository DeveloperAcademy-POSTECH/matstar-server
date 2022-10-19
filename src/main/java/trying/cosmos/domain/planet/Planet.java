package trying.cosmos.domain.planet;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import trying.cosmos.domain.common.DateAuditingEntity;
import trying.cosmos.domain.user.User;
import trying.cosmos.global.exception.CustomException;
import trying.cosmos.global.exception.ExceptionType;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static trying.cosmos.global.exception.ExceptionType.NO_DATA;
import static trying.cosmos.global.exception.ExceptionType.NO_PERMISSION;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Planet extends DateAuditingEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "planet_id")
    private Long id;

    @OneToMany(mappedBy = "planet")
    private List<User> owners = new ArrayList<>();

    private String name;

    private LocalDate meetDate;

    @Enumerated(EnumType.STRING)
    private PlanetImageType image;

    private String inviteCode;

    // Constructor
    public Planet(User user, String name, PlanetImageType image) {
        this.name = name;
        this.inviteCode = UUID.randomUUID().toString();
        this.image = image;
        this.meetDate = LocalDate.now();
        user.setPlanet(this);
        this.owners.add(user);
    }

    // Convenience Method
    public void join(User guest) {
        if (owners.size() != 1) {
            throw new CustomException(ExceptionType.PLANET_JOIN_FAILED);
        }
        User owner = owners.get(0);
        if (owner.equals(guest)) {
            throw new CustomException(ExceptionType.PLANET_JOIN_FAILED);
        }
        this.owners.add(guest);
        owner.setMate(guest);
        guest.setMate(owner);
        guest.setPlanet(this);
    }

    public void updateDday(int days) {
        this.meetDate = LocalDate.now().minusDays(days);
    }

    public int getDday() {
        return (int) Duration.between(this.meetDate.atStartOfDay(), LocalDate.now().atStartOfDay()).toDays() + 1;
    }

    public void authorize(Long userId) {
        for (User owner : owners) {
            if (owner.getId().equals(userId)) {
                return;
            }
        }
        throw new CustomException(ExceptionType.NO_PERMISSION);
    }

    public boolean isOwnedBy(User user) {
        return owners.contains(user);
    }

    public String getInviteCode(Long userId) {
        if (owners.size() != 1) {
            // 초대코드가 존재하지 않음
            throw new CustomException(NO_DATA);
        }
        if (!owners.get(0).getId().equals(userId)) {
            throw new CustomException(NO_PERMISSION);
        }
        return this.inviteCode;
    }

    public boolean isFull() {
        return this.owners.size() > 1;
    }
}
