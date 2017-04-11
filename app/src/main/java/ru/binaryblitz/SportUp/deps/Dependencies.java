package ru.binaryblitz.SportUp.deps;

import javax.inject.Singleton;

import dagger.Component;

import ru.binaryblitz.SportUp.activities.CreateEventActivity;
import ru.binaryblitz.SportUp.activities.CreateAccountActivity;
import ru.binaryblitz.SportUp.activities.EventActivity;
import ru.binaryblitz.SportUp.activities.EventsMapActivity;
import ru.binaryblitz.SportUp.activities.MainActivity;
import ru.binaryblitz.SportUp.activities.RegistrationActivity;
import ru.binaryblitz.SportUp.activities.SelectCityActivity;
import ru.binaryblitz.SportUp.activities.SportEventsActivity;
import ru.binaryblitz.SportUp.activities.UserListActivity;
import ru.binaryblitz.SportUp.activities.VotesActivity;
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

    void inject(CreateEventActivity createEventActivity);

    void inject(RegistrationActivity registrationActivity);

    void inject(CreateAccountActivity createAccountActivity);

    void inject(MainActivity mainActivity);

    void inject(UserListActivity userListActivity);

    void inject(VotesActivity votesActivity);
}
