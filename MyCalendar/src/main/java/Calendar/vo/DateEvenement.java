package Calendar.vo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public record DateEvenement(LocalDateTime valeur) {

    @JsonCreator
    public DateEvenement(@JsonProperty("valeur") LocalDateTime valeur) {
        if (valeur == null) {
            throw new IllegalArgumentException("La date de l'événement ne peut pas être nulle.");
        }
        this.valeur = valeur;
    }

    // Constructeur par défaut pour la sérialisation JSON
    private DateEvenement() {
        this(LocalDateTime.now());
    }
}