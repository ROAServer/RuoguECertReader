package icu.takeneko.eCert.reader.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import icu.takeneko.eCert.reader.CreditItem
import icu.takeneko.eCert.reader.R
import icu.takeneko.eCert.reader.databinding.ActivityCreditsBinding
import icu.takeneko.eCert.reader.views.CreditView

class CreditsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreditsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreditsBinding.inflate(layoutInflater)
        setContentView(binding.main)
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener {
            this.finish()
        }
        val credits = CreditItem.readCredits(this)
        credits.forEach {
            val view = CreditView(this).apply { creditItem = it }
            binding.listCreditItems.addView(view)
        }

    }
}