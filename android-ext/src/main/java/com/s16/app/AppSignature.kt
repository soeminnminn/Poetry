package com.s16.app

import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.util.Base64

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import kotlin.experimental.and

/**
 * MD5 :  70:20:7C:E8:4E:E9:4D:6E:0F:A8:F6:81:47:B6:52:AF
 * SHA1 : 1B:B5:CC:4A:FC:04:FD:06:64:2B:98:EB:7F:86:31:C2:CD:C4:94:A9
 * SHA256 : 6C:38:D3:AE:A1:D0:3D:74:69:2D:83:FE:EF:B2:B0:AE:EB:09:77:80:4D:FA:27:8B:DD:B0:01:59:AE:22:06:F4
 * SHA Base64 : G7XMSvwE/QZkK5jrf4Yxws3ElKk=
 */
@Suppress("DEPRECATION")
@SuppressLint("PackageManagerGetSignatures")
class AppSignature(context: Context) : ContextWrapper(context) {

    private var mSignatures: ByteArray? = null

    init {
        try {
            val signatures = packageManager.getPackageInfo(
                packageName,
                PackageManager.GET_SIGNATURES
            ).signatures

            for (signature in signatures) {
                mSignatures = signature.toByteArray()
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }

    /**
     * Get all the app signatures for the current package
     */
    // Get all package signatures for the current package
    // For each signature create a compatible hash
    val signatures: ByteArray = mSignatures!!

    val bytesMD5 : ByteArray?
        get() = getBytes("MD5")


    val bytesSHA : ByteArray?
        get() = getBytes("SHA")

    val bytesSHA1 : ByteArray?
        get() = getBytes("SHA-1")

    val bytesSHA256 : ByteArray?
        get() = getBytes("SHA-256")

    val hexStringMD5 : String
        get() = getHexString(bytesMD5)

    val hexStringSHA1 : String
        get() = getHexString(bytesSHA1)

    val hexStringSHA256 : String
        get() = getHexString(bytesSHA256)

    val base64SHA : String
        get() = Base64.encodeToString(bytesSHA, Base64.DEFAULT)

    private fun getBytes(algorithm: String): ByteArray? = mSignatures?.let {
        try {
            val md = MessageDigest.getInstance(algorithm)
            md.update(it)
            md.digest()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
            null
        }
    }

    override fun toString(): String {
        return """MD5        : $hexStringMD5, 
            SHA1       : $hexStringSHA1, 
            SHA256     : $hexStringSHA256, 
            SHA(Base64): $base64SHA"""
    }

    private fun getHexString(bytes: ByteArray?) : String = bytes?.let {
        val sb = StringBuilder()
        for (b in it) {
            if (sb.isNotEmpty()) {
                sb.append(":")
            }
            sb.append(String.format("%02x", b and 0xff.toByte()).toUpperCase(Locale.ENGLISH))
        }
        sb.toString()
    } ?: ""
}