package Calendar.Serialization;

import Calendar.CalendarManager;
import Calendar.Events.*;
import Calendar.vo.*;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Classe utilitaire pour gérer la sérialisation et la désérialisation du calendrier et des événements
 */
public class JsonCalendarSerializer {

    private final ObjectMapper objectMapper;

    public JsonCalendarSerializer() {
        objectMapper = new ObjectMapper();

        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // Enregistrement des sous-types pour le polymorphisme
        objectMapper.registerSubtypes(
                new NamedType(RendezVous.class, "RendezVous"),
                new NamedType(Reunion.class, "Reunion"),
                new NamedType(EvenementPeriodique.class, "EvenementPeriodique"),
                new NamedType(Anniversaire.class, "Anniversaire")
        );
    }

    /**
     * Expose l'ObjectMapper pour les tests unitaires
     */
    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    /**
     * Sérialise la liste des événements en JSON en utilisant des wrappers
     */
    public String serializeEvents(List<Event> events) throws JsonProcessingException {
        List<EventWrapper> wrappers = events.stream()
                .map(EventWrapper::new)
                .collect(Collectors.toList());
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(wrappers);
    }

    /**
     * Désérialise une liste d'événements depuis une chaîne JSON
     */
    public List<Event> deserializeEvents(String json) throws JsonProcessingException {
        List<EventWrapper> wrappers = objectMapper.readValue(json, objectMapper.getTypeFactory()
                .constructCollectionType(ArrayList.class, EventWrapper.class));
        return wrappers.stream()
                .map(EventWrapper::getEvent)
                .collect(Collectors.toList());
    }

    /**
     * Sauvegarde tous les événements d'un CalendarManager dans un fichier JSON
     */
    public void saveCalendarToFile(CalendarManager calendar, String filePath) throws IOException {
        // Récupérer tous les événements (sans filtre de période)
        List<Event> allEvents = new ArrayList<>();
        calendar.getAllEvents().forEach(allEvents::add);

        String json = serializeEvents(allEvents);
        Files.writeString(Paths.get(filePath), json);
    }

    /**
     * Charge les événements depuis un fichier JSON dans un CalendarManager
     */
    public void loadCalendarFromFile(CalendarManager calendar, String filePath) throws IOException {
        if (!new File(filePath).exists()) {
            throw new IOException("Le fichier n'existe pas: " + filePath);
        }

        String json = Files.readString(Paths.get(filePath));
        List<Event> events = deserializeEvents(json);

        // Ajouter tous les événements au calendrier
        calendar.clearEvents();
        for (Event event : events) {
            calendar.ajouterEvenement(event);
        }
    }
}