package ru.binaryblitz.sportup.models

import java.util.*

data class Game(var id: Int, var name: String?, var startsAt: Date, var endsAt: Date, var address: String?, var userLimit: Int,
                val teamLimit: Int, val isPublic: Boolean, val price: Int)