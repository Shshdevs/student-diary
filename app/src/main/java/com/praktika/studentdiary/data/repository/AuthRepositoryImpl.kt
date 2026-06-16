package com.praktika.studentdiary.data.repository

import com.praktika.studentdiary.data.source.AuthSource
import com.praktika.studentdiary.domain.repository.AuthRepository
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val source: AuthSource,
) : AuthRepository {
    override val sessionState: Flow<SessionStatus>
        get() = source.sessionState

    override suspend fun signUp(
        username: String,
        email: String,
        password: String,
    ): Result<Unit> {
        return source.signUp(username, email, password)
    }

    override suspend fun signIn(
        email: String,
        password: String,
    ): Result<Unit> {
        return source.signIn(email, password)
    }
}