package com.tomer.anibadi

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.gson.Gson
import com.tomer.anibadi.adap.WidService
import com.tomer.anibadi.databinding.ActivityAddUserBinding
import com.tomer.anibadi.databinding.CalDiaBinding
import com.tomer.anibadi.modal.Chilren
import com.tomer.anibadi.modal.Mother
import com.tomer.anibadi.modal.WidgetMod
import com.tomer.anibadi.util.ChildManager
import com.tomer.anibadi.util.CipherUtils
import com.tomer.anibadi.util.Repo
import com.tomer.anibadi.util.Utils
import java.io.ByteArrayInputStream
import java.io.File
import java.util.Date
import kotlin.concurrent.thread
import kotlin.random.Random

class AddUser : AppCompatActivity(), View.OnClickListener, ChildManager.CLis {

    //region GLOBAL

    private val b by lazy { ActivityAddUserBinding.inflate(layoutInflater) }
    private val repo by lazy { Repo(this) }
    private val calDia by lazy { crCal() }
    private val gson by lazy { Gson() }

    private var isPreg = false
    private var isLacto = false

    private lateinit var editDate: EditText
    private lateinit var fileAadhar: File
    private var motherId = Date().time.toString()

    private val listChild = mutableListOf<ChildManager>()
    private var isContra = false

    private val resultLauncher = registerForActivityResult(object : ActivityResultContract<Unit, ByteArray?>() {
        override fun createIntent(context: Context, input: Unit): Intent {
            return Intent(context, CameraHandler::class.java)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): ByteArray? {
            return if (resultCode == RESULT_OK) {
                b.etAadhar.setText(intent!!.getStringExtra("aano"))
                b.etDob.setText(intent.getStringExtra("dob"))
                val sts = intent.getStringArrayExtra("names")
                var reps = 0
                val climax = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                sts!!.forEach { str ->
                    val data1 = ClipData.newPlainText("names${reps++}", str)
                    climax.setPrimaryClip(data1)
                }
                intent.getByteArrayExtra("imageData")
            } else null
        }

    }) { arr ->
        if (arr != null) {
            try {
                val ois = fileAadhar.outputStream()
                CipherUtils.encrypt(ByteArrayInputStream(arr), ois)
                b.imgadhar.setImageBitmap(BitmapFactory.decodeByteArray(arr, 0, arr.size))
                Log.d("TAG--", "82: ${arr.size}")
            } catch (e: Exception) {
                Log.e("TAG--", ": 83", e)
            }
        }
    }

    //endregion GLOBAL

