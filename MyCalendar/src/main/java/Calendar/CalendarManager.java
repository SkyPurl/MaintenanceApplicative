package Calendar;

import Calendar.Events.*;
import Calendar.vo.*;
import java.util.ArrayList;
import java.util.List;

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
                .filter(e -> e.appartientAPeriode(periode))
                .toList();
    }

    public void afficherEvenements() {
        events.forEach(event -> System.out.println(event.description()));
    }
}