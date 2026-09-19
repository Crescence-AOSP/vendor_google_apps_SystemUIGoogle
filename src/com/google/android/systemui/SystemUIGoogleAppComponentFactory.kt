package com.google.android.systemui

import android.content.Context
import com.android.systemui.SystemUIAppComponentFactoryBase

class SystemUIGoogleAppComponentFactory : SystemUIAppComponentFactoryBase() {
    override fun createSystemUIInitializer(context: Context) = SystemUIGoogleInitializer(context)
}