    //region ACTIVITY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(b.root)
        b.apply {
            etDob.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {
                    if (p2 > p3) return
                    if (p0.length == 2 || p0.length == 5) {
                        "$p0/".also { b.etDob.setText(it) }
                        b.etDob.setSelection(b.etDob.text.length)
                    }
                }

                override fun afterTextChanged(p0: Editable?) {
                }

            })
            etP.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {
                    if (p2 > p3) return
                    if (p0.length == 2 || p0.length == 5) {
                        "$p0/".also { b.etP.setText(it) }
                        b.etP.setSelection(b.etP.text.length)
                    }
                }

                override fun afterTextChanged(p0: Editable?) {
                }

            })

            btBack.setOnClickListener(this@AddUser)
            btDOB.setOnClickListener(this@AddUser)
            btDOP.setOnClickListener(this@AddUser)
            btLac.setOnClickListener(this@AddUser)
            btPreg.setOnClickListener(this@AddUser)
            btAddChild.setOnClickListener(this@AddUser)
            btAddChild.setOnClickListener(this@AddUser)
            btDone.setOnClickListener(this@AddUser)
            imgadhar.setOnClickListener(this@AddUser)
        }

        if (intent.hasExtra("id")) {
            val crrmod = repo.getMother(intent.getStringExtra("id").toString())
            isLacto = crrmod.isLactating
            isPreg = crrmod.isPreg
            setAccordingly()


            motherId = crrmod.ID
            b.apply {
                etAadhar.setText(crrmod.aadNo)

                etName.setText(crrmod.name)
                etHName.setText(crrmod.hName)
                etDob.setText(crrmod.dob)

                etPhno.setText(crrmod.phno)

                "Update".also { btDone.text = it }
                if (isLacto) {
                    etP.setText(crrmod.doLaDeli)
                    btLac.background = ContextCompat.getDrawable(this@AddUser, R.drawable.round_white)
                }

                if (isPreg) {
                    etP.setText(crrmod.doLastP)
                    btPreg.background = ContextCompat.getDrawable(this@AddUser, R.drawable.round_white)
                }

                btToapp.setOnClickListener(this@AddUser)
            }


            thread {
                val newFile = File(getExternalFilesDir("aadhar"), "${motherId}")
                if (newFile.exists()) {
                    val tempFile = File(externalCacheDir, motherId)
                    tempFile.deleteOnExit()
                    CipherUtils.decrypt(newFile.inputStream(), tempFile.outputStream())
                    b.imgadhar.post {
                        b.imgadhar.setImageURI(tempFile.getURI())
                    }
                }
            }


            for (i in 0 until crrmod.childs.size) adderChild(i, crrmod.childs[i])


        } else {
            adderChild(0, null)
            isContra = true
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.topfadein, R.anim.fadetobottom)
    }

    //endregion ACTIVITY

    //region USER DEFINED


    private fun getRandomPhone(): String {
        val all = "0123456798"
        val built = StringBuilder("7")
        for (i in 0..8) {
            built.append(all[Random.nextInt(9)])
        }
        return built.toString()
    }

    private fun adderChild(index: Int, mod: Chilren?) {
        val childManager = ChildManager(this, layoutInflater, b.childCont, this, listChild.size, mod)
        b.childCont.addView(childManager.b.root, index)
        listChild.add(childManager)
    }

    private fun crCal(): AlertDialog {
        val build = AlertDialog.Builder(this)
        val bi = CalDiaBinding.inflate(layoutInflater)
        build.setView(bi.root)
        val pic = bi.datePixker
        bi.btDone.setOnClickListener {
            setDate(pic.dayOfMonth, pic.month, pic.year)
            calDia.dismiss()
        }
        return build.create()
    }

    private fun setDate(day: Int, month: Int, year: Int) {
        val dy = String.format("%02d", day)
        val mon = String.format("%02d", (month + 1))
        "$dy/$mon/$year".also {
            editDate.setText(it)
        }
        editDate.setSelection(editDate.text.length)
    }

    private fun setAccordingly() {
        when {
            isLacto -> {
                b.apply {
                    b.llPorL.visibility = View.VISIBLE
                    b.tvPregOrLac.visibility = View.VISIBLE
                    "Date of Delivery".also { b.tvPregOrLac.text = it }
                }
            }
            isPreg -> {
                b.apply {
                    b.llPorL.visibility = View.VISIBLE
                    b.tvPregOrLac.visibility = View.VISIBLE
                    "LMP Date".also { b.tvPregOrLac.text = it }
                }
            }
            else -> {
                b.apply {
                    b.llPorL.visibility = View.GONE
                    b.tvPregOrLac.visibility = View.GONE
                }
            }
        }
    }

    private fun generateUri(id: String) {
        val stoDir = getExternalFilesDir("aadhar")
        val imgFile = File(stoDir, id)
        fileAadhar = imgFile
    }


    private fun saveMother() {
        if (b.etName.text.isNullOrEmpty()) {
            b.etName.error = "Enter Name"
            b.etName.requestFocus()
            return
        }
        if (b.etHName.text.isNullOrEmpty()) {
            b.etHName.error = "Enter Name"
            b.etHName.requestFocus()
            return
        }
        if (b.etDob.text.isNullOrEmpty() || b.etDob.text.length < 10) {
            b.etDob.error = "Incorrect DOB"
            b.etDob.requestFocus()
            return
        }
        if (!(b.etPhno.text.length <= 1 || b.etPhno.text.length >= 10)) {
            b.etPhno.error = "Incorrect No"
            b.etPhno.requestFocus()
            return
        }
        if (isPreg || isLacto) {
            if (b.etP.text.isNullOrEmpty() || b.etP.text.length < 10) {
                b.etP.error = "Incorrect"
                return
            }
        }
        val children = mutableListOf<Chilren>()
        for (i in listChild) {
            if (i.isVisible) {
                if (i.b.etName.text.isNullOrEmpty()) {
                    i.b.etName.error = "Enter Name"
                    return
                }
                if (i.b.etDob.text.isNullOrEmpty() || i.b.etDob.text.length < 10) {
                    i.b.etDob.error = "Incorrect DOB"
                    return
                }
                children.add(
                    Chilren(
                        i.b.etName.text.toString().trim(),
                        i.b.etDob.text.toString().trim(),
                        i.isBoy
                    )
                )
            }
        }
        if (b.etPhno.text.isNullOrEmpty()) {
            b.etPhno.setText(getRandomPhone())
        }

        val mother = Mother(
            motherId,
            b.etName.text.toString().trim(),
            b.etDob.text.toString().trim(),
            b.etHName.text.toString().trim(),
            b.etPhno.text.toString().trim(),
            b.etAadhar.text.toString().trim(),
            b.etP.text.toString().trim(),
            b.etP.text.toString().trim(),
            isPreg,
            isLacto,
            children
        )
        repo.saveMother(mother)
        for (i in children) Utils.setAlarm(i.dob, this)
        if (isContra) {
            val intent = Intent()
            intent.putExtra("data", gson.toJson(mother))
            setResult(100, intent)
        }
        finish()
    }


    private fun crWidget(forMother: Boolean, pos: Int) {

        if (forMother) {
            val int = Intent(this, WidService::class.java)
            var icon = R.drawable.ic_women
            if (isLacto) icon = R.drawable.ic_lactation
            if (isPreg) icon = R.drawable.ic_preg
            val mw = WidgetMod(
                b.etName.text.toString().trim(),
                b.etDob.text.toString().trim(),
                icon,
                b.etP.text.toString().trim(),
                b.etPhno.text.toString().trim(),
                b.etAadhar.text.toString().trim()
            )
            int.putExtra("data", gson.toJson(mw))
            startService(int)
        } else {
            val int = Intent(this, WidService::class.java)
            val t = listChild[pos].mod
            val mon = Utils.getMonths(t!!.dob)
            val icon = when {
                mon < 6 -> 0
                mon < 36 -> 1
                mon < 72 -> 2
                else -> 0
            }
            val mw = WidgetMod(t.name, t.dob, icon, b.etName.text.toString(), b.etPhno.text.toString(), b.etAadhar.text.toString())
            int.putExtra("data", gson.toJson(mw))
            startService(int)
        }
    }


    private fun File.getURI(): Uri = FileProvider.getUriForFile(applicationContext, "com.tomer.anibadi.fileProvider", this)

    //endregion USER DEFINED

    //region INTERFACES
    override fun onClick(view: View) {

        when (view.id) {
            b.btBack.id -> finish()
            b.btDOB.id -> {
                calDia.show()
                editDate = b.etDob
            }
            b.btDOP.id -> {
                calDia.show()
                editDate = b.etP
            }
            b.btDone.id -> saveMother()
            b.btToapp.id -> crWidget(true, 0)

            b.imgadhar.id -> {
                generateUri(motherId)
                resultLauncher.launch(Unit)
                overridePendingTransition(0, R.anim.fadezoomout)
            }

            b.btAddChild.id -> adderChild(listChild.size, null)


            b.btLac.id -> {
                isLacto = !isLacto
                if (isLacto)
                    b.btLac.background = ContextCompat.getDrawable(this, R.drawable.round_white)
                else b.btLac.background = ContextCompat.getDrawable(this, R.drawable.round_non_sel)

                isPreg = false
                b.btPreg.background = ContextCompat.getDrawable(this, R.drawable.round_non_sel)

                setAccordingly()

            }
            b.btPreg.id -> {
                isPreg = !isPreg
                if (isPreg)
                    b.btPreg.background = ContextCompat.getDrawable(this, R.drawable.round_white)
                else b.btPreg.background = ContextCompat.getDrawable(this, R.drawable.round_non_sel)

                isLacto = false
                b.btLac.background = ContextCompat.getDrawable(this, R.drawable.round_non_sel)
                setAccordingly()
            }
        }

    }


    override fun pnCLose(pos: Int) {
        listChild[pos].b.root.visibility = View.GONE
    }

    override fun onDia(pos: Int) {
        calDia.show()
        editDate = listChild[pos].b.etDob
    }

    override fun widget(pos: Int) {
        crWidget(false, pos)
    }

    //endregion INTERFACES
}