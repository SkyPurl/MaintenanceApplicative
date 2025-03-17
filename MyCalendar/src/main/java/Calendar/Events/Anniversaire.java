package Calendar.Events;

import Calendar.vo.*;

import java.util.Iterator;

public class Anniversaire extends Event {


    protected Anniversaire(TitreEvenement titre, DateEvenement dateDebut, HeureDebut heureDebut, DureeEvenement duree, ProprietaireEvenement proprietaire) {
        super(titre, dateDebut, heureDebut, duree, proprietaire);
    }

    @Override
    public String description() {
        return "";
    }

    @Override
    public Iterator<Event> occurrences(Periode periode) {
        return null;
    }
}
