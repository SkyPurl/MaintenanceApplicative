package Calendar.vo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;
import java.util.UUID;

public record EventId(String valeur) {

    @JsonCreator
    public EventId(@JsonProperty("valeur") String valeur) {
        if (valeur == null || valeur.isBlank()) {
            throw new IllegalArgumentException("L'identifiant de l'événement ne peut pas être vide ou null");
        }
        this.valeur = valeur;
    }

    // Constructeur par défaut pour la sérialisation JSON
    private EventId() {
        this(UUID.randomUUID().toString());
    }

    public static EventId generate() {
        return new EventId(UUID.randomUUID().toString());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventId eventId = (EventId) o;
        return Objects.equals(valeur, eventId.valeur);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valeur);
    }

    @Override
    public String toString() {
        return valeur;
    }
}