package ru.binaryblitz.SportUp.deps;

import javax.inject.Singleton;

import dagger.Component;
import ru.binaryblitz.SportUp.activities.EventActivity;
import ru.binaryblitz.SportUp.activities.EventsMapActivity;
import ru.binaryblitz.SportUp.activities.SelectCityActivity;
import ru.binaryblitz.SportUp.activities.SportEventsActivity;
import ru.binaryblitz.SportUp.fragments.SportsListFragment;
import ru.binaryblitz.SportUp.fragments.UserEventsFragment;
import ru.binaryblitz.SportUp.server.ServerApi;

@Singleton
@Component(modules = {ServerApi.class,})
public interface Dependencies {
    void inject(SelectCityActivity selectCityActivity);

    void inject(SportsListFragment sportsListFragment);

    void inject(SportEventsActivity sportEventsActivity);

    void inject(EventsMapActivity eventsMapActivity);

    void inject(EventActivity eventActivity);

    void inject(UserEventsFragment userEventsFragment);
}
