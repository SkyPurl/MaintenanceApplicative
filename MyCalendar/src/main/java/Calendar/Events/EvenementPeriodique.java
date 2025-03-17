package Calendar.Events;

import Calendar.vo.*;
import java.util.stream.Stream;

// Événement périodique
public class EvenementPeriodique extends Event {
    private final int frequenceJours;

    public EvenementPeriodique(TitreEvenement titre, DateEvenement dateDebut, HeureDebut heureDebut, DureeEvenement duree, int frequenceJours) {
        super(titre, dateDebut, heureDebut, duree);
        this.frequenceJours = frequenceJours;
    }

    public int getFrequenceJours() {
        return frequenceJours;
    }

    @Override
    public String description() {
        return "Événement périodique : " + titre.valeur() + " tous les " + frequenceJours + " jours";
    }

    @Override
    public boolean appartientAPeriode(Periode periode) {
        return Stream.iterate(dateDebut.valeur(), d -> d.isBefore(periode.fin()), d -> d.plusDays(frequenceJours))
                .anyMatch(d -> !d.isBefore(periode.debut()));
    }
}
