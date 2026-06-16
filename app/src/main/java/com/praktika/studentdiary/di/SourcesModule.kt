package com.praktika.studentdiary.di

import com.praktika.studentdiary.data.source.AuthSource
import com.praktika.studentdiary.data.source.AuthSourceImpl
import com.praktika.studentdiary.data.source.DashboardSource
import com.praktika.studentdiary.data.source.DashboardSourceImpl
import com.praktika.studentdiary.data.source.ScheduleSource
import com.praktika.studentdiary.data.source.ScheduleSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SourcesModule {
    @Binds
    @Singleton
    abstract fun bindAuthSource(
        authSourceImpl: AuthSourceImpl,
    ): AuthSource

    @Binds
    @Singleton
    abstract fun bindScheduleSource(
        authSourceImpl: ScheduleSourceImpl,
    ): ScheduleSource

    @Binds
    @Singleton
    abstract fun bindDashboardSource(
        dashboardSourceImpl: DashboardSourceImpl,
    ): DashboardSource
}