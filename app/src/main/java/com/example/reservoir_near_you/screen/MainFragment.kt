package com.example.reservoir_near_you.screen

import android.app.Activity
import android.app.UiModeManager.MODE_NIGHT_AUTO
import android.app.UiModeManager.MODE_NIGHT_YES
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.preference.PreferenceManager
import com.example.reservoir_near_you.FirebaseUserLiveData
import com.example.reservoir_near_you.R
import com.example.reservoir_near_you.databinding.FragmentMainBinding
import com.example.reservoir_near_you.viewModels.LoginViewModel
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment() {

    companion object {
        const val TAG = "MainFragment"
        const val SIGN_IN_RESULT_CODE = 1001
    }

    private val viewModel by viewModels<LoginViewModel>()
    private lateinit var binding: FragmentMainBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_main,
            container,
            false
        )
        setHasOptionsMenu(true)
        observeAuthenticationState()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadSettings()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SIGN_IN_RESULT_CODE) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                Log.i(
                    TAG,
                    "Successfully signed in user ${FirebaseAuth.getInstance().currentUser?.displayName}!"
                )
                var action = MainFragmentDirections.actionMainFragmentToMapsFragment()
                view?.findNavController()?.navigate(action)
            } else {
                Log.i(TAG, "Sign in unsuccessful ${response?.error?.errorCode}")
            }
        }
    }

    private fun observeAuthenticationState() {
        viewModel.authenticationState.observe(viewLifecycleOwner, Observer { authenticationState ->
            when (authenticationState) {
                LoginViewModel.AuthenticationState.AUTHENTICATED -> {
                    Log.d(TAG, "AUTHENTICATED")
                    binding.welcomeText.text="Velkommen, ${FirebaseAuth.getInstance().currentUser?.displayName}. I denne appen kan du se alle vannmagasinene i nærheten av deg og se hvert magasin sitt fyllingsnivå."
                    activity?.invalidateOptionsMenu()
                }
                else -> {
                    Log.d(TAG, "UNAUTHENTICATED")
                    activity?.invalidateOptionsMenu()
                    binding.welcomeText.text="Logg inn for å få tilgang til kartet"
                }
            }
        })
    }

    private fun launchSignInFlow() {
        val providers = arrayListOf(AuthUI.IdpConfig.EmailBuilder().build())

        startActivityForResult(
            AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(
                providers
            ).build(), SIGN_IN_RESULT_CODE
        )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        if (viewModel.authenticationState.value != LoginViewModel.AuthenticationState.AUTHENTICATED) {
            menu.findItem(R.id.map).isEnabled = false
            menu.findItem(R.id.logout).isVisible = false
        }
        else{
            menu.findItem(R.id.login).isVisible = false
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return (when(item.itemId) {
            R.id.map -> {
                val action = MainFragmentDirections.actionMainFragmentToMapsFragment()
                view?.findNavController()?.navigate(action)
                true
            }
            R.id.logout -> {
                AuthUI.getInstance().signOut(requireContext())
                activity?.invalidateOptionsMenu()
                true
            }
            R.id.login -> {
                launchSignInFlow()
                true
            }
            R.id.settings -> {
                val action = MainFragmentDirections.actionMainFragmentToSettingsFragment()
                view?.findNavController()?.navigate(action)
                true
            }
            else -> super.onOptionsItemSelected(item)
        })
    }

    private fun loadSettings() {
        val sp = context?.let { PreferenceManager.getDefaultSharedPreferences(it) }
        val dark_mode = sp?.getBoolean("dark_mode", false)

        if (dark_mode == true) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            binding.logo.setImageResource(R.drawable.logo_dark)
            binding.welcomeText.setTextColor(Color.WHITE)
        }
        else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            binding.logo.setImageResource(R.drawable.logo_light)
        }
    }
}
