package Calendar.vo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public record Periode(LocalDateTime debut, LocalDateTime fin) {

    @JsonCreator
    public Periode(
            @JsonProperty("debut") LocalDateTime debut,
            @JsonProperty("fin") LocalDateTime fin) {
        if (debut == null || fin == null || debut.isAfter(fin)) {
            throw new IllegalArgumentException("Période invalide.");
        }
        this.debut = debut;
        this.fin = fin;
    }

    // Constructeur par défaut pour la sérialisation JSON
    private Periode() {
        this(LocalDateTime.now(), LocalDateTime.now().plusDays(1));
    }
}