package com.sedi.learnall.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.fragment.findNavController
import com.sedi.learnall.Color
import com.sedi.learnall.R
import com.sedi.learnall.data.DatabaseHelper
import com.sedi.learnall.data.WordItemDatabase
import com.sedi.learnall.data.interfaces.IActionCard
import com.sedi.learnall.data.interfaces.TranslateResponseCallbackImpl
import com.sedi.learnall.data.models.WordItem
import com.sedi.learnall.data.models.WordItemRoomModel
import com.sedi.learnall.data.remote.YandexTranslater
import com.sedi.learnall.databinding.EditLayoutBinding
import com.sedi.learnall.ui.dialogs.DialogColorChooser
import com.sedi.learnall.ui.dialogs.MessageBox
import com.sedi.learnall.ui.listeners.ChangeColorListener
import kotlinx.android.synthetic.main.edit_layout.*
import org.json.JSONException
import org.json.JSONObject

class EditFragment : BaseFragment(R.layout.edit_layout), LifecycleOwner,
    TranslateResponseCallbackImpl, ChangeColorListener {

    //Data
    private var binding: EditLayoutBinding? = null


    companion object {
        private const val RC_HANDLE_RECORD_AUDIO_PERMISSION = 4
        private const val REQ_CODE_SPEECH_INPUT = 5
        private var wordItem = WordItem(otherName = "Girl", nativeName = "Девушка")
        private lateinit var nativeTextChangeListener: TextWatcher
        private lateinit var otherTextChangeListener: TextWatcher
    }

    private var modifyingCard = ModifyingCard.NONE
    private var modifyingItem = ModifyingItem.NONE
    private var direction = Direction.DEFAULT
    private var db: WordItemDatabase? = null
    private lateinit var yandexTranslater: YandexTranslater

    override fun onSuccess(response: String) {

        activity?.runOnUiThread {
           // binding?.ivEditNative?.isEnabled = true
          //  binding?.ivEditOther?.isEnabled = true
        }


        val jObject = JSONObject(response)
        val jArray = jObject.getJSONArray("text")

        var result: String = ""
        for (i in 0 until jArray.length()) {
            try {
                result += jArray.getString(i)
            } catch (e: JSONException) {
                e.printStackTrace()
            }

        }

        when (direction) {
            Direction.TRANSLATE_TO_OTHER -> initOthers(result)
            Direction.TRANSLATE_TO_NATIVE -> initNative(result)
            else -> TODO()
        }
    }

    override fun onFaillure(e: Exception) {
        activity?.runOnUiThread {
          //  binding?.ivEditNative?.isEnabled = true
           // binding?.ivEditOther?.isEnabled = true
        }


        if (e.message != null)
            Log.e("LearnAll", e.message!!)
    }

    override fun onColorChanged(color: Color) {
        when (modifyingCard) {
            ModifyingCard.NATIVE_CARD -> changeNativeColor(color)
            ModifyingCard.OTHER_CARD -> changeOtherColor(color)
            else -> TODO()
        }
    }


    private fun changeOtherColor(color: Color?) {

        when (modifyingItem) {
            ModifyingItem.CARD -> {
                wordItem.getCardStateOther().setBackColor(color!!.name)
            }
            ModifyingItem.TEXT -> {
                wordItem.getCardStateOther().setTextColor(color!!.name)
            }
            ModifyingItem.NONE -> TODO()
        }


    }

    private fun showPopupMenu(targetView: View) {
        val popupMenu = PopupMenu(context, targetView)
        popupMenu.inflate(R.menu.popup_edit_card)

        popupMenu.setOnMenuItemClickListener {

            when (it.itemId) {
                R.id.menu_edit_card_color -> showChangeColorDialog(ModifyingItem.CARD)
                R.id.menu_edit_text_color -> showChangeColorDialog(ModifyingItem.TEXT)
                else -> false
            }
        }
        popupMenu.show()

    }

    private fun changeNativeColor(color: Color?) {

        when (modifyingItem) {
            ModifyingItem.CARD -> {
                wordItem.getCardStateOther().setBackColor(color!!.name)
            }
            ModifyingItem.TEXT -> {
                wordItem.getCardStateNative().setTextColor(color!!.name)
            }
            ModifyingItem.NONE -> TODO()
        }
    }


    private fun initNative(response: String) {

        wordItem.setNativeName(response)

        activity?.runOnUiThread {
           // binding?.ivTranslateNative?.isEnabled = true
           // binding?.ivTranslateNative?.isEnabled = true
        }

    }


    private fun initOthers(response: String) {

        sayText(response)

        wordItem.setOtherName(response)

        activity?.runOnUiThread {
           // binding?.ivTranslateOther?.isEnabled = true
          //  binding?.ivTranslateNative?.isEnabled = true
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.edit_layout, container, false)
        binding?.card = wordItem
        binding?.executePendingBindings()


        db = WordItemDatabase.invoke(context!!)

        if (textToSpeech == null)
            initTTS()



        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        initViewListeners()
        setupViews()
        yandexTranslater = YandexTranslater(context!!)

        return binding?.root
    }

    private fun setupViews() {


        binding?.appToolBar?.apply {
            setTitle("Редактор карточки")
            showBackButton()
            onBackClick {
                findNavController().navigate(R.id.show_fragment)
            }
        }

    }

    private fun initViewListeners() {

        binding?.ivWordOtherEdit?.setOnClickListener { OnEditCardOther(it) }
        binding?.ivEditOther?.setOnClickListener { onVoiceInputOther(it) }
        binding?.ivTranslateOther?.setOnClickListener { OnTranslateOther(it) }
        binding?.ivTranslateNative?.setOnClickListener { OnTranslateNative(it) }
        binding?.btnSave?.setOnClickListener { SaveWord(it) }
        binding?.ivWordNative?.setOnClickListener { onVoiceInputNative(it) }
        binding?.ivWordNativeEdit?.setOnClickListener { OnEditCardNative(it) }

        otherTextChangeListener = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

                //    iv_translate_other.isEnabled = !TextUtils.isEmpty(et_word_other.text)


                if (direction == Direction.SPEAK_OTHER && !TextUtils.isEmpty(wordItem.getOtherName())) {
                    translateOther()
                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        }

        nativeTextChangeListener = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

                //    iv_translate_native.isEnabled = !TextUtils.isEmpty(et_word_native.text)

                ///     wordItem.setNativeName(tv_card_native.text.toString())

                if (direction == Direction.SPEAK_NATIVE && !TextUtils.isEmpty(wordItem.getNativeName())) {
                    translateNative()
                }


            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        }


        binding?.etWordOther?.addTextChangedListener(otherTextChangeListener)
        binding?.etWordNative?.addTextChangedListener(nativeTextChangeListener)

    }

    private fun translateOther() {

        activity?.runOnUiThread {
         //  binding?.ivEditNative?.isEnabled = false
           // binding?.ivEditOther?.isEnabled = false
        }

        wordItem.setOtherName(et_word_other.text.toString())
        direction = Direction.TRANSLATE_TO_NATIVE
        yandexTranslater.translate(
            "en",
            "ru",
            wordItem.getOtherName(),
            this,
            this
        )
    }


    private fun translateNative() {

        activity?.runOnUiThread {
           // binding?.ivEditNative?.isEnabled = false
          //  binding?.ivEditOther?.isEnabled = false
        }


        direction = Direction.TRANSLATE_TO_OTHER

        yandexTranslater.translate(
            "ru",
            "en",
            wordItem.getNativeName(),
            this,
            this
        )
    }

    fun OnTranslateNative(view: View) {

        translateNative()
        view.isClickable = false
    }

    fun OnTranslateOther(view: View) {
        translateOther()
        view.isClickable = false
    }


    fun OnEditCardOther(view: View) {

        modifyingCard = ModifyingCard.OTHER_CARD
        showPopupMenu(view)
    }

    fun OnEditCardNative(view: View) {

        modifyingCard = ModifyingCard.NATIVE_CARD
        showPopupMenu(view)

    }

    private fun onDismisedColorChoosedDialog() {
        modifyingCard = ModifyingCard.NONE
        modifyingItem = ModifyingItem.NONE
    }

    public fun SaveWord(view: View) {


        if (TextUtils.isEmpty(wordItem.getNativeName()) || TextUtils.isEmpty(wordItem.getOtherName())) return



        DatabaseHelper.asynkSaveOrUpdateWordItem(db!!, wordItem.copy(), object : IActionCard {
            override fun onComplete(
                data: WordItemRoomModel?,
                collectionData: ArrayList<WordItemRoomModel>?
            ) {
                activity?.runOnUiThread {
                    toast("Успешно сохранено", Toast.LENGTH_LONG)
                    wordItem = WordItem()
                }
            }

            override fun onError(exception: Exception) {
                activity?.runOnUiThread {
                    MessageBox.show(
                        activity as Activity,
                        "Ошибка добавления",
                        exception.message ?: "Обратитесь к разработчику",
                        activity as LifecycleOwner
                    )
                }
            }

        })


    }

    private fun showChangeColorDialog(modifyingItem: ModifyingItem): Boolean {

        this.modifyingItem = modifyingItem

        val dialogColorChooser = DialogColorChooser(
            context,
            this
        )

        dialogColorChooser.setTitle(R.string.select_color)
        dialogColorChooser.setPositiveButton(
            android.R.string.ok
        ) { _, _ ->
            dialogColorChooser.dismiss()
        }


        dialogColorChooser.setOnDismissListener { onDismisedColorChoosedDialog() }

        dialogColorChooser.show()


        return false
    }

    private fun startSpeechInput() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )


        when (direction) {
            Direction.SPEAK_NATIVE -> intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ru")
            Direction.SPEAK_OTHER -> intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en")
            else -> {//TODO
            }
        }

        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Скажи что хотел")
        startActivityForResult(intent, REQ_CODE_SPEECH_INPUT)
    }


    @SuppressWarnings
    fun onVoiceInputNative(view: View) {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            Toast.makeText(
                context,
                R.string.no_recognition_available,
                Toast.LENGTH_LONG
            ).show()
        }
        direction = Direction.SPEAK_NATIVE
        checkVoicePermissions()

    }

    @SuppressWarnings
    fun onVoiceInputOther(view: View) {


        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            Toast.makeText(
                context,
                R.string.no_recognition_available,
                Toast.LENGTH_LONG
            ).show()
        }

        direction = Direction.SPEAK_OTHER
        checkVoicePermissions()

    }

    private fun checkVoicePermissions() {
        val permissions = arrayOf(Manifest.permission.RECORD_AUDIO)

        if (!ActivityCompat.shouldShowRequestPermissionRationale(
                this.requireActivity(),
                permissions[0]
            )
        ) {
            ActivityCompat.requestPermissions(
                this.requireActivity(),
                permissions,
                RC_HANDLE_RECORD_AUDIO_PERMISSION
            )
        }
    }

    fun sayText(response: String) {
        val utteranceId = this.hashCode().toString()
        val speechStatus =
            textToSpeech?.speak(response, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode != RC_HANDLE_RECORD_AUDIO_PERMISSION)
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == RC_HANDLE_RECORD_AUDIO_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Maybe Start Speech recognithion
                startSpeechInput()
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_CODE_SPEECH_INPUT && resultCode == Activity.RESULT_OK) {


            var text = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)!![0]
            when (direction) {
                Direction.SPEAK_NATIVE -> {
                    wordItem.setNativeName(text)
                }
                Direction.SPEAK_OTHER -> {
                    wordItem.setOtherName(text)
                }
                else -> return
            }
        }

    }

    enum class ModifyingCard {
        NATIVE_CARD,
        OTHER_CARD,
        NONE
    }

    enum class Direction {
        SPEAK_OTHER,
        SPEAK_NATIVE,
        TRANSLATE_TO_OTHER,
        TRANSLATE_TO_NATIVE,
        DEFAULT
    }

    enum class ModifyingItem {
        CARD,
        TEXT,
        NONE
    }

    override fun onDestroy() {
        super.onDestroy()
        wordItem = WordItem()
    }


}