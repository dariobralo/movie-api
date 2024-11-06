package com.movieapp.movie_api.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class PasswordReset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer otp;

    @Column(nullable = false)
    private Date expirationTime;

    @Column(nullable = false)
    private Integer usagesCount;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /*
     *
     */
    public void setUsagesCount(Integer usagesCount) {
        this.usagesCount = usagesCount;
    }
}
