package ru.binaryblitz.sportup.base;

import android.support.v4.app.Fragment;

import ru.binaryblitz.sportup.deps.Dependencies;

public class BaseFragment extends Fragment {

    protected Dependencies dependencies() {
        return ((BaseActivity) getActivity()).dependencies;
    }

    public void onInternetConnectionError() {
        ((BaseActivity) getActivity()).onInternetConnectionError();
    }
}
