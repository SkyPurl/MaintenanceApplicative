package Calendar.Events;

import Calendar.vo.*;
import java.util.Iterator;
import java.util.stream.Stream;

public class RendezVous extends Event {

    public RendezVous(TitreEvenement titre, DateEvenement dateDebut, HeureDebut heureDebut, DureeEvenement duree, ProprietaireEvenement proprietaire) {
        super(titre, dateDebut, heureDebut, duree, proprietaire);
    }

    @Override
    public String description() {
        return "RDV : " + titre.valeur()
                + " (propriétaire : " + proprietaire.valeur() + ") "
                + " le " + dateDebut.valeur() + " à "
                + heureDebut.heure() + "h" + heureDebut.minute();
    }

    @Override
    public Iterator<Event> occurrences(Periode periode) {
        return Stream.<Event>of(this)
                .filter(e -> !dateDebut.valeur().isBefore(periode.debut()))
                .filter(e -> dateDebut.valeur().isBefore(periode.fin()))
                .toList()
                .iterator();
    }
}
