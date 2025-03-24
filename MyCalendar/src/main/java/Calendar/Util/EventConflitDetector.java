package Calendar.Util;

import Calendar.Events.Event;

import java.time.LocalDateTime;

public class EventConflitDetector {

    public boolean detectConflict(Event event1, Event event2) {
        // Obtenir le début et la fin des événements
        LocalDateTime debut1 = event1.getDateDebut().valeur()
                .withHour(event1.getHeureDebut().heure())
                .withMinute(event1.getHeureDebut().minute());
        LocalDateTime fin1 = debut1.plusMinutes(event1.getDuree().valeur());

        LocalDateTime debut2 = event2.getDateDebut().valeur()
                .withHour(event2.getHeureDebut().heure())
                .withMinute(event2.getHeureDebut().minute());
        LocalDateTime fin2 = debut2.plusMinutes(event2.getDuree().valeur());

        return debut1.isBefore(fin2) && fin1.isAfter(debut2);
    }
}