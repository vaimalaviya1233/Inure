package app.simple.inure.viewmodels.factory

import android.app.Application
import android.content.pm.PackageInfo
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.simple.inure.model.ActivityInfoModel
import app.simple.inure.viewmodels.subviewers.ActivityInfoViewModel

class MetaInfoFactory(private val application: Application, private val packageId: String, private val activityInfoModel: ActivityInfoModel, val packageInfo: PackageInfo) : ViewModelProvider.AndroidViewModelFactory(application) {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        when {
            modelClass.isAssignableFrom(ActivityInfoViewModel::class.java) -> {
                return ActivityInfoViewModel(application, activityInfoModel, packageId, packageInfo) as T
            }
            else -> {
                throw IllegalArgumentException("Nope, Wrong Viewmodel!!")
            }
        }
    }
}