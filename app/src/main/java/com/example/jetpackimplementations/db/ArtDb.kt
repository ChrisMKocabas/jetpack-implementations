package com.example.jetpackimplementations.db


import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.jetpackimplementations.model.Art

@Database(entities = [Art::class], version = 2)
abstract class ArtDatabase : RoomDatabase() {
    abstract fun artDao(): ArtDao
}
