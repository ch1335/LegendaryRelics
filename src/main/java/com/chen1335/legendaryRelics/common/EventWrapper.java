package com.chen1335.legendaryRelics.common;

import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.EventPriority;
import org.apache.logging.log4j.util.Cast;

import java.util.*;

public class EventWrapper {
    public final Map<Class<? extends Event>, EnumMap<EventPriority, List<EventInvoker<Event>>>> eventHandlers = new HashMap<>();

    public void invokeEvents(Event event) {
        EnumMap<EventPriority, List<EventInvoker<Event>>> eventPriorityListEnumMap = eventHandlers.computeIfAbsent(event.getClass(), (clazz) -> {
            EnumMap<EventPriority, List<EventInvoker<Event>>> enumMap = new EnumMap<>(EventPriority.class);
            for (EventPriority value : EventPriority.values()) {
                enumMap.put(value, new ArrayList<>());
            }
            return enumMap;
        });

        for (List<EventInvoker<Event>> value : eventPriorityListEnumMap.values()) {
            for (EventInvoker<Event> eventInvoker : value) {
                eventInvoker.invoke(event);
            }
        }
    }

    public interface EventInvoker<T extends Event> {
        void invoke(T event);
    }

    public void clear() {
        eventHandlers.clear();
    }

    public <T extends Event> void addListener(Class<T> eventClass, EventPriority priority, EventInvoker<T> eventInvoker) {
        EnumMap<EventPriority, List<EventInvoker<Event>>> eventPriorityListEnumMap = eventHandlers.computeIfAbsent(eventClass, (clazz) -> {
            EnumMap<EventPriority, List<EventInvoker<Event>>> enumMap = new EnumMap<>(EventPriority.class);
            for (EventPriority value : EventPriority.values()) {
                enumMap.put(value, new ArrayList<>());
            }
            return enumMap;
        });

        eventPriorityListEnumMap.get(priority).add(Cast.cast(eventInvoker));
    }

    public <T extends Event> void addListener(Class<T> eventClass, EventInvoker<T> eventInvoker) {
        addListener(eventClass, EventPriority.NORMAL, eventInvoker);
    }
}
