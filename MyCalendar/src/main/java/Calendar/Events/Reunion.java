// Reunion.java
package Calendar.Events;

import Calendar.vo.*;
import java.util.stream.Stream;

public class Reunion extends Event {
    private final LieuEvenement lieu;
    private final Participants participants;

    public Reunion(TitreEvenement titre, DateEvenement dateDebut, HeureDebut heureDebut,
                   DureeEvenement duree, LieuEvenement lieu, Participants participants) {
        super(titre, dateDebut, heureDebut, duree);
        this.lieu = lieu;
        this.participants = participants;
    }

    @Override
    public String description() {
        return "Réunion : " + titre.valeur() + " à "
                + lieu.valeur() + " avec " + String.join(", ", participants.noms());
    }

    @Override
    public Stream<Event> occurrences(Periode periode) {
        return Stream.of((Event) this)
                .filter(e -> !dateDebut.valeur().isBefore(periode.debut()))
                .filter(e -> dateDebut.valeur().isBefore(periode.fin()));
    }
}
