package app.simple.inure.viewmodels.viewers

import android.app.Application
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import app.simple.inure.apk.utils.MetaUtils
import app.simple.inure.extensions.viewmodels.WrappedViewModel
import app.simple.inure.models.ProviderInfoModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProvidersViewModel(application: Application, val packageInfo: PackageInfo) : WrappedViewModel(application) {

    private val providers: MutableLiveData<MutableList<ProviderInfoModel>> by lazy {
        MutableLiveData<MutableList<ProviderInfoModel>>().also {
            getProvidersData("")
        }
    }

    fun getProviders(): LiveData<MutableList<ProviderInfoModel>> {
        return providers
    }

    fun getProvidersData(keyword: String) {
        viewModelScope.launch(Dispatchers.Default) {
            kotlin.runCatching {
                val list = arrayListOf<ProviderInfoModel>()

                val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    PackageManager.GET_PROVIDERS or PackageManager.MATCH_DISABLED_COMPONENTS
                } else {
                    @Suppress("deprecation")
                    PackageManager.GET_PROVIDERS or PackageManager.GET_DISABLED_COMPONENTS
                }

                for (pi in getApplication<Application>().packageManager.getPackageInfo(packageInfo.packageName, flags).providers) {
                    val providerInfoModel = ProviderInfoModel()


                    providerInfoModel.providerInfo = pi
                    providerInfoModel.name = pi.name
                    providerInfoModel.authority = pi.authority
                    providerInfoModel.isExported = pi.exported
                    providerInfoModel.permissions = pi.readPermission + pi.writePermission

                    with(StringBuilder()) {
                        append(" | ")
                        append(MetaUtils.getServiceFlags(pi.flags, getApplication()))

                        providerInfoModel.status = this.toString()
                    }

                    if (providerInfoModel.name.lowercase().contains(keyword.lowercase())) {
                        list.add(providerInfoModel)
                    }
                }

                list.sortBy {
                    it.name.substring(it.name.lastIndexOf(".") + 1)
                }

                providers.postValue(list)
            }.getOrElse {
                if (it is NullPointerException) {
                    notFound.postValue(88)
                } else {
                    postError(it)
                }
            }
        }
    }
}