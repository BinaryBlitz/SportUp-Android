package ru.binaryblitz.SportUp.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

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

    public void getSportTypes(int id, final JsonArrayResponseListener callback) {
        networkService.getSportTypes(id)
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

    public void getEvents(int id, int sportTypeId, String date, final JsonArrayResponseListener callback) {
        networkService.getEvents(id, sportTypeId, date)
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

    public void getEvent(int id, String token, final JsonObjectResponseListener callback) {
        networkService.getEvent(id, token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(new Function<Throwable, ObservableSource<? extends JsonObject>>() {
                    @Override
                    public ObservableSource<? extends JsonObject> apply(Throwable throwable) throws Exception {
                        return Observable.error(throwable);
                    }
                })
                .subscribe(new Observer<JsonObject>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(JsonObject value) {
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

    public void getMyEvents(String token, final JsonArrayResponseListener callback) {
        networkService.getMemberships(token)
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

    public void getInvites(String token, final JsonArrayResponseListener callback) {
        networkService.getInvites(token)
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

    public void sendEvent(JsonObject event, String token, final JsonObjectResponseListener callback) {
        networkService.createEvent(event, token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(new Function<Throwable, ObservableSource<? extends JsonObject>>() {
                    @Override
                    public ObservableSource<? extends JsonObject> apply(Throwable throwable) throws Exception {
                        return Observable.error(throwable);
                    }
                })
                .subscribe(new Observer<JsonObject>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(JsonObject value) {
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

    public void auth(JsonObject body, final JsonObjectResponseListener callback) {
        networkService.authWithPhoneNumber(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(new Function<Throwable, ObservableSource<? extends JsonObject>>() {
                    @Override
                    public ObservableSource<? extends JsonObject> apply(Throwable throwable) throws Exception {
                        return Observable.error(throwable);
                    }
                })
                .subscribe(new Observer<JsonObject>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(JsonObject value) {
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

    public void verify(JsonObject body, String token, final JsonObjectResponseListener callback) {
        networkService.verifyPhoneNumber(body, token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(new Function<Throwable, ObservableSource<? extends JsonObject>>() {
                    @Override
                    public ObservableSource<? extends JsonObject> apply(Throwable throwable) throws Exception {
                        return Observable.error(throwable);
                    }
                })
                .subscribe(new Observer<JsonObject>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(JsonObject value) {
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

    public void createUser(JsonObject body, final JsonObjectResponseListener callback) {
        networkService.createUser(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(new Function<Throwable, ObservableSource<? extends JsonObject>>() {
                    @Override
                    public ObservableSource<? extends JsonObject> apply(Throwable throwable) throws Exception {
                        return Observable.error(throwable);
                    }
                })
                .subscribe(new Observer<JsonObject>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(JsonObject value) {
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

    public void joinEvent(int id, String token, final JsonObjectResponseListener callback) {
        networkService.joinEvent(id, token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(new Function<Throwable, ObservableSource<? extends JsonObject>>() {
                    @Override
                    public ObservableSource<? extends JsonObject> apply(Throwable throwable) throws Exception {
                        return Observable.error(throwable);
                    }
                })
                .subscribe(new Observer<JsonObject>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(JsonObject value) {
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

    public void leaveEvent(int id, String token, final JsonObjectResponseListener callback) {
        networkService.leaveEvent(id, token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(new Function<Throwable, ObservableSource<? extends JsonObject>>() {
                    @Override
                    public ObservableSource<? extends JsonObject> apply(Throwable throwable) throws Exception {
                        return Observable.error(throwable);
                    }
                })
                .subscribe(new Observer<JsonObject>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(JsonObject value) {
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

    public void deleteEvent(int id, String token, final JsonObjectResponseListener callback) {
        networkService.deleteEvent(id, token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(new Function<Throwable, ObservableSource<? extends JsonObject>>() {
                    @Override
                    public ObservableSource<? extends JsonObject> apply(Throwable throwable) throws Exception {
                        return Observable.error(throwable);
                    }
                })
                .subscribe(new Observer<JsonObject>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(JsonObject value) {
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
