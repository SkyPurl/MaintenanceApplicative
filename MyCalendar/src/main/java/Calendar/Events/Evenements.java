package Calendar.Events;

import Calendar.vo.Periode;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

public class Evenements {

    private final List<Event> interne;

    public Evenements() {
        this.interne = new ArrayList<>();
    }

    public void ajouter(Event e) {
        interne.add(e);
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

    public Iterator<Event> iterator() {
        return interne.iterator();
    }
}
