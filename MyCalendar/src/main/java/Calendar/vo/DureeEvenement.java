package Calendar.vo;

public record DureeEvenement(int valeur) {
    public DureeEvenement {
        if (valeur <= 0) {
            throw new IllegalArgumentException("La durée doit être positive.");
        }
    }
}
