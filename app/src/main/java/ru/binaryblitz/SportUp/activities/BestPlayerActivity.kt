package ru.binaryblitz.SportUp.activities

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_best_player.*
import ru.binaryblitz.SportUp.R
import ru.binaryblitz.SportUp.base.BaseActivity

class BestPlayerActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_best_player)

        left_btn.setOnClickListener {
            finish()
        }
    }

}

