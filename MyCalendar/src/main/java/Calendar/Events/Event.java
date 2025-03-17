// Event.java
package Calendar.Events;

import Calendar.vo.*;
import java.util.stream.Stream;

public abstract class Event {
    protected final TitreEvenement titre;
    protected final DateEvenement dateDebut;
    protected final HeureDebut heureDebut;
    protected final DureeEvenement duree;

    protected Event(TitreEvenement titre, DateEvenement dateDebut, HeureDebut heureDebut, DureeEvenement duree) {
        this.titre = titre;
        this.dateDebut = dateDebut;
        this.heureDebut = heureDebut;
        this.duree = duree;
    }

    public TitreEvenement getTitre() { return titre; }
    public DateEvenement getDateDebut() { return dateDebut; }
    public HeureDebut getHeureDebut() { return heureDebut; }
    public DureeEvenement getDuree() { return duree; }

    public abstract String description();

    // Méthode à implémenter dans chaque sous-classe : génère TOUTES les occurrences
    // se trouvant dans la période donnée, sous forme de Stream d’Event.
    public abstract Stream<Event> occurrences(Periode periode);
}
