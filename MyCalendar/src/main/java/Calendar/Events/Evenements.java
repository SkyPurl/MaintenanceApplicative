package Calendar.Events;

import Calendar.vo.EventId;
import Calendar.vo.Periode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

public class Evenements implements Iterable<Event> {

    private final List<Event> interne;

    public Evenements() {
        this.interne = new ArrayList<>();
    }

    public void ajouter(Event e) {
        interne.add(e);
    }

    public boolean supprimer(EventId id) {
        return interne.removeIf(e -> e.getId().equals(id));
    }

    public Optional<Event> trouverParId(EventId id) {
        return interne.stream()
                .filter(e -> e.getId().equals(id))
                .findFirst();
    }

    public Iterator<Event> occurrences(Periode periode) {
        var occurrences = interne.stream()
                .flatMap(e -> {
                    Iterator<Event> it = e.occurrences(periode);
                    return StreamSupport.stream(
                            Spliterators.spliteratorUnknownSize(it, Spliterator.ORDERED),
                            false
                    );
                })
                .toList();
        return occurrences.iterator();
    }

    @Override
    public Iterator<Event> iterator() {
        return new ArrayList<>(interne).iterator(); // Copie défensive
    }
}