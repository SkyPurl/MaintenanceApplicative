package Calendar.Events;

import Calendar.vo.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.stream.Stream;

public class RendezVous extends Event {

    public RendezVous(TitreEvenement titre, DateEvenement dateDebut, HeureDebut heureDebut, DureeEvenement duree, ProprietaireEvenement proprietaire) {
        super(titre, dateDebut, heureDebut, duree, proprietaire);
    }

    @Override
    public String description() {
        LocalDateTime dt = dateDebut.valeur();
        int heures = dt.getHour();
        int minutes = dt.getMinute();
        LocalDate date = dt.toLocalDate();

        String heureFormatted = String.format("%02d", minutes);

        return "RDV : " + titre.valeur()
                + " (propriétaire : " + proprietaire.valeur() + ") "
                + "le " + date + " à " + heures + "h" + heureFormatted;
    }


    @Override
    public Iterator<Event> occurrences(Periode periode) {
        return singleOccurrenceIterator(periode);
    }
}
