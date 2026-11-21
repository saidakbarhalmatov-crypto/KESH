package com.kesh.finance.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: TransactionEntity): Long

    @Delete
    suspend fun delete(transaction: TransactionEntity)

    @Query("DELETE FROM transactions")
    suspend fun deleteAll()

    @Query(
        "SELECT COUNT(*) FROM transactions " +
            "WHERE amount = :amount AND bankName = :bankName AND timestamp >= :sinceTimestamp"
    )
    suspend fun countRecentDuplicate(
        amount: Double,
        bankName: String,
        sinceTimestamp: Long
    ): Int
}

@Database(
    entities = [TransactionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class KeshDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile
        private var INSTANCE: KeshDatabase? = null

        fun getInstance(context: Context): KeshDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    KeshDatabase::class.java,
                    "kesh_pro_v2.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
