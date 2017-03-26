package com.vaadin.demo.ui.views.base;

import com.vaadin.ui.VerticalLayout;
import io.reactivex.disposables.Disposable;

import java.util.HashSet;
import java.util.Set;

public class VerticalLayoutView extends VerticalLayout implements HasSubscriptions {

    private Set<Disposable> subs = new HashSet<>();

    @Override
    public Set<Disposable> getSubs() {
        return subs;
    }

    @Override
    public void detach() {
        super.detach();
        disposeSubscriptions();
    }
}
