package com.ulisesdiaz.loginfacebook

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.facebook.*
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.gson.Gson
import com.squareup.picasso.Picasso


class MainActivity : AppCompatActivity() {

    private var callbackManager: CallbackManager? = null
    private var profileTracker: ProfileTracker? = null
    private var accessToken: AccessToken? = null

    private var imgFoto: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val txtNombre =  findViewById<TextView>(R.id.txtNombre)
        imgFoto = findViewById(R.id.imgFoto)

        callbackManager = CallbackManager.Factory.create()

        var loginButton = findViewById<LoginButton>(R.id.login_button)
        loginButton.setPermissions("email") // Se especifica los permisos que se van a usar en la aplicacion

        if (AccessToken.getCurrentAccessToken() != null && Profile.getCurrentProfile() != null){
            accessToken = AccessToken.getCurrentAccessToken()
            txtNombre.text = String.format("%s %s", Profile.getCurrentProfile().firstName, Profile.getCurrentProfile().lastName)
            cargarFoto()
        }

        // Configuracion de callback
        loginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult?> {
            override fun onSuccess(loginResult: LoginResult?) {
                // obtener el token de la Api de facebook
                val accessToken = AccessToken.getCurrentAccessToken()
                // Saber si esta logueado
                val isLoggedIn = accessToken != null && !accessToken.isExpired
                Log.d("ACCESS_TOKEN", accessToken.token)
                if (Profile.getCurrentProfile() == null){
                    profileTracker = object : ProfileTracker(){
                        override fun onCurrentProfileChanged(oldProfile: Profile?, currentProfile: Profile?) {
                            Log.d("NOMBRE", currentProfile?.firstName!!)
                            profileTracker?.startTracking()
                            txtNombre.text = String.format("%s %s", currentProfile.firstName, currentProfile.lastName)
                        }
                    }
                }else{
                    val profile = Profile.getCurrentProfile()
                    Log.d("NOMBRE", profile?.firstName!!)
                    txtNombre.text = String.format("%s %s", profile.firstName, profile.lastName)

                }
            }

            override fun onCancel() {
                // App code
            }

            override fun onError(exception: FacebookException) {
                // App code
                Log.d("ERROR Login FAACEBOOK", exception.message.toString())
            }
        })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager?.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun cargarFoto(){
        val request = GraphRequest.newMeRequest(accessToken){ objeto, response ->
            // Manera nativa
            val url = response.jsonObject.getJSONObject("picture").getJSONObject("data").getString("url")

            // Utilizando Gson
            var json = response.jsonObject.toString()
            val gson = Gson()
            val dataResponse = gson.fromJson(json, PictureResponse::class.java)
            Log.d("GRAPH: ", dataResponse.picture?.data?.url!!)
            Picasso.get().load(dataResponse.picture?.data?.url!!).into(imgFoto)

        }
        val parameters = Bundle()
        parameters.putString("fields", "picture.height(300)")
        request.parameters = parameters
        request.executeAsync()
    }


}