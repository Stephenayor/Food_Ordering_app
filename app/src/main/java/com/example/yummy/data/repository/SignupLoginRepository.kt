package com.example.yummy.data.repository

import android.content.ContentValues
import android.content.Context
import android.util.Log
import com.example.yummy.utils.AppConstants
import com.example.yummy.utils.Resource
import com.example.yummy.utils.Tools
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import java.lang.Exception
import javax.inject.Inject

class SignupLoginRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore,
    @ApplicationContext private val context: Context
) {

    @Inject
    lateinit var firebase: Firebase
    private lateinit var firebaseAuthentication: FirebaseAuth
    private lateinit var result: FirebaseUser


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


        suspend fun executeOnboard(
        userEmail: String,
        password: String
    ): Flow<Resource<FirebaseUser>> {
        var result: FirebaseUser? = null
        var errorMessage: Exception? = null
        return flow {
            try {
                firebaseAuthentication = firebaseAuth
                firebaseAuthentication.createUserWithEmailAndPassword(
                    userEmail,
                    password
                )
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            result = firebaseAuthentication.currentUser!!
                        }
                    }
                emit(Resource.Success(result))
            } catch (e: Exception) {
                emit(Resource.Error("Unable to Create Account"))
            }
        }
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


}