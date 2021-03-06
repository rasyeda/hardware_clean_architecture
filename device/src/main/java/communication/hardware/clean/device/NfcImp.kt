package communication.hardware.clean.device

import android.app.Activity
import android.nfc.NfcAdapter
import android.os.Bundle
import avila.domingo.lifecycle.ILifecycleObserver
import communication.hardware.clean.domain.nfc.INfc
import io.reactivex.Observable
import io.reactivex.Single

class NfcImp(
    private val activity: Activity,
    private val flags: Int,
    private val bundle: Bundle
) : INfc, ILifecycleObserver {

    private var rxBus: (ByteArray) -> Unit = {}

    private var nfcAdapter: NfcAdapter? = NfcAdapter.getDefaultAdapter(activity)

    override fun getIdTag(): Observable<ByteArray> =
        Observable.create {
            rxBus = { tag ->
                it.onNext(tag)
            }
        }

    override fun isSupported(): Single<Boolean> = Single.create {
        it.onSuccess(nfcAdapter?.run { true } ?: false)
    }

    override fun resume() {
        nfcAdapter?.enableReaderMode(
            activity,
            { tag ->
                rxBus.invoke(tag.id)
            },
            flags,
            bundle
        )
    }

    override fun pause() {
        nfcAdapter?.disableReaderMode(activity)
    }
}