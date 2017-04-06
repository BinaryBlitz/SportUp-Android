package ru.binaryblitz.SportUp.models

import java.util.*

data class MyEvent(var id: Int, var eventId: Int, var name: String?, var startsAt: Date,
                   var endsAt: Date, var icon: String, var color: Int, var sportTypeId: Int)
