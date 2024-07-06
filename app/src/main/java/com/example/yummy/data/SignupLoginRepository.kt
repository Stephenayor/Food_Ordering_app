package com.example.yummy.data

import android.content.ContentValues
import android.content.Context
import android.util.Log
import com.example.yummy.utils.Resource
import com.example.yummy.utils.Tools
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
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


    suspend fun executeOnboardNewUsers(
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
                            Tools.showToast(context, "ACCOUNT CREATED SUCCESSFULLY")
                        } else {
                            errorMessage = task.exception
                            Log.d(ContentValues.TAG, "createUserWithEmail:failure", task.exception)
                        }
                    }

                if (result != null) {
                    emit(Resource.Success(result))
                } else {
                    emit(Resource.Error("Unable to Create Account"))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.localizedMessage ?: "An error occurred"))
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