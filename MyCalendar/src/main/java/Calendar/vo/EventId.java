package Calendar.vo;

import java.util.Objects;
import java.util.UUID;

public record EventId(String valeur) {

    public EventId {
        if (valeur == null || valeur.isBlank()) {
            throw new IllegalArgumentException("L'identifiant de l'événement ne peut pas être vide ou null");
        }
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