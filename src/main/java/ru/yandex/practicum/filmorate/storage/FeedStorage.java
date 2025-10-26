package ru.yandex.practicum.filmorate.storage;


import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

public interface FeedStorage {
    List<Event> getFeedByUserId(Long userId);

    void addEvent(Event event);
}