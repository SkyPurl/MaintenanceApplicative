package Calendar.vo;

public record ProprietaireEvenement(String valeur) {
    public ProprietaireEvenement {
        if(valeur == null || valeur.isBlank()) {
            throw new IllegalArgumentException("Le propriétaire ne peut pas être vide ou nul.");
        }
    }
}
