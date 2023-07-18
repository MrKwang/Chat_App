package com.example.myapplication.utilities

class Constants {
    companion object{
        const val KEY_COLLECTION_USERS = "users"
        const val KEY_USER = "User"
        const val KEY_NAME = "name"
        const val KEY_EMAIL = "email"
        const val KEY_PASSWORD = "password"
        const val KEY_PREFERENCE_NAME = "chatAppPreference"
        const val KEY_IS_SIGNED_IN = "isSignedIn"
        const val KEY_USER_ID = "userID"
        const val KEY_IMAGE = "image"
        const val KEY_FCM_TOKEN = "fcmToken"
        const val KEY_COLLECTION_CHAT = "chat"
        const val KEY_SEND_ID = "sendId"
        const val KEY_RECEIVE_ID = "receiveId"
        const val KEY_MESSAGE = "message"
        const val KEY_TIME = "time"
        const val KEY_LAST_MESSAGE = "last_message"
        const val KEY_COLLECTION_CONVERSATION = "conversation"
        const val KEY_USER_1_NAME = "user1_Name"
        const val KEY_USER_2_NAME = "user2_Name"
        const val KEY_USER_1_ID = "user1_Id"
        const val KEY_USER_2_ID = "user2_Id"
        const val KEY_USER_1_IMAGE = "user1_Image"
        const val KEY_USER_2_IMAGE = "user2_Image"
        const val KEY_USER_1_TOKEN = "user1_Token"
        const val KEY_USER_2_TOKEN = "user2_Token"
        const val KEY_AVAILABILITY = "availability"
        const val REMOTE_MSG_AUTHORIZATION = "Authorization"
        const val REMOTE_MSG_CONTENT_TYPE = "Content-Type"
        const val REMOTE_MSG_DATA = "data"
        const val REMOTE_MSG_REGISTRATION_IDS = "registration_ids"

        var remoteMsgHeaders: HashMap<String, String>? = HashMap()
        fun getRemoteHeaders(): HashMap<String, String> {
            remoteMsgHeaders?.set(REMOTE_MSG_AUTHORIZATION, "key=AAAAZV2z7WY:APA91bEsdIMio5Lcqg3anTA8aZtu8IiH5lRKehEwKDlvHK8Ez-W3LBOvskJ8Gl-m1VWX3t9UHvK1-8MmZYLrM11FnQAFK5Qk7Pg0oYKlSL9il_CfJZ6SlO7vl9ZXv7Yv8FYKayPnoKc_")
            remoteMsgHeaders?.set(REMOTE_MSG_CONTENT_TYPE, "application/json")
            return remoteMsgHeaders!!
        }
    }
    /*@Throws(IOException::class)
    private fun getAccessToken(): String? {
        val scope = Scope("https://www.googleapis.com/auth/firebase.messaging")
        val googleCredentials: GoogleCredentials = GoogleCredentials
            .fromStream(FileInputStream("service-account.json"))
            .createScoped(listOf(scope))
        googleCredentials. refreshAccessToken()
        return googleCredentials.accessToken.tokenValue
    }*/


}