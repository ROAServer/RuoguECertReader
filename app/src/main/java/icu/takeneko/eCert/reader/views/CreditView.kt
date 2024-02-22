package icu.takeneko.eCert.reader.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.google.android.material.textview.MaterialTextView
import icu.takeneko.eCert.reader.CreditItem
import icu.takeneko.eCert.reader.R

class CreditView : LinearLayout {

    private val textCreditName: MaterialTextView
    private val textParticipatePart: MaterialTextView
    private val textLink: MaterialTextView

    lateinit var _creditItem: CreditItem
    var creditItem: CreditItem
        set(value) {
            _creditItem = value
            textCreditName.text = value.name
            textParticipatePart.text = value.participatePart
            textLink.text = value.link
        }
        get() = _creditItem

    constructor(context: Context) : super(context) {
        LayoutInflater.from(context).inflate(R.layout.credit_view, this)
        textCreditName = findViewById(R.id.text_credit_name)
        textParticipatePart = findViewById(R.id.text_participate_part)
        textLink = findViewById(R.id.text_link)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        LayoutInflater.from(context).inflate(R.layout.credit_view, this)
        textCreditName = findViewById(R.id.text_credit_name)
        textParticipatePart = findViewById(R.id.text_participate_part)
        textLink = findViewById(R.id.text_link)
    }


}