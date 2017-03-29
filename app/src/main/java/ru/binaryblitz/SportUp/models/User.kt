package ru.binaryblitz.SportUp.models

class User(var id: Int, var firstName: String?, var lastName: String?, var phone: String?, var city: String?) {

    fun asString(): String {
        return Integer.toString(id) + "entity" + firstName + "entity" + lastName + "entity" + phone +
                "entity" + city
    }

    companion object {
        fun fromString(str: String): User {
            val strings = str.split("entity".toRegex()).dropLastWhile(String::isEmpty).toTypedArray()
            return User(Integer.parseInt(strings[0]), strings[1], strings[2],
                    strings[3], strings[4])
        }

        fun createDefault(): User {
            return User(1, "Evgeny", "Efanov", "null", "null")
        }
    }
}
