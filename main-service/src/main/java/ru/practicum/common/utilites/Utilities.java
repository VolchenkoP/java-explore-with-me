package ru.practicum.common.utilites;

import ru.practicum.events.dto.EventRespShort;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Utilities {

    public static List<? extends EventRespShort> addViewsAndConfirmedRequests(
            List<? extends EventRespShort> eventRespShorts,
            Map<Long, Long> confirmedRequests,
            List<Long> views) {

        for (int i = 0; i < eventRespShorts.size(); i++) {
            if ((!views.isEmpty()) && (views.get(i) != 0)) {
                eventRespShorts.get(i).setViews(views.get(i));
            } else {
                eventRespShorts.get(i).setViews(0L);
            }
            eventRespShorts.get(i)
                    .setConfirmedRequests(confirmedRequests
                            .getOrDefault(eventRespShorts.get(i).getId(), 0L));
        }
        return eventRespShorts;
    }

    public static <T> List<T> checkTypes(List<?> list, Class<T> clazz) {
        List<T> result = new ArrayList<>();
        for (Object item : list) {
            try {
                result.add(clazz.cast(item));
            } catch (ClassCastException e) {
                System.out.println(e.getMessage());
            }
        }
        return result;
    }
}
