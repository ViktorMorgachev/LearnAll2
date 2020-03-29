package com.sedi.learnall.data.interfaces

interface TranslateResponseCallbackImpl {

    fun onSuccess(response: String)
    fun onFaillure(e : Exception)

}