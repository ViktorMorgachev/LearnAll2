package com.sedi.learnall.data.models

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import com.sedi.learnall.Color


/**
 * Состояние каждой карточки, фон, цвет текста
 * */
class CardState(
    private var backColor: String = Color.GRAY.name,
    private var textColor: String = Color.BLACK.name
) : BaseObservable() {
    fun copy(): CardState {
        return CardState(this.backColor, this.textColor)
    }


    @Bindable
    fun setBackColor(color: String) {
        backColor = color
        notifyPropertyChanged(BR.backColor)
    }

    fun getBackColor(): String = backColor


    @Bindable
    fun setTextColor(color: String) {
        textColor = color
        notifyPropertyChanged(BR.textColor)
    }

    fun getTextColor(): String = textColor

}