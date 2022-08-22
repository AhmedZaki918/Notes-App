package com.notesapp.data.di

import android.content.Context
import androidx.room.Room
import com.notesapp.data.local.Constants.DATABASE_NAME
import com.notesapp.data.local.ToDoDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, ToDoDatabase::class.java, DATABASE_NAME)
            .build()


    @Provides
    @Singleton
    fun provideDao(db: ToDoDatabase) = db.toDoDao()
}