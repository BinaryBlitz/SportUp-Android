package ru.binaryblitz.sportup.server;

import com.google.gson.JsonArray;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class EndpointsService {
    private final ApiEndpoints networkService;

    public EndpointsService(ApiEndpoints networkService) {
        this.networkService = networkService;
    }

    public void getCityList(final JsonArrayResponseListener callback) {
        networkService.getCitiesList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(new Function<Throwable, ObservableSource<? extends JsonArray>>() {
                    @Override
                    public ObservableSource<? extends JsonArray> apply(Throwable throwable) throws Exception {
                        return Observable.error(throwable);
                    }
                })
                .subscribe(new Observer<JsonArray>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(JsonArray value) {
                        callback.onSuccess(value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.onError(e.getLocalizedMessage());
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    public void getSportTypes(final JsonArrayResponseListener callback) {
        networkService.getSportTypes()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(new Function<Throwable, ObservableSource<? extends JsonArray>>() {
                    @Override
                    public ObservableSource<? extends JsonArray> apply(Throwable throwable) throws Exception {
                        return Observable.error(throwable);
                    }
                })
                .subscribe(new Observer<JsonArray>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(JsonArray value) {
                        callback.onSuccess(value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.onError(e.getLocalizedMessage());
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    public void getEvents(int id, String date, final JsonArrayResponseListener callback) {
        networkService.getEvents(id, date)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(new Function<Throwable, ObservableSource<? extends JsonArray>>() {
                    @Override
                    public ObservableSource<? extends JsonArray> apply(Throwable throwable) throws Exception {
                        return Observable.error(throwable);
                    }
                })
                .subscribe(new Observer<JsonArray>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(JsonArray value) {
                        callback.onSuccess(value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        callback.onError(e.getLocalizedMessage());
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }
}
