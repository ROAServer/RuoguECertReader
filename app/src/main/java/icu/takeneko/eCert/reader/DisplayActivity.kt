package icu.takeneko.eCert.reader

import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.TagLostException
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.ToastUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.progressindicator.CircularProgressIndicator
import icu.takeneko.eCert.reader.data.DataAdapters
import icu.takeneko.eCert.reader.nfc.Nfc
import icu.takeneko.eCert.reader.nfc.NfcException
import icu.takeneko.eCert.reader.nfc.NfcTag
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import java.io.IOException
import java.lang.IllegalArgumentException
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
    private var nfcTag: NfcTag? = null

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

    override fun onResume() {
        super.onResume()
        setupNfc()
    }

    override fun onPause() {
        super.onPause()
        Nfc.finalizeNfc(this)
    }

    fun launch(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ): Job = externalScope.launch(context, start, block)

    private fun readTagIfAvailable() {
        if (nfcTag == null) return
        val circularProgressIndicator = CircularProgressIndicator(this).apply {
            isIndeterminate = true
            setTheme(R.style.Theme_RuoguECertReader)
        }
        val waitingDialog = MaterialAlertDialogBuilder(this)
            .setTitle(R.string.text_reading_tag)
            .setView(LinearLayout(this).apply {
                setPadding(10.dp, 10.dp, 10.dp, 25.dp)
                setLayoutParams(
                    ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                )
                gravity = Gravity.CENTER
                addView(circularProgressIndicator)
            })
            .setCancelable(false)
            .create()
        waitingDialog.show()
        externalScope.launch(Dispatchers.IO) {
            try {
                nfcTag!!.apply {
                    connect()
                    val blocks = determineWritableBlocks()
                    logI("Blocks: ${blocks.joinToString(", ")}")
                    val content = readBlocksToByteArray(*(blocks.toIntArray()))
                    logI(hexView(content))
                    try {
                        DataAdapters.tryStartActivity(this@DisplayActivity, content)
                    } catch (e: IllegalArgumentException) {
                        e.printStackTrace()
                        launch(Dispatchers.Main) {
                            waitingDialog.dismiss()
                            MaterialAlertDialogBuilder(this@DisplayActivity)
                                .setTitle(R.string.text_illegal_tag)
                                .setPositiveButton(R.string.text_continue) { _, _ -> }
                                .create()
                                .show()
                        }
                        return@launch
                    }
                }
                launch(Dispatchers.Main) {
                    waitingDialog.dismiss()
                }
            } catch (e: IOException) {
                Log.e("ECert", "readTagIfAvailable: ", e)
                launch(Dispatchers.Main) {
                    waitingDialog.dismiss()
                    MaterialAlertDialogBuilder(this@DisplayActivity)
                        .setTitle(R.string.text_nfc_failure)
                        .setMessage(R.string.text_io_error)
                        .setPositiveButton(R.string.text_continue) { _, _ -> }
                        .create()
                        .show()
                }
            } catch (e: TagLostException) {
                Log.e("ECert", "readTagIfAvailable: ", e)
                launch(Dispatchers.Main) {
                    waitingDialog.dismiss()
                    MaterialAlertDialogBuilder(this@DisplayActivity)
                        .setTitle(R.string.text_nfc_failure)
                        .setMessage(R.string.text_tag_lost)
                        .setPositiveButton(R.string.text_continue) { _, _ -> }
                        .create()
                        .show()
                }
            }
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.action == NfcAdapter.ACTION_TECH_DISCOVERED) {
            val tag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(NfcAdapter.EXTRA_TAG, Tag::class.java)
            } else {
                intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
            } ?: return
            try {
                nfcTag = NfcTag(tag)
                readTagIfAvailable()
            } catch (e: NfcException) {
                Toast.makeText(
                    this,
                    formatResString(
                        R.string.text_tag_not_supported,
                        this,
                        tag.id.toHexString(HexFormat.UpperCase)
                    ),
                    Toast.LENGTH_SHORT
                ).show()
            }
            Toast.makeText(
                this,
                formatResString(
                    R.string.text_new_tag,
                    this,
                    tag.id.toHexString(HexFormat.UpperCase)
                ),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun setupNfc() {
        try {
            Nfc.setUpNfc(this)
        } catch (e: NfcException) {
            e.printStackTrace()
            MaterialAlertDialogBuilder(this)
                .setTitle(R.string.text_nfc_failure)
                .setMessage(e.message)
                .setNegativeButton(R.string.text_exit_app) { _, _ ->
                    finish()
                }
                .setCancelable(false)
                .create()
                .show()
            return
        }
        if (!Nfc.available) {
            MaterialAlertDialogBuilder(this)
                .setTitle(R.string.text_nfc_failure)
                .setMessage(
                    formatResString(
                        R.string.text_nfc_state_error,
                        this,
                        Nfc.available.toString()
                    )
                )
                .setNegativeButton(R.string.text_exit_app) { _, _ ->
                    finish()
                }
                .setCancelable(false)
                .create()
                .show()
        }
    }

    private val Int.dp
        get() = (resources.displayMetrics.density * this + 0.5f).toInt()

}