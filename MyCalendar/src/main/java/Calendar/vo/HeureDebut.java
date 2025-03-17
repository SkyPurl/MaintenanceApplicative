package Calendar.vo;

public record HeureDebut(int heure, int minute) {
    public HeureDebut {
        if (heure < 0 || heure > 23 || minute < 0 || minute > 59) {
            throw new IllegalArgumentException("Heure ou minute invalide.");
        }
    }
}
