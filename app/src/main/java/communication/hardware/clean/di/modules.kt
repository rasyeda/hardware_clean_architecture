package communication.hardware.clean.di

import android.annotation.SuppressLint
import android.hardware.SensorManager
import android.nfc.NfcAdapter
import android.os.Bundle
import android.view.SurfaceView
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import avila.domingo.lifecycle.ILifecycleObserver
import avila.domingo.lifecycle.LifecycleManager
import com.google.android.gms.location.LocationRequest
import communication.hardware.clean.device.LocationImp
import communication.hardware.clean.device.NfcImp
import communication.hardware.clean.device.SensorImp
import communication.hardware.clean.device.SmsImp
import communication.hardware.clean.device.camera.*
import communication.hardware.clean.device.camera.model.mapper.CameraSideMapper
import communication.hardware.clean.di.qualifiers.*
import communication.hardware.clean.domain.camera.ICamera
import communication.hardware.clean.domain.camera.model.CameraSide
import communication.hardware.clean.domain.interactor.ReadNfcUseCase
import communication.hardware.clean.domain.interactor.ShakingUseCase
import communication.hardware.clean.domain.interactor.TakePictureUseCase
import communication.hardware.clean.domain.interactor.location.GetLocationUseCase
import communication.hardware.clean.domain.interactor.location.GetLocationsUseCase
import communication.hardware.clean.domain.interactor.location.StopLocationsUseCase
import communication.hardware.clean.domain.interactor.sms.GetSmsUseCase
import communication.hardware.clean.domain.interactor.sms.SendSmsUseCase
import communication.hardware.clean.domain.location.ILocation
import communication.hardware.clean.domain.nfc.INfc
import communication.hardware.clean.domain.sensor.ISensor
import communication.hardware.clean.domain.sms.ISms
import communication.hardware.clean.model.mapper.NfcMapper
import communication.hardware.clean.model.mapper.PictureMapper
import communication.hardware.clean.model.mapper.ShakeMapper
import communication.hardware.clean.schedulers.IScheduleProvider
import communication.hardware.clean.schedulers.ScheduleProviderImp
import communication.hardware.clean.ui.MainActivityViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.binds
import org.koin.dsl.module
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

val activityModule = module {
    lateinit var referenceActivity: AppCompatActivity
    factory { (activity: AppCompatActivity) ->
        referenceActivity = activity
        LifecycleManager(
            arrayOf(get(Camera), get(Location), get(Nfc), get(Sms), get(Sensor)),
            activity.lifecycle
        )
        Unit
    }
    factory { referenceActivity }
}

val viewModelModule = module {
    viewModel {
        MainActivityViewModel(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }
}

val useCaseModule = module {
    single { GetLocationUseCase(get()) }
    single { GetLocationsUseCase(get()) }
    single { StopLocationsUseCase(get()) }
    single { GetSmsUseCase(get()) }
    single { SendSmsUseCase(get()) }
    single { TakePictureUseCase(get()) }
    single { ShakingUseCase(get()) }
    single { ReadNfcUseCase(get()) }
}

val cameraModule = module {
    single<ICamera> { CameraImp(get(), get()) }

    single {
        SurfaceView(get()).apply {
            layoutParams =
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
        }
    }

    single(Camera) {
        NativeCameraManager(
            get(),
            get(),
            get(),
            get(),
            get(),
            get()
        )
    } binds arrayOf(
        ICameraSide::class,
        INativeCamera::class,
        ILifecycleObserver::class
    )

    single { CameraRotationUtil(get(), get(Camera), get()) }

    single { CameraSide.BACK }

    single { 640..2160 }
}

val locationModule = module {
    single<ILocation>(Location) {
        LocationImp(
            androidContext(),
            get(Interval),
            get(FastInterval),
            get(Priority),
            get(MinAccuracy)
        )
    }

    single(Interval) { (get() as TimeUnit).toMillis(1) }
    single(FastInterval) { (get() as TimeUnit).toMillis(1) }
    single(Priority) { LocationRequest.PRIORITY_HIGH_ACCURACY }
    single(MinAccuracy) { 200 }
    single { TimeUnit.SECONDS }
}

val nfcModule = module {
    single<INfc>(Nfc) { NfcImp(get(), get(), get()) }
    single { NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK or NfcAdapter.FLAG_READER_NFC_B }
    single {
        Bundle().apply { putInt(NfcAdapter.EXTRA_READER_PRESENCE_CHECK_DELAY, 1000) }
    }
}

val sensorModule = module {
    single<ISensor>(Sensor) {
        SensorImp(
            androidContext(),
            get(SamplingPeriodUs),
            get(SakeThreshold)
        )
    }
    single(SamplingPeriodUs) { SensorManager.SENSOR_DELAY_NORMAL }
    single(SakeThreshold) { 600 }
}

val smsModule = module {
    single<ISms>(Sms) { SmsImp(androidContext()) }
}

@SuppressLint("ConstantLocale")
val mapperModule = module {
    single { ShakeMapper(get()) }
    single { NfcMapper() }
    single { SimpleDateFormat(get(), Locale.getDefault()) }
    single { PictureMapper() }
    single { CameraSideMapper() }
    single { "HH:mm:ss" }
}

val scheduleModule = module {
    single<IScheduleProvider> { ScheduleProviderImp() }
}