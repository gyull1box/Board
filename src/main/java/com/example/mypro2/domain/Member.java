package com.example.mypro2.domain;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(uniqueConstraints =  {@UniqueConstraint(columnNames = {"email"})})
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor @NoArgsConstructor
public class Member {
    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    @NotNull
    private String email;

    @NotNull
    private String password;

    @Embedded
    private Address address;

    private LocalDateTime joinedAt;

    @Column(columnDefinition = "boolean default false")
    private boolean emailVerified;

    private String emailCheckToken;

    @Enumerated(EnumType.STRING)
    private MemberType type;

    public void generateEmailCheckToken() {
        emailCheckToken = UUID.randomUUID().toString();
    }

    public boolean isValidToken(String token) {
        return token.equals(emailCheckToken);
    }

    public void completeSignup() {
        setEmailVerified(true);
        setJoinedAt(LocalDateTime.now());
    }

}
