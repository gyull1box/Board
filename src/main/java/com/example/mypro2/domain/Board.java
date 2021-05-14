package com.example.mypro2.domain;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter @Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Board {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long id;

    private String content;

    @NonNull
    private String title;

    private LocalDateTime writeAt;

    private LocalDateTime modifiedAt;

    private String author;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "member")
    private List<Comment> comments = new ArrayList<>();

    int liked;
}
