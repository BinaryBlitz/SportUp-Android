package ru.binaryblitz.SportUp.models

import ru.binaryblitz.SportUp.utils.LogUtil

class User(var id: Int, var firstName: String?, var lastName: String?, var phone: String?, var city: String?, var avatarUrl: String) {

    fun asString(): String {
        return Integer.toString(id) + "entity" + firstName + "entity" + lastName + "entity" + phone +
                "entity" + city + "entity" + avatarUrl
    }

    companion object {
        fun fromString(string: String): User {
            val strings = string.split("entity".toRegex()).dropLastWhile(String::isEmpty).toTypedArray()
            return User(Integer.parseInt(strings[0]), strings[1], strings[2],
                    strings[3], strings[4], strings[5])
        }

        fun createDefault(): User {
            return User(1, "Evgeny", "Efanov", "null", "null", "null")
        }
    }
}
