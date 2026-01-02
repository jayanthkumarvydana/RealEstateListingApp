package app.s3397892jayanthkumar.realestate



import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "properties")
data class PropertyEntity(
    @PrimaryKey val id: String,
    val propertyName: String,
    val contact: String,
    val description: String,
    val price: String,
    val place: String,
    val beds: String,
    val baths: String,
    val area: String,
    val type: String,
    val amenities: String
)

fun Property.toEntity(): PropertyEntity {
    return PropertyEntity(
        id = id,
        propertyName = propertyName,
        contact = contact,
        description = description,
        price = price,
        place = place,
        beds = beds,
        baths = baths,
        area = area,
        type = type,
        amenities = amenities.joinToString(",")
    )
}
