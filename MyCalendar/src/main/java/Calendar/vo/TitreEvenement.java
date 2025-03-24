package Calendar.vo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record TitreEvenement(String valeur) {

    @JsonCreator
    public TitreEvenement(@JsonProperty("valeur") String valeur) {
        if (valeur == null || valeur.isBlank()) {
            throw new IllegalArgumentException("Le titre ne peut pas être vide.");
        }
        this.valeur = valeur;
    }

    // Constructeur par défaut pour la sérialisation JSON
    private TitreEvenement() {
        this("Titre par défaut");
    }
}