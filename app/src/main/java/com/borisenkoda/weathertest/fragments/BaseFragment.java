package com.borisenkoda.weathertest.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Toast;

import com.borisenkoda.weathertest.R;
import com.borisenkoda.weathertest.activity.MainActivity;
import com.borisenkoda.weathertest.app.App;
import com.borisenkoda.weathertest.helpers.Easy;
import com.borisenkoda.weathertest.helpers.FragmentStack;

import rx.Subscriber;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by BDA on 31.01.2016.
 */
public class BaseFragment extends Fragment {
    private CompositeSubscription viewSubscriptions;
    {
        setRetainInstance(true);
        super.setArguments(new Bundle());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.inject(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewSubscriptions = new CompositeSubscription();
    }

    @Override
    public void onResume() {
        super.onResume();
        setHasOptionsMenu(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewSubscriptions.unsubscribe();
    }



    private abstract class AutoSubscription<T> extends Subscriber<T> {

    }


    protected abstract class ViewSubscription<T> extends AutoSubscription<T> {
        {
            viewSubscriptions.add(this);
        }

        @Override
        public void onError(Throwable e) {
            Easy.logE(e.getMessage());
            Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
        }
    }



    public FragmentStack getFragmentStack(){
        return ((MainActivity) getActivity()).getFragmentStack();
    }

}
