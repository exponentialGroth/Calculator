package com.exponential_groth.calculator.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Variable::class],
    version = 1,
    exportSchema = false
)
abstract class StorageDB: RoomDatabase() {
    abstract fun getVariablesDao(): VariablesDao

    companion object {
        @Volatile private var instance: StorageDB? = null

        fun getInstance(context: Context): StorageDB {
            return instance?: synchronized(this) {
                instance?: buildDatabase(context).also {
                    instance = it
                }
            }
        }

        private fun buildDatabase(context: Context): StorageDB {
            return Room.databaseBuilder(context, StorageDB::class.java, "storageDB").fallbackToDestructiveMigration().build()
        }
    }
}