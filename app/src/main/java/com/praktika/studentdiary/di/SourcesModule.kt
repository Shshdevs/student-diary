package com.praktika.studentdiary.di

import com.praktika.studentdiary.data.source.AiSource
import com.praktika.studentdiary.data.source.AiSourceImpl
import com.praktika.studentdiary.data.source.AuthSource
import com.praktika.studentdiary.data.source.AuthSourceImpl
import com.praktika.studentdiary.data.source.DashboardSource
import com.praktika.studentdiary.data.source.DashboardSourceImpl
import com.praktika.studentdiary.data.source.MaterialsSource
import com.praktika.studentdiary.data.source.MaterialsSourceImpl
import com.praktika.studentdiary.data.source.PdfParserSource
import com.praktika.studentdiary.data.source.PdfParserSourceImpl
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

    @Binds
    @Singleton
    abstract fun bindAiSource(
        aiSourceImpl: AiSourceImpl,
    ): AiSource

    @Binds
    @Singleton
    abstract fun bindMaterialsSource(
        materialsSourceImpl: MaterialsSourceImpl,
    ): MaterialsSource

    @Binds
    @Singleton
    abstract fun bindPdfParseSource(
        pdfParserSource: PdfParserSourceImpl,
    ): PdfParserSource
}