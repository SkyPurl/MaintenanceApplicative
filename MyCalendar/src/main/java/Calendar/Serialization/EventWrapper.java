package Calendar.Serialization;

import Calendar.Events.*;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Classe enveloppe pour faciliter la sérialisation des événements
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventWrapper {
    @JsonProperty("type")
    private final String type;

    @JsonProperty("event")
    private final Event event;

    @JsonCreator
    public EventWrapper(
            @JsonProperty("type") String type,
            @JsonProperty("event") Event event) {
        this.type = type;
        this.event = event;
    }

    public EventWrapper(Event event) {
        this.event = event;
        this.type = event.getClass().getSimpleName();
    }

    // Constructeur par défaut pour Jackson
    private EventWrapper() {
        this.type = null;
        this.event = null;
    }

    public String getType() {
        return type;
    }

    public Event getEvent() {
        return event;
    }
}