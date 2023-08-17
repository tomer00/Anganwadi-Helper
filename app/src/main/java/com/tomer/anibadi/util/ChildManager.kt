package com.tomer.anibadi.util

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.tomer.anibadi.R
import com.tomer.anibadi.databinding.ChildRowBinding
import com.tomer.anibadi.modal.Chilren

class ChildManager(val con: Context, inflater: LayoutInflater, cont: ViewGroup, val lis: CLis, val pos: Int, val mod: Chilren?) : View.OnClickListener {

    var isBoy = true
    var isVisible = true
    val b: ChildRowBinding

    init {
        val v = inflater.inflate(R.layout.child_row, cont, false)
        b = ChildRowBinding.bind(v)
        b.apply {
            btBOy.setOnClickListener(this@ChildManager)
            btGirl.setOnClickListener(this@ChildManager)
            btDOB.setOnClickListener(this@ChildManager)
            btCross.setOnClickListener(this@ChildManager)
            etDob.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {
                    if (p2 > p3) return
                    if (p0.length == 2 || p0.length == 5) {
                        b.etDob.setText("$p0/")
                        b.etDob.setSelection(b.etDob.text.length)
                    }
                }

                override fun afterTextChanged(p0: Editable) {
                }

            })
        }

        if (mod != null) {
            b.apply {
                etName.setText(mod.name)
                etDob.setText(mod.dob)
                isBoy = mod.isBoy
                if (!mod.isBoy) {
                    b.btGirl.background = ContextCompat.getDrawable(con, R.drawable.round_sel)
                    b.btBOy.background = ContextCompat.getDrawable(con, R.drawable.round_non_sel)
                }
                root.setOnClickListener {
                    lis.widget(pos)
                }
            }
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            b.btBOy.id -> {
                isBoy = true
                b.btBOy.background = ContextCompat.getDrawable(con, R.drawable.round_sel)
                b.btGirl.background = ContextCompat.getDrawable(con, R.drawable.round_non_sel)
            }
            b.btGirl.id -> {
                isBoy = false
                b.btGirl.background = ContextCompat.getDrawable(con, R.drawable.round_sel)
                b.btBOy.background = ContextCompat.getDrawable(con, R.drawable.round_non_sel)
            }
            b.btDOB.id -> {
                lis.onDia(pos)
            }
            b.btCross.id -> {
                lis.pnCLose(pos)
                isVisible = false
            }
        }
    }

    interface CLis {
        fun pnCLose(pos: Int)
        fun onDia(pos: Int)
        fun widget(pos: Int)
    }
}