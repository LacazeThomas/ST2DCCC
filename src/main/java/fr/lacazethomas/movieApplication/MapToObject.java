package fr.lacazethomas.movieApplication;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.io.StringReader;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@Component
public class MapToObject {

    @SneakyThrows
    public Movie mapMovie(String o) {
        CSVReader reader = new CSVReaderBuilder(new StringReader(o)).withCSVParser(new CSVParserBuilder().withSeparator(';').build()).build();

        String[] line = reader.readNext();
        List<Long> actors = Stream.of(line[5].substring(1).substring(0, line[5].length() - 2).split(",")).map(Long::parseLong).collect(toList());
        reader.close();

        return Movie.builder().id(Long.parseLong(line[0])).rank(Integer.parseInt(line[1])).rating(Double.parseDouble(line[2])).title(line[3]).releaseDate(LocalDate.parse(line[4])).actorsID(actors).build();
    }

    @SneakyThrows
    public Actor mapActor(String o) {
        CSVReader reader = new CSVReaderBuilder(new StringReader(o)).withCSVParser(new CSVParserBuilder().withSeparator(';').build()).build();

        String[] line = reader.readNext();
        List<Long> movies = Stream.of(line[4].substring(1).substring(0, line[4].length() - 2).split(",")).map(Long::parseLong).collect(toList());
        reader.close();

        return Actor.builder().id(Long.parseLong(line[0])).firstName(line[1]).lastName(line[2]).birthDate(LocalDate.parse(line[3])).moviesID(movies).build();
    }
}
