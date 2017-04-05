package ru.binaryblitz.SportUp.models

class User(var id: Int, var firstName: String?, var lastName: String?, var phone: String?, var city: String?, var avatarUrl: String,
           var votesCount: Int, var eventsCount: Int, var violationsCount: Int) {

    fun asString(): String {
        return Integer.toString(id) + "entity" + firstName + "entity" + lastName + "entity" + phone +
                "entity" + city + "entity" + avatarUrl + "entity" + votesCount + "entity" + eventsCount + "entity" + violationsCount
    }

    companion object {
        fun fromString(string: String): User {
            val strings = string.split("entity".toRegex()).dropLastWhile(String::isEmpty).toTypedArray()
            return User(Integer.parseInt(strings[0]), strings[1], strings[2],
                    strings[3], strings[4], strings[5], Integer.parseInt(strings[6]), Integer.parseInt(strings[7]), Integer.parseInt(strings[8]))
        }

        fun createDefault(): User {
            return User(1, "Evgeny", "Efanov", "null", "null", "null", 0, 0, 0)
        }
    }
}
