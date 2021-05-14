package com.example.mypro2.domain;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SellBoard {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sBoard_id")
    private Long id;

    @NotNull
    private String url;

    @NotNull
    private String title;

    @NotNull
    private String content;

    private LocalDateTime uptTime;

    private String author;

    @NotNull
    private int price;
}
