package Calendar.Events;

import Calendar.vo.*;
import java.util.Iterator;

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

    public TitreEvenement getTitre() {
        return titre;
    }

    public DateEvenement getDateDebut() {
        return dateDebut;
    }

    public HeureDebut getHeureDebut() {
        return heureDebut;
    }

    public DureeEvenement getDuree() {
        return duree;
    }

    public abstract String description();

    public abstract Iterator<Event> occurrences(Periode periode);
}
