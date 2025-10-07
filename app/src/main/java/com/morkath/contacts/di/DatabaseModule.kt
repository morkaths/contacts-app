package com.morkath.contacts.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.morkath.contacts.data.local.database.AppDatabase
import com.morkath.contacts.data.local.database.dao.ContactDao

@Module // Đánh dấu lớp này là một module của Dagger Hilt
@InstallIn(SingletonComponent::class) // Tạo một instance duy nhất trong toàn bộ ứng dụng
internal object DatabaseModule {
    @Provides // Hàm này sẽ cung cấp một instance của AppDatabase
    @Singleton // Đảm bảo rằng chỉ có một instance của AppDatabase được tạo ra trong toàn bộ ứng dụng
    fun providesDatabase(
        @ApplicationContext context: Context,
    ): AppDatabase = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app.db"
    )
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    fun providesContactDao(
        database: AppDatabase
    ): ContactDao = database.contactDao()
}