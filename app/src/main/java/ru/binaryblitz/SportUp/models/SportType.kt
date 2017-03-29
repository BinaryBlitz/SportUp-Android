package ru.binaryblitz.SportUp.models

data class SportType(var id: Int, var name: String?, var gamesQuantity: Int, var placesQuantity: Int, var iconUrl: String?, var color: Int) {
    fun asString(): String {
        return Integer.toString(id) + "entity" + name + "entity" + color
    }

    companion object {
        fun fromString(str: String): SportType {
            val strings = str.split("entity".toRegex()).dropLastWhile(String::isEmpty).toTypedArray()
            return SportType(Integer.parseInt(strings[0]), strings[1], 0, 0, "", strings[2].toInt())
        }
    }
}
