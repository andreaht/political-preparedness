package com.example.android.politicalpreparedness.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.android.politicalpreparedness.network.models.Election

@Dao
interface ElectionDao {

    //TODO: Add insert query
    @Insert
    suspend fun insert(election: Election)

    /**
     * Selects and returns all rows in the table,
     *
     * sorted by start time in descending order.
     */
    @Query("SELECT * FROM election_table ORDER BY id DESC")
    fun getAllElections(): LiveData<List<Election>>

    /**
     * Selects and returns the row that matches the supplied start time, which is our key.
     *
     * @param id election_id to match
     */
    @Query("SELECT * from election_table WHERE id = :id")
    suspend fun get(id: Long): Election?

    @Query("DELETE FROM election_table WHERE id = :id")
    suspend fun deleteByElectionId(id: Long)

    /**
     * Deletes all values from the table.
     *
     * This does not delete the table, only its contents.
     */
    @Query("DELETE FROM election_table")
    suspend fun clear()

}