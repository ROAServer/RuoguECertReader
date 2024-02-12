package icu.takeneko.eCert.reader.activity

import android.os.Bundle
import icu.takeneko.eCert.reader.DisplayActivity
import icu.takeneko.eCert.reader.data.CertificateIssuer
import icu.takeneko.eCert.reader.data.cert.ECertV1
import icu.takeneko.eCert.reader.databinding.ActivityEcertV1DisplayBinding
import icu.takeneko.eCert.reader.logI
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import icu.takeneko.eCert.reader.R
import icu.takeneko.eCert.reader.formatResString
import icu.takeneko.eCert.reader.signature.SignatureChecker

class ECertV1DisplayActivity : DisplayActivity<ECertV1>(ECertV1::class.java) {

    private lateinit var binding: ActivityEcertV1DisplayBinding
    private lateinit var signatureChecker: SignatureChecker
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEcertV1DisplayBinding.inflate(layoutInflater)
        setContentView(binding.main)
        logI(data.toString())
        signatureChecker = SignatureChecker(this, "ecert_v1")
        displayCert()
    }

    private fun displayCert(){
        var validCertificate = true
        val cause = mutableListOf<String>()
        binding.textSerialNumber.text = data.serialNumber.toString()
        binding.textIssuanceTime.text = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(data.timeStamp * 1000))
        val matchResult = CertificateIssuer.match(data.issuer)
        if (matchResult == R.string.text_unknown){
            validCertificate = false
            cause += getString(R.string.text_unknown_issuer)
        }
        println(R.string.text_unknown)
        println(R.string.text_roa_server)
        println(R.string.text_ruogu_studio)
        println(matchResult)
        binding.textIssuer.text = getString(matchResult)
        binding.textRecipient.text = data.recipient
        binding.textGiftReason.text = data.giftReason
        binding.textDescription.text = data.description
        binding.textPostscript.text = data.postscript
        if (!signatureChecker.checkSignature(dataInBytesWithoutSignature, data.signature)){
            validCertificate = false
            cause += getString(R.string.text_invalid_signature)
        }
        if (validCertificate){
            binding.backgroundCard.setCardBackgroundColor(getColor(R.color.certValid))
            binding.statusText.setText(R.string.label_certificate_valid)
            binding.statusIcon.setImageResource(R.drawable.ic_check_circle)
        }else{
            binding.backgroundCard.setCardBackgroundColor(getColor(R.color.certInvalid))
            binding.statusText.text =
                formatResString(R.string.label_certificate_invalid, this, cause.joinToString("\n"))
            binding.statusIcon.setImageResource(R.drawable.ic_error_24px)
        }
    }
}