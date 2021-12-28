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
public class Actor {


    private Long id;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private List<Long> moviesID;

}
