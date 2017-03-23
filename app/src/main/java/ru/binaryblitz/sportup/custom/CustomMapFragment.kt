package ru.binaryblitz.sportup.custom

import android.content.Context
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout

import com.google.android.gms.maps.SupportMapFragment

import ru.binaryblitz.sportup.activities.EventsMapActivity

class CustomMapFragment {
    class TouchableWrapper(context: Context) : FrameLayout(context) {

        private var timeLastTouched: Long = 0
        private var updateMapAfterUserInteraction: UpdateMapAfterUserInteraction? = null

        init {
            try {
                updateMapAfterUserInteraction = context as EventsMapActivity
            } catch (e: ClassCastException) {
                throw ClassCastException()
            }

        }

        override fun dispatchTouchEvent(motionEvent: MotionEvent): Boolean {
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> timeLastTouched = SystemClock.uptimeMillis()
                MotionEvent.ACTION_UP -> {
                    val now = SystemClock.uptimeMillis()
                    if (now - timeLastTouched > SCROLL_TIME) {
                        updateMapAfterUserInteraction!!.onUpdateMapAfterUserInteraction()
                    }
                }
            }
            return super.dispatchTouchEvent(motionEvent)
        }

        interface UpdateMapAfterUserInteraction {
            fun onUpdateMapAfterUserInteraction()
        }

        companion object {
            private val SCROLL_TIME = 200L
        }
    }

    class MySupportMapFragment : SupportMapFragment() {
        lateinit var originalContentView: View
        lateinit var touchView: TouchableWrapper

        override fun onCreateView(inflater: LayoutInflater?, parent: ViewGroup?, savedInstanceState: Bundle?): View? {
            originalContentView = super.onCreateView(inflater, parent, savedInstanceState)!!
            touchView = TouchableWrapper(activity)
            touchView.addView(originalContentView)
            return touchView
        }

        override fun getView(): View? {
            return originalContentView
        }
    }
}
