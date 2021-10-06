package app.simple.inure.apk.utils

import android.content.Context
import android.content.pm.ActivityInfo
import android.view.WindowManager
import app.simple.inure.R
import app.simple.inure.util.StringUtils

object MetaUtils {
    fun getLaunchMode(mode: Int, context: Context): String {
        return when (mode) {
            ActivityInfo.LAUNCH_MULTIPLE -> context.getString(R.string.multiple)
            ActivityInfo.LAUNCH_SINGLE_INSTANCE -> context.getString(R.string.single_instance)
            ActivityInfo.LAUNCH_SINGLE_TASK -> context.getString(R.string.single_task)
            ActivityInfo.LAUNCH_SINGLE_TOP -> context.getString(R.string.single_top)
            else -> context.getString(R.string.not_available)
        }
    }

    fun getOrientationString(orientation: Int, context: Context): String {
        return when (orientation) {
            ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED -> context.getString(R.string.unspecified)
            ActivityInfo.SCREEN_ORIENTATION_BEHIND -> context.getString(R.string.behind)
            ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR -> context.getString(R.string.full_sensor)
            ActivityInfo.SCREEN_ORIENTATION_FULL_USER -> context.getString(R.string.full_user)
            ActivityInfo.SCREEN_ORIENTATION_LOCKED -> context.getString(R.string.locked)
            ActivityInfo.SCREEN_ORIENTATION_NOSENSOR -> context.getString(R.string.no_sensor)
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE -> context.getString(R.string.landscape)
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT -> context.getString(R.string.portrait)
            ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT -> context.getString(R.string.reverse_portrait)
            ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE -> context.getString(R.string.reverse_landscape)
            ActivityInfo.SCREEN_ORIENTATION_USER -> context.getString(R.string.user)
            ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE -> context.getString(R.string.sensor_landscape)
            ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT -> context.getString(R.string.sensor_portrait)
            ActivityInfo.SCREEN_ORIENTATION_SENSOR -> context.getString(R.string.sensor)
            ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE -> context.getString(R.string.user_landscape)
            ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT -> context.getString(R.string.user_portrait)
            else -> context.getString(R.string.not_available)
        }
    }

    @Suppress("deprecation")
    fun getSoftInputString(flag: Int): String {
        val builder = StringBuilder()

        if (flag and WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING != 0) builder.append("Adjust Nothing, ")
        if (flag and WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN != 0) builder.append("Adjust pan, ")
        if (flag and WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE != 0) builder.append("Adjust resize, ")
        if (flag and WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED != 0) builder.append("Adjust unspecified, ")
        if (flag and WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN != 0) builder.append("Always hidden, ")
        if (flag and WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE != 0) builder.append("Always visible, ")
        if (flag and WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN != 0) builder.append("Hidden, ")
        if (flag and WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE != 0) builder.append("Visible, ")
        if (flag and WindowManager.LayoutParams.SOFT_INPUT_STATE_UNCHANGED != 0) builder.append("Unchanged, ")
        if (flag and WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED != 0) builder.append("Unspecified, ")
        if (flag and WindowManager.LayoutParams.SOFT_INPUT_IS_FORWARD_NAVIGATION != 0) builder.append("ForwardNav, ")
        if (flag and WindowManager.LayoutParams.SOFT_INPUT_MASK_ADJUST != 0) builder.append("Mask adjust, ")
        if (flag and WindowManager.LayoutParams.SOFT_INPUT_MASK_STATE != 0) builder.append("Mask state, ")
        if (flag and WindowManager.LayoutParams.SOFT_INPUT_MODE_CHANGED != 0) builder.append("Mode changed, ")

        StringUtils.checkStringBuilderEnd(builder)
        val result = builder.toString()
        return if (result == "") "null" else result
    }

    fun getForegroundServiceType(type: Int, context: Context): String {
        val builder = StringBuilder()

        with(builder) {
            if ((0 and type) == 0) createString(context.getString(R.string.non_foreground))
            if ((1 shl 0 and type) == 1 shl 0) createString(context.getString(R.string.data_sync))
            if ((1 shl 1 and type) == 1 shl 1) createString(context.getString(R.string.media_playback))
            if ((1 shl 2 and type) == 1 shl 2) createString(context.getString(R.string.phone_call))
            if ((1 shl 3 and type) == 1 shl 3) createString(context.getString(R.string.location))
            if ((1 shl 4 and type) == 1 shl 4) createString(context.getString(R.string.connected_devices))
            if ((1 shl 5 and type) == 1 shl 5) createString(context.getString(R.string.media_projection))
            if ((1 shl 6 and type) == 1 shl 6) createString(context.getString(R.string.camera))
            if ((1 shl 7 and type) == 1 shl 7) createString(context.getString(R.string.microphone))
            if ((1 shl -1 and type) == 1 shl -1) createString(context.getString(R.string.manifest))
        }

        return builder.toString()
    }

    fun getServiceFlags(type: Int, context: Context): String {
        val builder = StringBuilder()

        with(builder) {
            if (type and 0x0001 != 0) createString(context.getString(R.string.stop_with_task))
            if (type and 0x0002 != 0) createString(context.getString(R.string.isolated_process))
            if (type and 0x0004 != 0) createString(context.getString(R.string.external_service))
            if (type and 0x0008 != 0) createString(context.getString(R.string.app_zygote))
            if (type and 0x10000 != 0) createString(context.getString(R.string.visible_to_instant_apps))
            if (type and 0x40000000 != 0) createString(context.getString(R.string.single_user))

            if (isBlank()) {
                append(context.getString(R.string.no_flags))
            }
        }

        return builder.toString()
    }

    private fun StringBuilder.createString(string: String) {
        if (isNotEmpty()) {
            append(" | $string")
        } else {
            append(string)
        }
    }
}