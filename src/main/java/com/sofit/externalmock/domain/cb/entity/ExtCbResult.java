package com.sofit.externalmock.domain.cb.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "ext_cb_result")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExtCbResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cbId;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 7)
    private String residentNumber;

    @Column(nullable = false)
    private Integer creditScore;

    private LocalDateTime evaluatedAt;
}
