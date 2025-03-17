// RendezVous.java
package Calendar.Events;

import Calendar.vo.*;
import java.util.stream.Stream;

public class RendezVous extends Event {
    public RendezVous(TitreEvenement titre, DateEvenement dateDebut,
                      HeureDebut heureDebut, DureeEvenement duree) {
        super(titre, dateDebut, heureDebut, duree);
    }

    @Override
    public String description() {
        return "RDV : " + titre.valeur() + " le "
                + dateDebut.valeur() + " à " + heureDebut.heure() + "h" + heureDebut.minute();
    }

    @Override
    public Stream<Event> occurrences(Periode periode) {
        return Stream.of((Event) this)
                .filter(e -> !dateDebut.valeur().isBefore(periode.debut()))
                .filter(e -> dateDebut.valeur().isBefore(periode.fin()));
    }
}
