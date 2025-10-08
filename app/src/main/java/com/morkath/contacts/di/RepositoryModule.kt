package com.morkath.contacts.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.morkath.contacts.data.local.database.dao.ContactDao
import com.morkath.contacts.domain.repository.ContactRepository
import com.morkath.contacts.data.repository.ContactRepositoryImpl
import com.morkath.contacts.data.repository.DeviceContactDataSourceImpl
import com.morkath.contacts.domain.repository.DeviceContactDataSource
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideContactRepository(
        contactDao: ContactDao
    ): ContactRepository = ContactRepositoryImpl(contactDao)

    @Provides
    @Singleton
    fun provideDeviceContactDataSource(
        @ApplicationContext context: Context
    ): DeviceContactDataSource = DeviceContactDataSourceImpl(context)
}