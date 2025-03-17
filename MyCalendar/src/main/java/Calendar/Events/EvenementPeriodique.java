// EvenementPeriodique.java
package Calendar.Events;

import Calendar.vo.*;

import java.util.Iterator;
import java.util.stream.Stream;

public class EvenementPeriodique extends Event {
    private final int frequenceJours;

    public EvenementPeriodique(TitreEvenement titre, DateEvenement dateDebut,
                               HeureDebut heureDebut, DureeEvenement duree, int frequenceJours, ProprietaireEvenement proprietaire) {
        super(titre, dateDebut, heureDebut, duree, proprietaire);
        this.frequenceJours = frequenceJours;
    }

    public int getFrequenceJours() {
        return frequenceJours;
    }

    @Override
    public String description() {
        return "Événement périodique : " + titre.valeur()
                + " (propriétaire : " + proprietaire.valeur() + ") "
                + " tous les " + frequenceJours + " jours";
    }

    @Override
    public Iterator<Event> occurrences(Periode periode) {
        return Stream.iterate(
                        dateDebut.valeur(),
                        d -> d.isBefore(periode.fin()),
                        d -> d.plusDays(frequenceJours)
                )
                .filter(d -> !d.isBefore(periode.debut()))
                .map(d -> (Event) new EvenementPeriodique(
                        titre,
                        new DateEvenement(d),
                        heureDebut,
                        duree,
                        frequenceJours,
                        proprietaire
                ))
                .toList()
                .iterator();
    }
}
