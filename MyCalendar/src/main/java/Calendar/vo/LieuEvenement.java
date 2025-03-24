package Calendar.vo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record LieuEvenement(String valeur) {

    @JsonCreator
    public LieuEvenement(@JsonProperty("valeur") String valeur) {
        if (valeur == null || valeur.isBlank()) {
            throw new IllegalArgumentException("Le lieu ne peut pas être vide.");
        }
        this.valeur = valeur;
    }

    // Constructeur par défaut pour la sérialisation JSON
    private LieuEvenement() {
        this("Lieu par défaut");
    }
}