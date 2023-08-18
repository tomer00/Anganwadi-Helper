package com.tomer.anibadi

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.tomer.anibadi.adap.AdapMain
import com.tomer.anibadi.databinding.ActivityMainBinding
import com.tomer.anibadi.modal.Mother
import com.tomer.anibadi.modal.MotherRv
import com.tomer.anibadi.util.KeyStoreUtil
import com.tomer.anibadi.util.Repo
import java.io.File
import kotlin.random.Random


class MainActivity : AppCompatActivity(), View.OnClickListener {

    //region GLOBAL

    private val b by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val repo by lazy { Repo(this) }
    private val adap by lazy { AdapMain(lis) }
    private val gson by lazy { Gson() }
    private val mainList = mutableListOf<Mother>()
    private val listRv = mutableListOf<MotherRv>()
    private val lis = object : AdapMain.M_L {
        override fun onMainLis(pos: Int) {
            perfornNextScreen(pos)
        }
    }

    private val contarct = registerForActivityResult(object : ActivityResultContract<String, String>() {

        override fun createIntent(context: Context, input: String): Intent {
            return Intent(this@MainActivity, AddUser::class.java)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): String {
            return if (resultCode == 100) {
                intent?.getStringExtra("data")!!
            } else "no"
        }

    }) {
        if (!it.equals("no")) {
            val i = gson.fromJson(it, Mother::class.java)
            mainList.add(i)
            var col = ContextCompat.getColor(this, R.color.colnor)
            var icon = R.drawable.ic_women
            if (i.isPreg) {
                icon = R.drawable.ic_preg
                col = ContextCompat.getColor(this, R.color.colPreg)
            }
            if (i.isLactating) {
                icon = R.drawable.ic_lactation
                col = ContextCompat.getColor(this, R.color.colLac)
            }
            val mRv = MotherRv(i.ID, i.name, i.hName, col, i.isPreg, i.isLactating, ContextCompat.getDrawable(this, icon)!!)
            listRv.add(mRv)
            adap.mainL.beginBatchedUpdates()
            adap.mainL.add(mRv)
            adap.mainL.endBatchedUpdates()
        }
    }


    private var isPreg = false
    private var isLacto = false

    //endregion GLOBAL

    //region ACTIVITY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(b.root)

        mainList.addAll(repo.getALLMother())
        for (i in mainList) {
            var col = ContextCompat.getColor(this, R.color.colnor)
            var icon = R.drawable.ic_women
            if (i.isPreg) {
                icon = R.drawable.ic_preg
                col = ContextCompat.getColor(this, R.color.colPreg)
            }
            if (i.isLactating) {
                icon = R.drawable.ic_lactation
                col = ContextCompat.getColor(this, R.color.colLac)
            }
            listRv.add(MotherRv(i.ID, i.name, i.hName, col, i.isPreg, i.isLactating, ContextCompat.getDrawable(this, icon)!!))
        }
        b.rvMain.adapter = adap
        adap.search(listRv, false)

        b.apply {
            btLac.setOnClickListener(this@MainActivity)
            btPreg.setOnClickListener(this@MainActivity)
            fabAdd.setOnClickListener(this@MainActivity)

            b.etSea.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(text: CharSequence, p1: Int, p2: Int, p3: Int) {
                    searchE(text.toString(), p2 > p3)
                }

                override fun afterTextChanged(p0: Editable?) {
                }

            })
        }


        checkForPass()
    }

    //endregion ACTIVITY

    //region USER DEFINED
    private fun searchE(newText: String, isPos: Boolean) {
        val newL = mutableListOf<MotherRv>()
        for (i in listRv) {
            if (!isLacto && !isPreg) {
                if (i.name.lowercase()
                        .startsWith(
                            newText.lowercase()
                                .trim()
                        )) newL.add(i)
            } else if (i.name.lowercase().startsWith(newText.lowercase().trim()) && i.isPreg == isPreg && i.isLacta == isLacto) newL.add(i)

        }
        adap.search(newL, isPos)
        b.rvMain.smoothScrollToPosition(0)
    }

    private fun perfornNextScreen(pos: Int) {
        for (i in mainList) {
            if (i.ID == adap.mainL[pos].ID) {
                val intent = Intent(this@MainActivity, AddUser::class.java)
                intent.putExtra("id", i.ID)
                startActivity(intent)
                overridePendingTransition(0, R.anim.fadezoomout)
                break
            }
        }
    }


    private fun checkForPass() {
        val f = File(getExternalFilesDir("secret"), "key")
        if (!f.exists()) {
            val r = Random(System.currentTimeMillis())
            val sb = r.nextBytes(32)
            KeyStoreUtil.encString(sb, f.outputStream())
        }
    }


    //endregion USER DEFINED

    //region INTERFACES
    override fun onClick(view: View) {

        when (view.id) {
            b.btLac.id -> {
                isLacto = !isLacto
                if (isLacto)
                    b.btLac.background = ContextCompat.getDrawable(this, R.drawable.round_sel)
                else b.btLac.background = ContextCompat.getDrawable(this, R.drawable.round_non_sel)

                isPreg = false
                b.btPreg.background = ContextCompat.getDrawable(this, R.drawable.round_non_sel)
                searchE(b.etSea.text.toString(), isLacto)
            }
            b.btPreg.id -> {
                isPreg = !isPreg
                if (isPreg)
                    b.btPreg.background = ContextCompat.getDrawable(this, R.drawable.round_sel)
                else b.btPreg.background = ContextCompat.getDrawable(this, R.drawable.round_non_sel)

                isLacto = false
                b.btLac.background = ContextCompat.getDrawable(this, R.drawable.round_non_sel)
                searchE(b.etSea.text.toString(), isPreg)
            }
            b.fabAdd.id -> {
                if (askPermi()) return
                contarct.launch("")
                overridePendingTransition(R.anim.scalefrombot, R.anim.fadeout)
            }
        }

    }

    //endregion INTERFACES

    //region PERMISSIONS----->>

    private fun checkPermiStorage(): Boolean {

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            val result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            val result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
        }

    }

    private fun checkPermiCam(): Boolean {
        val result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun askPermi(): Boolean {
        var ans = false
        if (!checkPermiStorage()) {
            ans = true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                try {
                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                    intent.addCategory("android.intent.category.DEFAULT")
                    intent.data = Uri.parse(String.format("package:%s", applicationContext.packageName))
                    startActivityForResult(intent, 2296)
                } catch (e: Exception) {
                    val intent = Intent().apply {
                        action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                    }
                    startActivityForResult(intent, 2296)
                }
            } else {
                //below android 11
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA), 100)
            }
        }

        if (!checkPermiCam()) {
            ans = true
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 100)
        }

        if (!Settings.canDrawOverlays(this)) {
            ans = true
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${packageName}")
            )
            startActivityForResult(intent, 101)
        }

        return ans
    }
    //endregion PERMISSIONS----->>
}