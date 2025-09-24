package com.morkath.contacts.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.morkath.contacts.data.local.database.dao.ContactDao
import com.morkath.contacts.domain.repository.ContactRepository
import com.morkath.contacts.data.repository.ContactRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideContactRepository(
        contactDao: ContactDao
    ): ContactRepository = ContactRepositoryImpl(contactDao)
}