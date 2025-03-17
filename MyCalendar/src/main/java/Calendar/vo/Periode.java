package Calendar.vo;

import java.time.LocalDateTime;

public record Periode(LocalDateTime debut, LocalDateTime fin) {
    public Periode {
        if (debut == null || fin == null || debut.isAfter(fin)) {
            throw new IllegalArgumentException("Période invalide.");
        }
    }
}