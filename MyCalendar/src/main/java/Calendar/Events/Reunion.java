// Reunion.java
package Calendar.Events;

import Calendar.vo.*;

import java.util.Iterator;
import java.util.stream.Stream;

public class Reunion extends Event {
    private final LieuEvenement lieu;
    private final Participants participants;

    public Reunion(TitreEvenement titre, DateEvenement dateDebut, HeureDebut heureDebut,
                   DureeEvenement duree, LieuEvenement lieu, ProprietaireEvenement proprietaire, Participants participants) {
        super(titre, dateDebut, heureDebut, duree, proprietaire);
        this.lieu = lieu;
        this.participants = participants;
    }

    public LieuEvenement getLieu() {
        return lieu;
    }

    public Participants getParticipants() {
        return participants;
    }

    @Override
    public String description() {
        return "Réunion : " + titre.valeur()
                + " (propriétaire : " + proprietaire.valeur() + ") "
                + " à " + lieu.valeur()
                + " avec " + String.join(", ", participants.noms());
    }

    @Override
    public Iterator<Event> occurrences(Periode periode) {
        return singleOccurrenceIterator(periode);
    }
}
