package Calendar.vo;

public record LieuEvenement(String valeur) {
    public LieuEvenement {
        if (valeur == null || valeur.isBlank()) {
            throw new IllegalArgumentException("Le lieu ne peut pas être vide.");
        }
    }
}
