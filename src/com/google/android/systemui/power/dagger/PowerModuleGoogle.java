package com.google.android.systemui.power.dagger;

import com.android.systemui.CoreStartable;
import com.android.systemui.power.EnhancedEstimates;
import com.android.systemui.power.PowerNotificationWarnings;
import com.android.systemui.power.PowerUI;
import com.android.systemui.power.data.repository.PowerRepositoryModule;
import com.android.systemui.statusbar.policy.ConfigurationController;

import com.google.android.systemui.power.EnhancedEstimatesGoogleImpl;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.ClassKey;
import dagger.multibindings.IntoMap;
import dagger.multibindings.IntoSet;

/** Dagger Module for code in the power package for Google SystemUI. */
@Module(
        includes = {
                PowerRepositoryModule.class,
        }
)
public interface PowerModuleGoogle {
    /** Starts PowerUI. */
    @Binds
    @IntoMap
    @ClassKey(PowerUI.class)
    CoreStartable bindPowerUIStartable(PowerUI impl);

    /** Listen to config changes for PowerUI. */
    @Binds
    @IntoSet
    ConfigurationController.ConfigurationListener bindPowerUIConfigChanges(PowerUI impl);

    /** Binds EnhancedEstimates to EnhancedEstimatesGoogleImpl. */
    @Binds
    EnhancedEstimates bindEnhancedEstimates(EnhancedEstimatesGoogleImpl enhancedEstimates);

    /** Binds WarningsUI to PowerNotificationWarnings. */
    @Binds
    PowerUI.WarningsUI provideWarningsUi(PowerNotificationWarnings controllerImpl);
}
