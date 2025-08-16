package com.example.xpense.di

import android.content.Context
import androidx.room.Room
import com.example.xpense.data.local.AppDatabase
import com.example.xpense.data.repository.ExpenseRepository
import com.example.xpense.data.repository.ExpenseRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides 
    @Singleton
    fun provideDb(@ApplicationContext ctx: Context): AppDatabase =
        Room.databaseBuilder(ctx, AppDatabase::class.java, "expenses.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides 
    @Singleton
    fun provideExpenseDao(db: AppDatabase) = db.expenseDao()

    @Provides 
    @Singleton
    fun provideExpenseRepository(db: AppDatabase): ExpenseRepository =
        ExpenseRepositoryImpl(db.expenseDao())
        
}