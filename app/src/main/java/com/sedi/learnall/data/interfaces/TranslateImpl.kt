package com.sedi.learnall.data.interfaces

import androidx.lifecycle.LifecycleOwner
import com.sedi.learnall.data.interfaces.TranslateResponseCallbackImpl

interface TranslateImpl {
    fun translate(
        nativeLanguage: String,
        otherLanguage: String,
        text: String,
        tranlateResponceCallback: TranslateResponseCallbackImpl,
        lifecycleOwner: LifecycleOwner
    )

}