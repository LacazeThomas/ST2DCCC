package fr.lacazethomas.movieApplication;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Movie {

    private Long id;
    private int rank;
    private double rating;
    private String title;
    private LocalDate releaseDate;
    private List<Long> actorsID;

}
