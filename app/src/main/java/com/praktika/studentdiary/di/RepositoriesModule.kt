package com.praktika.studentdiary.di

import com.praktika.studentdiary.data.repository.AuthRepositoryImpl
import com.praktika.studentdiary.data.repository.DashboardRepositoryImpl
import com.praktika.studentdiary.data.repository.MaterialsRepositoryImpl
import com.praktika.studentdiary.data.repository.ScheduleRepositoryImpl
import com.praktika.studentdiary.data.repository.TestRepositoryImpl
import com.praktika.studentdiary.domain.repository.AuthRepository
import com.praktika.studentdiary.domain.repository.DashboardRepository
import com.praktika.studentdiary.domain.repository.MaterialsRepository
import com.praktika.studentdiary.domain.repository.ScheduleRepository
import com.praktika.studentdiary.domain.repository.TestRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoriesModule {
    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl,
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindScheduleRepository(
        authRepositoryImpl: ScheduleRepositoryImpl,
    ): ScheduleRepository

    @Binds
    @Singleton
    abstract fun bindDashboardRepository(
        dashboardRepositoryImpl: DashboardRepositoryImpl,
    ): DashboardRepository

    @Binds
    @Singleton
    abstract fun bindMaterialsRepository(
        dashboardRepositoryImpl: MaterialsRepositoryImpl,
    ): MaterialsRepository

    @Binds
    @Singleton
    abstract fun bindTestRepository(
        testRepositoryImpl: TestRepositoryImpl,
    ): TestRepository
}