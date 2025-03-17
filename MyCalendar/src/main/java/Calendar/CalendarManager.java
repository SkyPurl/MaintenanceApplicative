package Calendar;

import Calendar.Events.Event;
import Calendar.vo.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CalendarManager {
    private final List<Event> events;

    public CalendarManager() {
        this.events = new ArrayList<>();
    }

    public void ajouterEvenement(Event evenement) {
        events.add(evenement);
    }

    public List<Event> eventsDansPeriode(Periode periode) {
        return events.stream()
                // Pour chaque Event, récupérer son flux d’occurrences
                .flatMap(e -> e.occurrences(periode))
                // Collecter toutes les occurrences en une liste
                .collect(Collectors.toList());
    }

    public void afficherEvenements() {
        events.forEach(e -> System.out.println(e.description()));
    }
}
