package com.google.android.systemui.dagger;

import com.android.systemui.dagger.GlobalModule;
import com.android.systemui.dagger.GlobalRootComponent;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {GlobalModule.class})
public interface SysUIGoogleGlobalRootComponent extends GlobalRootComponent {

    @Component.Builder
    interface Builder extends GlobalRootComponent.Builder {
        SysUIGoogleGlobalRootComponent build();
    }

    @Override
    SysUIGoogleSysUIComponent.Builder getSysUIComponent();
}
