package com.sedi.learnall.ui

import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import java.util.*

open class BaseFragment(contentLayoutId: Int) : Fragment(contentLayoutId) {


    protected var textToSpeech: TextToSpeech? = null

    fun initTTS() {
        textToSpeech = TextToSpeech(context, TextToSpeech.OnInitListener {
            if (it == TextToSpeech.SUCCESS) {
                val ttsLang = textToSpeech?.setLanguage(Locale("en"))
                if (ttsLang == TextToSpeech.LANG_MISSING_DATA) {
                    toast("TTS Language is unsupported")

                }
            } else toast("TTS initialization failed")
        })
    }

    protected fun toast(text: String, toastDuration: Int = Toast.LENGTH_SHORT) {

        when (toastDuration) {
            Toast.LENGTH_SHORT -> Toast.makeText(this.context, text, Toast.LENGTH_SHORT).show()
            else -> Toast.makeText(this.context, text, Toast.LENGTH_LONG).show()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        textToSpeech?.let {
            it.stop()
            it.shutdown()
        }
    }


}