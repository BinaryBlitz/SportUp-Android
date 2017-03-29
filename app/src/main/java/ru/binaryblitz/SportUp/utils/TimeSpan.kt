package ru.binaryblitz.SportUp.utils

import android.os.Parcel
import android.text.ParcelableSpan
import android.text.TextPaint
import android.text.style.MetricAffectingSpan

class TimeSpan : MetricAffectingSpan, ParcelableSpan {

    constructor() {}

    constructor(src: Parcel) {}

    override fun getSpanTypeId(): Int {
        return spanTypeIdInternal
    }

    val spanTypeIdInternal: Int
        get() = 0

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        writeToParcelInternal(dest, flags)
    }

    fun writeToParcelInternal(dest: Parcel, flags: Int) {}

    override fun updateDrawState(tp: TextPaint) {
        tp.baselineShift += (tp.ascent() / 2.5).toInt()
    }

    override fun updateMeasureState(tp: TextPaint) {
        tp.baselineShift += (tp.ascent() / 2.5).toInt()
    }
}
