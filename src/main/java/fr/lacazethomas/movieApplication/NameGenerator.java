package fr.lacazethomas.movieApplication;

import org.springframework.integration.file.FileNameGenerator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class NameGenerator implements FileNameGenerator {

    public String generateFileName(Message<?> message) {

        //Get filename present in the message header and add .json at the end
        //Exemple : "movies.csv" to "movies.json"
        return ((String) message.getHeaders().get("file_name")).split("\\.")[0] + ".json";
    }
}
