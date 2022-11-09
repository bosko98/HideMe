package com.example.hideme

import android.app.KeyguardManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CancellationSignal
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.example.hideme.R

class MainActivity : AppCompatActivity() {

    // create a CancellationSignal variable and assign a value null to it
    private var cancellationSignal: CancellationSignal? = null

    // create an authenticationCallback
    private val authenticationCallback: BiometricPrompt.AuthenticationCallback
        get() = @RequiresApi(Build.VERSION_CODES.P)
        object : BiometricPrompt.AuthenticationCallback() {

            // onAuthenticationError and onAuthenticationSucceeded
            // onAuthenticationError and show a toast
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
                super.onAuthenticationError(errorCode, errString)
                notifyUser("Authentication Error : $errString")
            }

            // onAuthenticationSucceeded and show a toast that Authentication has Succeed
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
                super.onAuthenticationSucceeded(result)
                notifyUser("Authentication Succeeded")

                // Starting next activity upon successful authentication
                startNotesActivity()

            }
        }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkBiometricSupport()
        // create a biometric dialog on Click of button
        findViewById<Button>(R.id.btn_login).setOnClickListener {
            // This creates a dialog of biometric auth and
            // it requires title , subtitle ,
            // and description

            val biometricPrompt = BiometricPrompt.Builder(this)
                .setTitle("Put a finger on sensor")
                .setSubtitle("Authentication")
                .setDescription("Uses FP")
                .setNegativeButton("Cancel", this.mainExecutor, DialogInterface.OnClickListener { dialog, which ->
                    notifyUser("Authentication Cancelled")
                }).build()

            // start the authenticationCallback in mainExecutor
            biometricPrompt.authenticate(getCancellationSignal(), mainExecutor, authenticationCallback)
        }
    }

    // function that starts notes activity
    private fun startNotesActivity(){
        var intent: Intent = Intent(this, NotesActivity::class.java)
        startActivity(intent)
    }

    // it will be called when authentication is cancelled by the user
    private fun getCancellationSignal(): CancellationSignal {
        cancellationSignal = CancellationSignal()
        cancellationSignal?.setOnCancelListener {
            notifyUser("Authentication was Cancelled by the user")
        }
        return cancellationSignal as CancellationSignal
    }

    // does the app have a fingerprint permission?
    @RequiresApi(Build.VERSION_CODES.M)
    private fun checkBiometricSupport(): Boolean {
        val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        if (!keyguardManager.isDeviceSecure) {
            notifyUser("Fingerprint authentication has not been enabled in settings")
            return false
        }
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.USE_BIOMETRIC) != PackageManager.PERMISSION_GRANTED) {
            notifyUser("Fingerprint Authentication Permission is not enabled")
            return false
        }
        return if (packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)) {
            true
        } else true
    }

    // this is a toast method which is responsible for showing messages
    // string as parameter
    private fun notifyUser(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
