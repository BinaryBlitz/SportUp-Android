package ru.binaryblitz.sportup.deps;

import javax.inject.Singleton;

import dagger.Component;
import ru.binaryblitz.sportup.activities.SelectCityActivity;
import ru.binaryblitz.sportup.server.ServerApi;

@Singleton
@Component(modules = {ServerApi.class,})
public interface Dependencies {
    void inject(SelectCityActivity homeActivity);
}