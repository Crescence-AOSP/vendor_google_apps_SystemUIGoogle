package com.google.android.systemui;

import android.content.Context;

import com.android.systemui.SystemUIInitializer;
import com.android.systemui.dagger.GlobalRootComponent;

import com.google.android.systemui.dagger.DaggerSysUIGoogleGlobalRootComponent;

public final class SystemUIGoogleInitializer extends SystemUIInitializer {
    public SystemUIGoogleInitializer(Context context) {
        super(context);
    }

    @Override
    protected GlobalRootComponent.Builder getGlobalRootComponentBuilder() {
        return DaggerSysUIGoogleGlobalRootComponent.builder();
    }
}
