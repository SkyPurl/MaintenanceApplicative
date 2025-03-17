package Calendar.Events;

import Calendar.vo.*;

public class Reunion extends Event {
    private final LieuEvenement lieu;
    private final Participants participants;

    public Reunion(TitreEvenement titre, DateEvenement dateDebut, HeureDebut heureDebut, DureeEvenement duree, LieuEvenement lieu, Participants participants) {
        super(titre, dateDebut, heureDebut, duree);
        this.lieu = lieu;
        this.participants = participants;
    }

    @Override
    public String description() {
        return "Réunion : " + titre.valeur() + " à " + lieu.valeur() + " avec " + String.join(", ", participants.noms());
    }
}

