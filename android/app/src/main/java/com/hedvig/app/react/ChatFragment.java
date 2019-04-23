package com.hedvig.app.react;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.react.ReactApplication;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactRootView;
import com.facebook.react.common.LifecycleState;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;

import dagger.android.support.AndroidSupportInjection;

public class ChatFragment extends Fragment implements DefaultHardwareBackBtnHandler {

    ReactRootView mReactRootView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        AndroidSupportInjection.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ReactRootView reactRootView = new ReactRootView(requireContext());
        mReactRootView = reactRootView;
        reactRootView.startReactApplication(getReactInstanceManager(), "Chat", getArguments());
        return reactRootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mReactRootView != null) {
            mReactRootView.unmountReactApplication();
            mReactRootView = null;
        }
        if (getReactInstanceManager().getLifecycleState() != LifecycleState.RESUMED) {
            getReactInstanceManager().onHostDestroy(getActivity());
            getReactNativeHost().clear();
        }
    }

    protected ReactNativeHost getReactNativeHost() {
        return ((ReactApplication) getActivity().getApplication()).getReactNativeHost();
    }

    ReactInstanceManager getReactInstanceManager() {
        return getReactNativeHost().getReactInstanceManager();
    }

    @Override
    public void invokeDefaultOnBackPressed() {
        if (getActivity() != null) {
            getActivity().onBackPressed();
        }
    }
}
