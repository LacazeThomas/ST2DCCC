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
    public Movie mapMovie(String csvLine) {
        // Create a CSVReader associated with the string 'csvLine' and with ';' as separator
        CSVReader reader = new CSVReaderBuilder(new StringReader(csvLine)).withCSVParser(new CSVParserBuilder().withSeparator(';').build()).build();

        // Read the first line only because we using spliter 
        String[] line = reader.readNext();

        // Parse the latest col to a list of idActors for exemple : [001,002,003] as id of actors participating in the movie
        List<Long> actors = Stream.of(line[5].substring(1).substring(0, line[5].length() - 2).split(",")).map(Long::parseLong).collect(toList());
        reader.close();

        //Return new Movie object with CSV param
        return Movie.builder().id(Long.parseLong(line[0])).rank(Integer.parseInt(line[1])).rating(Double.parseDouble(line[2])).title(line[3]).releaseDate(LocalDate.parse(line[4])).actorsID(actors).build();
    }

    @SneakyThrows
    public Actor mapActor(String csvLine) {
        // Create a CSVReader associated with the string 'csvLine' and with ';' as separator
        CSVReader reader = new CSVReaderBuilder(new StringReader(csvLine)).withCSVParser(new CSVParserBuilder().withSeparator(';').build()).build();

        // Read the first line only because we using spliter 
        String[] line = reader.readNext();

        // Parse the latest col to a list of idMovies for exemple : [001,002,003] as id of movies in which the actor is participating
        List<Long> movies = Stream.of(line[4].substring(1).substring(0, line[4].length() - 2).split(",")).map(Long::parseLong).collect(toList());
        reader.close();


        //Return new Actor object with CSV param
        return Actor.builder().id(Long.parseLong(line[0])).firstName(line[1]).lastName(line[2]).birthDate(LocalDate.parse(line[3])).moviesID(movies).build();
    }
}
