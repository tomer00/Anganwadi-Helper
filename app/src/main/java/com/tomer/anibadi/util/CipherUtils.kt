package com.tomer.anibadi.util

import android.content.Context
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import javax.crypto.Cipher
import javax.crypto.CipherOutputStream
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class CipherUtils {


    companion object {

        private fun getEncCipher(con: Context): Cipher {
            return Cipher.getInstance(KeyStoreUtil.TRANS_CIPHER).apply {
                init(Cipher.ENCRYPT_MODE, getKey(con), getIv(con))
            }
        }

        private fun getIv(con: Context): IvParameterSpec {
            val f = File(con.getExternalFilesDir("secret"), "key")
            return IvParameterSpec(KeyStoreUtil.decString(f.inputStream()).sliceArray(16..31))
        }

        private fun getKey(con: Context): SecretKeySpec {
            val f = File(con.getExternalFilesDir("secret"), "key")
            return SecretKeySpec(KeyStoreUtil.decString(f.inputStream()).sliceArray(0..15), KeyStoreUtil.ALGORITHM)
        }

        private fun getDecCipher(con: Context): Cipher {
            return Cipher.getInstance(KeyStoreUtil.TRANS_CIPHER).apply {
                init(Cipher.DECRYPT_MODE, getKey(con), getIv(con))
            }
        }

        fun encrypt(ins: InputStream, out1: OutputStream, con: Context) {
            val out = CipherOutputStream(out1, getEncCipher(con))
            ins.copyTo(out)
            ins.close()
        }

        fun decrypt(ins: InputStream, out1: OutputStream, con: Context) {
            val out = CipherOutputStream(out1, getDecCipher(con))
            ins.copyTo(out)
            ins.close()
        }
    }
}