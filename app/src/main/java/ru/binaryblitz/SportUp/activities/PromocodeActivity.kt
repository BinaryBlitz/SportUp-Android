package ru.binaryblitz.SportUp.activities

import android.os.Bundle
import kotlinx.android.synthetic.main.dialog_promocode.*
import ru.binaryblitz.SportUp.R
import ru.binaryblitz.SportUp.base.BaseActivity

class PromocodeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_promocode)

        backBtn.setOnClickListener {
            finish()
        }

        // TODO
    }

}

