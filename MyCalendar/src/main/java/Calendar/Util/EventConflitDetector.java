package Calendar.Util;

import Calendar.Events.Event;

import java.time.LocalDateTime;
import java.util.Objects;

public class EventConflitDetector {

    public boolean detectConflict(Event event1, Event event2) {
        // Vérifier si les événements concernent le même propriétaire
        // Si les propriétaires sont différents, il n'y a pas de conflit
        if (!Objects.equals(event1.getProprietaire().valeur(), event2.getProprietaire().valeur())) {
            return false;
        }

        // Obtenir le début et la fin des événements
        LocalDateTime debut1 = event1.getDateDebut().valeur()
                .withHour(event1.getHeureDebut().heure())
                .withMinute(event1.getHeureDebut().minute());
        LocalDateTime fin1 = debut1.plusMinutes(event1.getDuree().valeur());

        LocalDateTime debut2 = event2.getDateDebut().valeur()
                .withHour(event2.getHeureDebut().heure())
                .withMinute(event2.getHeureDebut().minute());
        LocalDateTime fin2 = debut2.plusMinutes(event2.getDuree().valeur());

        // Deux événements se chevauchent si:
        // Le début du premier est avant la fin du second ET
        // La fin du premier est après le début du second
        return debut1.isBefore(fin2) && fin1.isAfter(debut2);
    }
}