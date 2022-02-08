package observers;

import jade.GameObject;
import observers.events.Event;

import java.util.ArrayList;
import java.util.List;

public class EventSystem {
    private static List<Observer> observerList = new ArrayList<>();

    public static void addObserver(Observer observer) {
        observerList.add(observer);
    }

    public static void notify(GameObject obj, Event event) {
        for(Observer observer : observerList) {
            observer.onNotify(obj, event);
        }
    }
}
