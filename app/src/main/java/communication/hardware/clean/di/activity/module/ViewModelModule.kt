package communication.hardware.clean.di.activity.module

import androidx.lifecycle.ViewModel
import communication.hardware.clean.di.activity.ActivityScope
import communication.hardware.clean.di.activity.ViewModelKey
import communication.hardware.clean.domain.interactor.ReadNfcUseCase
import communication.hardware.clean.domain.interactor.ShakingUseCase
import communication.hardware.clean.domain.interactor.TakePictureUseCase
import communication.hardware.clean.domain.interactor.location.GetLocationUseCase
import communication.hardware.clean.domain.interactor.location.GetLocationsUseCase
import communication.hardware.clean.domain.interactor.location.StopLocationsUseCase
import communication.hardware.clean.domain.interactor.sms.GetSmsUseCase
import communication.hardware.clean.domain.interactor.sms.SendSmsUseCase
import communication.hardware.clean.model.mapper.NfcMapper
import communication.hardware.clean.model.mapper.ShakeMapper
import communication.hardware.clean.schedulers.IScheduleProvider
import communication.hardware.clean.ui.MainActivityViewModel
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module
class ViewModelModule {

    @Provides
    @ActivityScope
    @IntoMap
    @ViewModelKey(MainActivityViewModel::class)
    fun provideCameraViewModel(
        getLocationUseCase: GetLocationUseCase,
        getLocationsUseCase: GetLocationsUseCase,
        stopLocationsUseCase: StopLocationsUseCase,
        getSmsUseCase: GetSmsUseCase,
        sendSmsUseCase: SendSmsUseCase,
        shakingUseCase: ShakingUseCase,
        takePictureUseCase: TakePictureUseCase,
        readNfcUseCase: ReadNfcUseCase,
        scheduler: IScheduleProvider,
        shakeMapper: ShakeMapper,
        nfcMapper: NfcMapper
    ): ViewModel = MainActivityViewModel(
        getLocationUseCase,
        getLocationsUseCase,
        stopLocationsUseCase,
        getSmsUseCase,
        sendSmsUseCase,
        shakingUseCase,
        takePictureUseCase,
        readNfcUseCase,
        scheduler,
        shakeMapper,
        nfcMapper
    )
}