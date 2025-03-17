package Calendar.Events;

import Calendar.vo.*;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class RendezVous extends Event {

    public RendezVous(TitreEvenement titre, DateEvenement dateDebut, HeureDebut heureDebut, DureeEvenement duree) {
        super(titre, dateDebut, heureDebut, duree);
    }

    @Override
    public String description() {
        return "RDV : " + titre.valeur() + " le " + dateDebut.valeur() + " à " + heureDebut.heure() + "h" + heureDebut.minute();
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
