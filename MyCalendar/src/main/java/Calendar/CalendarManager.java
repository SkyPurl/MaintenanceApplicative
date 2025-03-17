package Calendar;

import Calendar.Events.Event;
import Calendar.vo.Periode;
import Calendar.Events.Evenements;

import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import java.util.Iterator;
import java.util.List;

public class CalendarManager {

    private final Evenements evenements;

    public CalendarManager() {
        this.evenements = new Evenements();
    }

    public void ajouterEvenement(Event evenement) {
        evenements.ajouter(evenement);
    }

    public List<Event> eventsDansPeriode(Periode periode) {
        Iterator<Event> it = evenements.occurrences(periode);
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(it, Spliterator.ORDERED),
                false
        ).collect(Collectors.toList());
    }

    public void afficherEvenements() {
        // On affiche seulement les événements initiaux
        // (pas chaque occurrence) ; adaptable selon tes besoins
        Iterator<Event> it = evenements.iterator();
        while (it.hasNext()) {
            System.out.println(it.next().description());
        }
    }
}
