package Calendar.Events;

import Calendar.vo.*;
import java.util.Iterator;
import java.util.stream.Stream;

public abstract class Event {
    protected final TitreEvenement titre;
    protected final DateEvenement dateDebut;
    protected final HeureDebut heureDebut;
    protected final DureeEvenement duree;
    protected final ProprietaireEvenement proprietaire;


    protected Event(TitreEvenement titre, DateEvenement dateDebut, HeureDebut heureDebut, DureeEvenement duree, ProprietaireEvenement proprietaire) {
        this.titre = titre;
        this.dateDebut = dateDebut;
        this.heureDebut = heureDebut;
        this.duree = duree;
        this.proprietaire = proprietaire;
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

    public ProprietaireEvenement getProprietaire() {
        return proprietaire;
    }

    protected Iterator<Event> singleOccurrenceIterator(Periode periode) {
        return Stream.<Event>of(this)
                .filter(e -> !dateDebut.valeur().isBefore(periode.debut()))
                .filter(e -> dateDebut.valeur().isBefore(periode.fin()))
                .iterator();
    }

    public abstract String description();

    public abstract Iterator<Event> occurrences(Periode periode);
}
