package com.tomer.anibadi.util

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.io.InputStream
import java.io.OutputStream
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

class KeyStoreUtil {
    //TEE Hardware Trusted executing environment
    companion object {
        const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
        private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC
        private const val PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7
        const val TRANS_CIPHER = "$ALGORITHM/$BLOCK_MODE/$PADDING"


        private fun getKey(): SecretKey {
            val keystore = KeyStore.getInstance("AndroidKeyStore").apply {
                load(null)
            }
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

        fun encString(bytes: ByteArray, ois: OutputStream) {
            val cipherENC = Cipher.getInstance(TRANS_CIPHER).apply {
                this.init(Cipher.ENCRYPT_MODE, getKey())
            }
            val ecBArray = cipherENC.doFinal(bytes)
            ois.use {
                it.write(cipherENC.iv.size)
                it.write(cipherENC.iv)
                it.flush()
                it.write(ecBArray)
                it.flush()
            }
        }

        fun decString(ins: InputStream): ByteArray {
            return ins.use {
                val i = it.read()
                val ivARR = ByteArray(i)
                it.read(ivARR)
                val cipDec = Cipher.getInstance(TRANS_CIPHER).apply {
                    init(
                        Cipher.DECRYPT_MODE, getKey(), IvParameterSpec(ivARR)
                    )
                }
                cipDec.doFinal(it.readBytes())
            }
        }
    }
}