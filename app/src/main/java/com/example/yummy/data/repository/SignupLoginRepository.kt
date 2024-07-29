package com.example.yummy.data.repository

import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.yummy.R
import com.example.yummy.utils.AppConstants
import com.example.yummy.utils.Resource
import com.example.yummy.utils.Tools
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SignupLoginRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore,
    @ApplicationContext private val context: Context,
    private val encryptedPrefs: SharedPreferences
) {

    @Inject
    lateinit var firebase: Firebase
    private lateinit var firebaseAuthentication: FirebaseAuth
    private lateinit var result: FirebaseUser
    private lateinit var signInRequest: BeginSignInRequest
    private lateinit var oneTapClient: SignInClient


    @ExperimentalCoroutinesApi
    suspend fun executeOnboardNewUsers(userEmail: String?, password: String?) =
        callbackFlow<Result<Resource<FirebaseUser>>> {
            firebaseAuthentication = firebaseAuth

            if (userEmail?.isNotEmpty() == true && password?.isNotEmpty() == true) {
                firebaseAuthentication.createUserWithEmailAndPassword(userEmail, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            result = firebaseAuthentication.currentUser!!
                            Tools.showToast(context, "ACCOUNT CREATED SUCCESSFULLY")
                            val item = Resource.Success(result)
                            this@callbackFlow.trySend(Result.success(item))

                                .onFailure {
                                    val error = Resource.Error(it?.message, null)
                                }
                        } else {
                            Log.d(ContentValues.TAG, "createUserWithEmail:failure", task.exception)
                            val nullData : FirebaseUser? = null
                            val firebaseAuthenticationErrorMessage = Resource.Error(task.exception?.localizedMessage, nullData)
                            this@callbackFlow.trySend(Result.success(firebaseAuthenticationErrorMessage))
                        }
                    }
            } else{
                val nullData : FirebaseUser? = null
                val firebaseAuthenticationErrorMessage = Resource.Error(AppConstants.GENERIC_ERROR_MSG, nullData)
                this@callbackFlow.trySend(Result.success(firebaseAuthenticationErrorMessage))
            }

            awaitClose {

            }
        }

    @ExperimentalCoroutinesApi
    suspend fun login(userEmail: String?, password: String?) =
        callbackFlow<Result<Resource<FirebaseUser>>> {
            firebaseAuthentication = firebaseAuth

            if (userEmail?.isNotEmpty() == true && password?.isNotEmpty() == true) {
                firebaseAuthentication.signInWithEmailAndPassword(userEmail, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = firebaseAuthentication.currentUser!!
                            val item = Resource.Success(user)
                            this@callbackFlow.trySend(Result.success(item))
                        } else {
                            Log.d(ContentValues.TAG, "signInWithEmail:failure", task.exception)
                            val nullData : FirebaseUser? = null
                            val firebaseLoginErrorMessage = Resource.Error(task.exception?.localizedMessage, nullData)
                            this@callbackFlow.trySend(Result.success(firebaseLoginErrorMessage))
                        }
                    }
            } else{
                val nullData : FirebaseUser? = null
                val firebaseLoginErrorMessage = Resource.Error(AppConstants.GENERIC_ERROR_MSG, nullData)
                this@callbackFlow.trySend(Result.success(firebaseLoginErrorMessage))
            }

            awaitClose {

            }
        }



    @ExperimentalCoroutinesApi
    fun firebaseAuthenticationWithGoogleAccount(idToken: String?) =
        callbackFlow<Result<Resource<FirebaseUser>>> {
        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(firebaseCredential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = firebaseAuth.currentUser
                    Tools.showToast(context, user?.email)
                    val item = Resource.Success(user)
                    this@callbackFlow.trySend(Result.success(item))
                } else {
                    Tools.showToast(context, "Login Failed")
                    val nullData : FirebaseUser? = null
                    val firebaseGoogleLoginErrorMessage = Resource.Error(task.exception?.localizedMessage, nullData)
                    this@callbackFlow.trySend(Result.success(firebaseGoogleLoginErrorMessage))
                }
            }

            awaitClose {}
    }

    fun saveLoginUID(key: String, value: String) {
        encryptedPrefs.edit().putString(key, value).apply()
    }

    fun getLoginUID(key: String): String? {
        return encryptedPrefs.getString(key, null)
    }



    fun sampleFlow(userEmail: String, password: String): Flow<String> {
        val stringFlow: Flow<String> = flow {
            emit("Hello")
            emit("World")
            emit("From")
            emit("Flow")
        }

        return stringFlow
    }


//    suspend fun executeOnboard(
//        userEmail: String,
//        password: String
//    ): Flow<Resource<FirebaseUser>> {
//        var result: FirebaseUser? = null
//        var errorMessage: Exception? = null
//        return flow {
//            try {
//                firebaseAuthentication = firebaseAuth
//                firebaseAuthentication.createUserWithEmailAndPassword(
//                    userEmail,
//                    password
//                )
//                    .addOnCompleteListener { task ->
//                        if (task.isSuccessful) {
//                            result = firebaseAuthentication.currentUser!!
//                        }
//                    }
//                emit(Resource.Success(result))
//            } catch (e: Exception) {
//                emit(Resource.Error("Unable to Create Account"))
//            }
//
//    }
//    }

}

