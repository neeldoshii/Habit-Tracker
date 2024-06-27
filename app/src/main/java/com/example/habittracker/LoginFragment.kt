package com.example.habittracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.habittracker.databinding.FragmentLoginBinding
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch
import timber.log.Timber
import java.security.MessageDigest
import java.util.UUID


class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var firebaseAuth: FirebaseAuth


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        // TODO(Dependency INJECT)
        firebaseAuth = FirebaseAuth.getInstance()

        binding.btnGoogle.setOnClickListener {
            userAuthentication(googleAuthentication = true)
        }

        binding.guestButton.setOnClickListener {
            userAuthentication(anonymousLogin = true)
        }
        return binding.root
    }

    private fun userAuthentication(
        googleAuthentication: Boolean = false,
        anonymousLogin: Boolean = false,
        emailLogin: Boolean = false
    ) {
        when {
            // Ref : for Firebase Google Auth Integration
            // https://firebase.google.com/docs/auth/android/google-signin
            // TODO Extract all it into VIEWMODEL MVVM Logic
            googleAuthentication -> {
                val rawNonce = UUID.randomUUID().toString()
                val bytes = rawNonce.toByteArray()
                val md = MessageDigest.getInstance("SHA-256")
                val digest = md.digest(bytes)
                val hashedNonce = digest.fold("") { str, it -> str + "%02x".format(it) }
                val credentialManager = CredentialManager.create(requireActivity())
                // Docs Ref : https://developer.android.com/identity/sign-in/credential-manager-siwg
                val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(BuildConfig.WEB_CLIENT_ID)
                    .setNonce(hashedNonce)
                    .build()

                try {
                    val request : GetCredentialRequest = GetCredentialRequest.Builder()
                        .addCredentialOption(googleIdOption)
                        .build()

                    lifecycleScope.launch {
                        val result = credentialManager.getCredential(
                            request = request,
                            context = requireActivity()
                        )
                        val credentials = result.credential

                        val googleIdToken = GoogleIdTokenCredential
                            .createFrom(credentials.data)

                        val firebaseCredential = GoogleAuthProvider.getCredential(googleIdToken.idToken, null)
                        firebaseAuth.signInWithCredential(firebaseCredential)
                            .addOnCompleteListener(requireActivity()){ task->
                                if (task.isSuccessful){
                                    val user = firebaseAuth.currentUser
                                    //TODO update UI
                                }
                                else{
                                    Timber.w(task.exception, "signInWithCredential:failure")
                                    //TODO update UI
                                }
                            }
                    }

                }catch (e : Exception){
                    //TODO
                    Timber.tag("Exception").e(e.toString())
                }
            }

            anonymousLogin -> {
                // Ref : https://firebase.google.com/docs/auth/android/anonymous-auth
                lifecycleScope.launch {
                    firebaseAuth.signInAnonymously()
                        .addOnCompleteListener(requireActivity()) { task ->
                            if (task.isSuccessful) {
                                Timber.d("signInAnonymously:success")
                                //val user = firebaseAuth.currentUser
                                //updateUI(user)
                            } else {
                                // If sign in fails, display a message to the user.
                                Timber.w(task.exception, "signInAnonymously:failure")
                                //updateUI(null)
                            }
                        }
                }
            }
            emailLogin -> {
                // TODO Not Yet Implemented
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}