package com.borisenkoda.weathertest.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.borisenkoda.weathertest.app.App;

import rx.Subscriber;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by BDA on 31.01.2016.
 */
public class BaseFragment extends Fragment {
    private CompositeSubscription viewSubscriptions, fragmentSubscriptions;
    {
        setRetainInstance(true);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentSubscriptions = new CompositeSubscription();
        App.inject(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewSubscriptions = new CompositeSubscription();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewSubscriptions.unsubscribe();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        fragmentSubscriptions.unsubscribe();
    }

    private abstract class AutoSubscription<T> extends Subscriber<T> {

    }


    protected abstract class ViewSubscription<T> extends AutoSubscription<T> {
        {
            viewSubscriptions.add(this);
        }
    }

    protected abstract class FragmentSubscription<T> extends AutoSubscription<T> {
        {
            fragmentSubscriptions.add(this);
        }
    }
}
