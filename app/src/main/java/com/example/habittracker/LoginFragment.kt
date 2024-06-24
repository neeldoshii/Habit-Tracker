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

        val rawNonce = UUID.randomUUID().toString()
        val bytes = rawNonce.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        val hashedNonce = digest.fold("") { str, it -> str + "%02x".format(it) }

        // TODO(Dependency INJECT)
        firebaseAuth = FirebaseAuth.getInstance()

        val credentialManager = CredentialManager.create(requireActivity())

        binding.btnGoogle.setOnClickListener {
            // Docs Ref : https://developer.android.com/identity/sign-in/credential-manager-siwg
            val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(BuildConfig.WEB_CLIENT_ID)
                .setNonce(hashedNonce)
                .build()

            // Ref : for Firebase Google Auth Integration
            // https://firebase.google.com/docs/auth/android/google-signin
            // TODO Call this in a switch case func to handle (email, anonymous login, google auth)
            // TODO Extract it into VIEWMODEL MVVM Logic
            try {
                val request : GetCredentialRequest = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                viewLifecycleOwner.lifecycleScope.launch {
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

        return binding.root
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