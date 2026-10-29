package com.meditrack.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DoseDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(dose: DoseEntity): Long

    @Query("SELECT * FROM doses WHERE medicineId = :medicineId AND reminderTime = :time AND date = :date LIMIT 1")
    suspend fun getDose(medicineId: Long, time: String, date: String): DoseEntity?

    @Query("SELECT * FROM doses WHERE date = :date ORDER BY reminderTime ASC")
    fun getDosesForDate(date: String): Flow<List<DoseEntity>>

    @Query("SELECT * FROM doses WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC, reminderTime ASC")
    fun getDosesForRange(startDate: String, endDate: String): Flow<List<DoseEntity>>

    @Query("SELECT * FROM doses ORDER BY date DESC, reminderTime ASC")
    fun getAllDoses(): Flow<List<DoseEntity>>

    @Query("SELECT COUNT(*) FROM doses WHERE status = 'TAKEN' AND date BETWEEN :startDate AND :endDate")
    fun getTakenCount(startDate: String, endDate: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM doses WHERE date BETWEEN :startDate AND :endDate")
    fun getTotalCount(startDate: String, endDate: String): Flow<Int>

    @Query("DELETE FROM doses WHERE medicineId = :medicineId")
    suspend fun deleteDosesForMedicine(medicineId: Long)
}
