package com.google.android.systemui.power

import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.KeyValueListParser
import android.util.Log
import com.android.settingslib.fuelgauge.AVERAGE_TIME_TO_DISCHARGE_UNKNOWN
import com.android.settingslib.fuelgauge.ESTIMATE_MILLIS_UNKNOWN
import com.android.settingslib.fuelgauge.Estimate
import com.android.settingslib.utils.PowerUtil
import com.android.systemui.dagger.SysUISingleton
import com.android.systemui.power.EnhancedEstimates
import java.time.Duration
import javax.inject.Inject

@SysUISingleton
class EnhancedEstimatesGoogleImpl @Inject constructor(private val context: Context) :
    EnhancedEstimates {

    private val parser = KeyValueListParser(',')

    override fun isHybridNotificationEnabled(): Boolean {
        return try {
            val packageInfo =
                context.packageManager.getPackageInfo(
                    "com.google.android.apps.turbo",
                    PackageManager.MATCH_UNINSTALLED_PACKAGES,
                )
            if (packageInfo.applicationInfo?.enabled != true) {
                return false
            }
            updateFlags()
            parser.getBoolean("hybrid_enabled", true)
        } catch (unused: PackageManager.NameNotFoundException) {
            false
        }
    }

    override fun getEstimate(): Estimate {
        val uri =
            Uri.Builder()
                .scheme("content")
                .authority("com.google.android.apps.turbo.estimated_time_remaining")
                .appendPath("time_remaining")
                .build()
        try {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    var isBasedOnUsage = true
                    val usageIndex = cursor.getColumnIndex("is_based_on_usage")
                    if (usageIndex != -1 && cursor.getInt(usageIndex) == 0) {
                        isBasedOnUsage = false
                    }

                    var roundedAverageBatteryLife = -1L
                    val avgIndex = cursor.getColumnIndex("average_battery_life")
                    if (avgIndex != -1) {
                        val avgLife = cursor.getLong(avgIndex)
                        if (avgLife != -1L) {
                            val thresholdMillis =
                                if (Duration.ofMillis(avgLife) >= Duration.ofDays(1)) {
                                    Duration.ofHours(1).toMillis()
                                } else {
                                    Duration.ofMinutes(15).toMillis()
                                }
                            roundedAverageBatteryLife =
                                PowerUtil.roundTimeToNearestThreshold(avgLife, thresholdMillis)
                        }
                    }

                    val estimateIndex = cursor.getColumnIndex("battery_estimate")
                    val batteryEstimate = cursor.getLong(estimateIndex)

                    return Estimate(
                        estimateMillis = batteryEstimate,
                        isBasedOnUsage = isBasedOnUsage,
                        averageDischargeTime = roundedAverageBatteryLife,
                    )
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, "Something went wrong when getting an estimate from Turbo", e)
        }
        return Estimate(
            estimateMillis = ESTIMATE_MILLIS_UNKNOWN.toLong(),
            isBasedOnUsage = false,
            averageDischargeTime = AVERAGE_TIME_TO_DISCHARGE_UNKNOWN.toLong(),
        )
    }

    override fun getLowWarningThreshold(): Long {
        updateFlags()
        return parser.getLong("low_threshold", Duration.ofHours(3L).toMillis())
    }

    override fun getSevereWarningThreshold(): Long {
        updateFlags()
        return parser.getLong("severe_threshold", Duration.ofHours(1L).toMillis())
    }

    override fun getLowWarningEnabled(): Boolean {
        updateFlags()
        return parser.getBoolean("low_warning_enabled", false)
    }

    fun updateFlags() {
        try {
            parser.setString(
                Settings.Global.getString(
                    context.contentResolver,
                    "hybrid_sysui_battery_warning_flags",
                )
            )
        } catch (unused: IllegalArgumentException) {
            Log.e(TAG, "Bad hybrid sysui warning flags")
        }
    }

    companion object {
        private const val TAG = "EnhancedEstimates"
    }
}
