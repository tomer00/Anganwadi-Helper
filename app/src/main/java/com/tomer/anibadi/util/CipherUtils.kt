package com.tomer.anibadi.util

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.StandardCharsets
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.CipherOutputStream
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

class CipherUtils {
    //TEE Hardware Trusted executing environment
    companion object {
        private const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
        private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC
        private const val PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7
        private const val TRANS_CIPHER = "$ALGORITHM/$BLOCK_MODE/$PADDING"


        private val cipherENC = Cipher.getInstance(TRANS_CIPHER).apply {
            this.init(Cipher.ENCRYPT_MODE, getKey())
        }
        private val keystore = KeyStore.getInstance("AndroidKeyStore").apply {
            load(null)
        }

        private fun getKey(): SecretKey {
            val existing: SecretKey? = keystore?.getKey("secret_key", null) as? SecretKey
            return existing ?: createKey()
        }

        private fun createKey(): SecretKey {
            return KeyGenerator.getInstance(ALGORITHM).apply {
                init(
                    KeyGenParameterSpec.Builder(
                        "secret_key", KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                    )
                        .setBlockModes(BLOCK_MODE)
                        .setEncryptionPaddings(PADDING)
                        .setUserAuthenticationRequired(false)
                        .setRandomizedEncryptionRequired(true)
                        .build()
                )
            }.generateKey()
        }

        fun encString(string: String, ois: OutputStream) {
            val ecBArray = cipherENC.doFinal(string.toByteArray(StandardCharsets.UTF_8))
            ois.use {
                it.write(cipherENC.iv.size)
                it.write(cipherENC.iv)
                it.flush()
                it.write(ecBArray.size)
                it.write(ecBArray)
                it.flush()
            }
        }

        fun decString(ins: InputStream): String {
            return ins.use {
                var i = it.read()
                val ivARR = ByteArray(i)
                it.read(ivARR)
                val cipDec = Cipher.getInstance(TRANS_CIPHER).apply {
                    init(
                        Cipher.DECRYPT_MODE, getKey(), IvParameterSpec(ivARR)
                    )
                }

                i = it.read()
                String(cipDec.doFinal(it.readBytes()))
            }
        }

        fun encrypt(ins: InputStream, out1: OutputStream) {
            val out = CipherOutputStream(out1, cipherENC)
            out.use { ois ->
                ois.write(cipherENC.iv)
//                Log.d("TAG--", "encrypt: ${ois.}")
                var count: Int
                val buffer = ByteArray(1024)
                while (ins.read(buffer).also { count = it } > 0) {
                    ois.write(buffer, 0, count)
                }
                ins.close()
            }
        }

        fun decrypt(ins: InputStream, out1: OutputStream) {
            val ivArray = ByteArray(16)
            ins.read(ivArray)
            Log.d("TAG--", "decrypt: ${ivArray.size}")
            val cipher = Cipher.getInstance(TRANS_CIPHER).apply {
                init(Cipher.DECRYPT_MODE, getKey(), IvParameterSpec(ivArray))
            }
            val out = CipherOutputStream(out1, cipher)
            out.use { ois ->
                var count: Int
                val buffer = ByteArray(1024)
                while (ins.read(buffer).also { count = it } > 0) {
                    ois.write(buffer, 0, count)
                }
                ins.close()
            }
        }
    }
}