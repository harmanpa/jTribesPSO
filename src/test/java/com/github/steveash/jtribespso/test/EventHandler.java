package com.github.steveash.jtribespso.test;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

/**
* @author Steve Ash
*/
public class EventHandler {

    public static EventHandler make(EventBus toListen) {
        EventHandler handler = new EventHandler();
        toListen.register(handler);
        return handler;
    }

    EventHandler() {
    }

    private final Multiset<Class<?>> eventCounts = HashMultiset.create();

    @Subscribe
    public void onEvent(Object event) {
        eventCounts.add(event.getClass());
    }

    public int countForEvent(Class<?> eventClass) {
        return eventCounts.count(eventClass);
    }
}
