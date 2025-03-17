package Calendar.Events;

import Calendar.vo.*;
import java.util.Iterator;
import java.util.stream.Stream;

public class Anniversaire extends Event {

    private final String personneFetee;

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
        return Stream.<Event>of(this)
                .filter(e -> !dateDebut.valeur().isBefore(periode.debut()))
                .filter(e -> dateDebut.valeur().isBefore(periode.fin()))
                .iterator();
    }

    public String getPersonneFetee() {
        return personneFetee;
    }
}
