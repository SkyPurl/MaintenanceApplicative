package Calendar.vo;

import java.time.LocalDateTime;

public record DateEvenement(LocalDateTime valeur) {
    public DateEvenement {
        if (valeur == null) {
            throw new IllegalArgumentException("La date de l'événement ne peut pas être nulle.");
        }
    }
}
