package Calendar.vo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record ProprietaireEvenement(String valeur) {

    @JsonCreator
    public ProprietaireEvenement(@JsonProperty("valeur") String valeur) {
        if(valeur == null || valeur.isBlank()) {
            throw new IllegalArgumentException("Le propriétaire ne peut pas être vide ou nul.");
        }
        this.valeur = valeur;
    }

    // Constructeur par défaut pour la sérialisation JSON
    private ProprietaireEvenement() {
        this("Propriétaire par défaut");
    }
}