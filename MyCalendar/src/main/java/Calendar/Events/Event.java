package Calendar.Events;

import Calendar.vo.*;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.stream.Stream;

public abstract class Event {
    protected final TitreEvenement titre;
    protected final DateEvenement dateDebut;
    protected final HeureDebut heureDebut;
    protected final DureeEvenement duree;
    protected final ProprietaireEvenement proprietaire;
    protected final EventId id;

    protected Event(TitreEvenement titre, DateEvenement dateDebut, HeureDebut heureDebut,
                    DureeEvenement duree, ProprietaireEvenement proprietaire) {
        this.titre = titre;
        this.dateDebut = dateDebut;
        this.heureDebut = heureDebut;
        this.duree = duree;
        this.proprietaire = proprietaire;
        this.id = EventId.generate();
    }

    // Constructeur alternatif avec ID spécifique
    protected Event(EventId id, TitreEvenement titre, DateEvenement dateDebut, HeureDebut heureDebut,
                    DureeEvenement duree, ProprietaireEvenement proprietaire) {
        this.id = id;
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

    public EventId getId() {
        return id;
    }

    public LocalDateTime getStartDateTime() {
        return dateDebut.valeur()
                .withHour(heureDebut.heure())
                .withMinute(heureDebut.minute());
    }

    public LocalDateTime getEndDateTime() {
        return getStartDateTime().plusMinutes(duree.valeur());
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