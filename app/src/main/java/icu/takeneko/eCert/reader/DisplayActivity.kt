package icu.takeneko.eCert.reader

import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.ToastUtils
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

abstract class DisplayActivity<E : Parcelable>(private val dataClass:Class<E>) : AppCompatActivity() {
    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, e ->
        ToastUtils.showLong(formatResString(R.string.text_exception_coroutine, this, e.toString()))
        e.printStackTrace()
    }

    private val externalScope: CoroutineScope = lifecycleScope + coroutineExceptionHandler
    protected lateinit var data: E
    protected lateinit var dataInBytesWithoutSignature:ByteArray

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent.hasExtra("data")) {
            data = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra("data", dataClass)!!
            }else{
                intent.getParcelableExtra<E>("data")!!
            }
        }else{
            logW("Intent has no extra named `data`!")
        }
        if (intent.hasExtra("dataInBytesWithoutSignature")){
            dataInBytesWithoutSignature = intent.getByteArrayExtra("dataInBytesWithoutSignature")!!
        }
    }

    fun launch(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ): Job = externalScope.launch(context, start, block)


}