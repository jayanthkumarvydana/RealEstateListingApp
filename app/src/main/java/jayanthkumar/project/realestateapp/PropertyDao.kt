package jayanthkumar.project.realestateapp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PropertyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProperty(property: PropertyEntity)

    @Query("SELECT * FROM properties ORDER BY propertyName ASC")
    suspend fun getAllProperties(): List<PropertyEntity>

    @Query("DELETE FROM properties WHERE id = :propertyId")
    suspend fun deletePropertyById(propertyId: String)
    @Query("SELECT * FROM properties WHERE id = :propertyId LIMIT 1")
    suspend fun getPropertyById(propertyId: String): PropertyEntity?
}