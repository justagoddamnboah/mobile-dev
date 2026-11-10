package com.example.mobiledev.storage

import com.example.mobiledev.MobileApplication
import com.example.mobiledev.appsettings.prefs.BackupManager
import com.example.mobiledev.network.Pony
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.IOException

class FileManager : OfflineStorage {

    override fun load(): Flow<List<Pony>> = flow {
        val context = MobileApplication.mobileApplicationContext()
        val data = BackupManager.restoreBackup(context)
            ?: throw IOException("Резервная копия отсутствует или повреждена")
        emit(data)
    }.flowOn(Dispatchers.IO)

    override suspend fun save(items: List<Pony>) {
        val context = MobileApplication.mobileApplicationContext()
        if (!BackupManager.createBackup(context, items)) {
            throw IOException("Не удалось сохранить резервную копию")
        }
    }
}