package com.sedi.learnall.ui.dialogs

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.util.Xml
import android.view.LayoutInflater
import android.view.View
import com.sedi.learnall.R
import com.sedi.learnall.Color
import com.sedi.learnall.convertColorIntToColor
import com.sedi.learnall.custom.CustomColorPickerItem
import com.sedi.learnall.ui.listeners.ChangeColorListener
import kotlinx.android.synthetic.main.color_picker_dialog.view.*


class DialogColorChooser :
    AlertDialog.Builder, DialogInterface {


    private var changeColorListener: ChangeColorListener
    private val customColorPickerItems = ArrayList<CustomColorPickerItem>()

    companion object {
        var colors = listOf(
            Color.BLUE,
            Color.RED,
            Color.DARK_GREEN,
            Color.BLACK,
            Color.LIGHT_GREEN,
            Color.ORANGE,
            Color.YELLOW,
            Color.VIOLET,
            Color.PINK,
            Color.PEACE_COLOR,
            Color.COLOR1,
            Color.COLOR2,
            Color.COLOR3,
            Color.COLOR3,
            Color.COLOR4,
            Color.COLOR5,
            Color.COLOR6,
            Color.COLOR7,
            Color.COLOR8,
            Color.COLOR9
        )
    }

    constructor(
        context: Context?,
        changeColorListener: ChangeColorListener
    ) : super(context) {

        this.changeColorListener = changeColorListener

        setView(getView(context, colors))

    }

    private lateinit var clickItemCallback: onClickItemCallback

    private fun getView(context: Context?, colors: List<Color>): View {

        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.color_picker_dialog, null)


        initListeners()


        val parser = view.resources.getXml(R.xml.item_selected_color)

        try {
            parser.next()
            parser.nextTag()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val attr = Xml.asAttributeSet(parser)
        val count = attr.attributeCount


        if (count < 0) return view


        for (i in colors.indices) {

            val customColorPickerItem = CustomColorPickerItem(context!!, attr, colors[i].color)

            // Вешаем слушателей
            customColorPickerItem.onClickListener(clickItemCallback, i)
            customColorPickerItems.add(customColorPickerItem)

            view.parent_root.addView(customColorPickerItem)
        }


        return view

    }

    private fun initListeners() {
        clickItemCallback = object : onClickItemCallback {
            override fun onClicked(visibility: Int, color: Int, position: Int) {
                if (visibility == View.VISIBLE) {

                    changeColorListener.onColorChanged(convertColorIntToColor(color) ?: Color.GRAY)

                    for (i in 0 until customColorPickerItems.size) {
                        customColorPickerItems[i].setChecked(false)
                    }

                    customColorPickerItems[position].setChecked(true)

                }
            }

        }
    }

    override fun dismiss() {

    }

    override fun cancel() {

    }


    interface onClickItemCallback {
        fun onClicked(visibility: Int, color: Int, position: Int)
    }

}