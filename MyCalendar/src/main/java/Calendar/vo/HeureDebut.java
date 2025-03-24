package Calendar.vo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record HeureDebut(int heure, int minute) {

    @JsonCreator
    public HeureDebut(
            @JsonProperty("heure") int heure,
            @JsonProperty("minute") int minute) {
        if (heure < 0 || heure > 23 || minute < 0 || minute > 59) {
            throw new IllegalArgumentException("Heure ou minute invalide.");
        }
        this.heure = heure;
        this.minute = minute;
    }

    private HeureDebut() {
        this(9, 0);  // 9h00 par défaut
    }
}