package ru.binaryblitz.SportUp.models

data class Player(val id: Int, val name: String, val role: String, var violationsCount: Int, var memberShipId: Int)