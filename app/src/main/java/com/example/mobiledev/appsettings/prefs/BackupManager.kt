package com.example.mobiledev.appsettings.prefs

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.core.content.edit
import com.example.mobiledev.network.Pony
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

object BackupManager {
    private const val BACKUP_FILE_NAME = "ponies_backup.json"
    private const val KEY_USE_BACKUP = "use_backup"
    private val PUBLIC_DIR = Environment.DIRECTORY_DOCUMENTS


    fun setUseBackup(context: Context, use: Boolean) {
        context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE).edit {
            putBoolean(KEY_USE_BACKUP, use)
        }
    }


    fun shouldUseBackup(context: Context): Boolean {
        return context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
            .getBoolean(KEY_USE_BACKUP, false)
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    private fun findPublicUri(context: Context): Uri? {
        val collection = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        return context.contentResolver.query(
            collection,
            arrayOf(MediaStore.MediaColumns._ID),
            "${MediaStore.MediaColumns.DISPLAY_NAME} = ? AND ${MediaStore.MediaColumns.RELATIVE_PATH} = ?",
            arrayOf(BACKUP_FILE_NAME, "$PUBLIC_DIR/"),
            null
        )?.use { cursor ->
            if (cursor.moveToFirst()) ContentUris.withAppendedId(collection, cursor.getLong(0)) else null
        }
    }


    @Suppress("DEPRECATION")
    private fun legacyPublicFile(): File {
        val dir = Environment.getExternalStoragePublicDirectory(PUBLIC_DIR)
        dir.mkdirs()
        return File(dir, BACKUP_FILE_NAME)
    }


    private fun hasPublic(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            findPublicUri(context) != null
        } else {
            legacyPublicFile().exists()
        }
    }


    private fun writePublic(context: Context, text: String): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val resolver = context.contentResolver
            val uri = findPublicUri(context) ?: resolver.insert(
                MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY),
                ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, BACKUP_FILE_NAME)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/json")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, PUBLIC_DIR)
                }
            ) ?: return false
            resolver.openOutputStream(uri, "wt")?.use { it.write(text.toByteArray()) } ?: return false
            true
        } else {
            legacyPublicFile().writeText(text)
            true
        }
    }


    private fun readPublic(context: Context): String? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val uri = findPublicUri(context) ?: return null
            context.contentResolver.openInputStream(uri)?.use { it.readBytes().toString(Charsets.UTF_8) }
        } else {
            val file = legacyPublicFile()
            if (file.exists()) file.readText() else null
        }
    }


    private fun deletePublic(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val uri = findPublicUri(context) ?: return true
            context.contentResolver.delete(uri, null, null) > 0
        } else {
            val file = legacyPublicFile()
            !file.exists() || file.delete()
        }
    }


    suspend fun hasBackup(context: Context): Boolean = withContext(Dispatchers.IO) {
        try {
            hasPublic(context)
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }


    suspend fun createBackup(context: Context, ponies: List<Pony>): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val jsonArray = JSONArray()
                for (pony in ponies) {
                    val obj = JSONObject().apply {
                        put("id", pony.id)
                        put("name", pony.name)
                        put("sex", pony.sex)
                        put("residence", pony.residence)
                        put("kind", JSONArray(pony.kind))
                        put("imageUrl", pony.imageUrl)
                    }
                    jsonArray.put(obj)
                }
                val written = writePublic(context, jsonArray.toString())
                if (written) {
                    File(context.filesDir, BACKUP_FILE_NAME).delete()
                }
                written
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }


    suspend fun restoreBackup(context: Context): List<Pony>? = withContext(Dispatchers.IO) {
        try {
            val jsonString = readPublic(context) ?: return@withContext null
            val jsonArray = JSONArray(jsonString)
            val ponies = mutableListOf<Pony>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val kindArray = obj.optJSONArray("kind")
                val kindList = if (kindArray != null) {
                    (0 until kindArray.length()).map { index ->
                        kindArray.getString(index)
                    }
                } else {
                    val oldKind = obj.optString("kind", "")
                    if (oldKind.isNotBlank()) listOf(oldKind) else emptyList()
                }
                ponies.add(
                    Pony(
                        id = obj.optInt("id", 0),
                        name = obj.getString("name"),
                        sex = obj.optString("sex").takeIf { it.isNotBlank() },
                        residence = obj.optString("residence").takeIf { it.isNotBlank() },
                        kind = kindList,
                        imageUrl = obj.optString("imageUrl", "")
                    )
                )
            }
            ponies
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    suspend fun deleteBackup(context: Context): Boolean = withContext(Dispatchers.IO) {
        try {
            val text = readPublic(context)
            if (text != null) {
                File(context.filesDir, BACKUP_FILE_NAME).writeText(text)
            }
            val deleted = deletePublic(context)
            if (deleted) {
                setUseBackup(context, false)
            }
            deleted
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}