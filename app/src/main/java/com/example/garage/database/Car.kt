package com.example.garage.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull

@Entity(tableName = "car")
data class Car(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    @NotNull
    val id: Long = 0,

    @ColumnInfo(name = "model")
    @NotNull
    var model: String,

    @ColumnInfo(name = "year")
    @NotNull
    val year: Int,

    @ColumnInfo(name = "brand")
    @NotNull
    val brand: String,

    @ColumnInfo(name = "logo")
    @NotNull
    val logo: String,

    @ColumnInfo(name = "image", typeAffinity = ColumnInfo.BLOB)
    val image: ByteArray?,

    @ColumnInfo(name = "displacement")
    @NotNull
    val displacement: Int,

    @ColumnInfo(name = "plate")
    @NotNull
    val plate: String,

    @ColumnInfo(name = "supply")
    @NotNull
    val supply: String,

    @ColumnInfo(name = "km")
    @NotNull
    val km: Double,
)
