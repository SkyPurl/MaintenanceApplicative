package Calendar.vo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record DureeEvenement(int valeur) {

    @JsonCreator
    public DureeEvenement(@JsonProperty("valeur") int valeur) {
        if (valeur <= 0) {
            throw new IllegalArgumentException("La durée doit être positive.");
        }
        this.valeur = valeur;
    }

    // Constructeur par défaut pour la sérialisation JSON
    private DureeEvenement() {
        this(60);  // Valeur par défaut de 60 minutes
    }
}