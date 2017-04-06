package ru.binaryblitz.SportUp.push

import com.google.firebase.iid.FirebaseInstanceIdService

class MyInstanceIDListenerService : FirebaseInstanceIdService() {
    override fun onTokenRefresh() {}
}