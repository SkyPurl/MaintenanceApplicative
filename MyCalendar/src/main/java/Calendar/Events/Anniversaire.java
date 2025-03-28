package Calendar.Events;

import Calendar.vo.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Iterator;
import java.util.stream.Stream;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Anniversaire extends Event {

    private final String personneFetee;

    protected Anniversaire() {
        super();
        personneFetee = "Anniversaire";
    }

    public Anniversaire(
            TitreEvenement titre,
            DateEvenement dateDebut,
            HeureDebut heureDebut,
            DureeEvenement duree,
            ProprietaireEvenement proprietaire,
            String personneFetee
    ) {
        super(titre, dateDebut, heureDebut, duree, proprietaire);
        this.personneFetee = personneFetee;
    }

    @Override
    public String description() {
        return "Anniversaire : " + titre.valeur()
                + " (propriétaire : " + proprietaire.valeur() + ") "
                + " pour " + personneFetee;
    }

    @Override
    public Iterator<Event> occurrences(Periode periode) {
        return singleOccurrenceIterator(periode);
    }

    public String getPersonneFetee() {
        return personneFetee;
    }
}
