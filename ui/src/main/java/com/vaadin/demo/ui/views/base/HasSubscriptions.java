package com.vaadin.demo.ui.views.base;

import io.reactivex.disposables.Disposable;

import java.util.Set;

public interface HasSubscriptions {

    default void addSubscription(Disposable disposable) {
        getSubs().add(disposable);
    }

    default void disposeSubscriptions(){
        getSubs().forEach(Disposable::dispose);
        getSubs().clear();
    }

    Set<Disposable> getSubs();


}
