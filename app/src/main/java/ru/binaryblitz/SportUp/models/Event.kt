package ru.binaryblitz.SportUp.models

import java.util.*

data class Event(var id: Int, var name: String?, var startsAt: Date, var endsAt: Date, var address: String?, var userLimit: Int,
                 val teamLimit: Int, val isPublic: Boolean, val price: Int, val latitude: Double, val longitude: Double, 
                 var sportTypeId: Int, val password: String?)
