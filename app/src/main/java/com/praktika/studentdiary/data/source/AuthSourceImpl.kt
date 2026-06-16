package com.praktika.studentdiary.data.source

import io.github.jan.supabase.annotations.SupabaseInternal
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import javax.inject.Inject

class AuthSourceImpl @Inject constructor(
    private val auth: Auth,
) : AuthSource {
    override val sessionState: Flow<SessionStatus> = auth.sessionStatus

    @OptIn(SupabaseInternal::class)
    override suspend fun signUp(
        username: String,
        email: String,
        password: String,
    ): Result<Unit> = try {

        auth.signUpWith(Email) {
            this.email = email
            this.password = password
            this.data = buildJsonObject {
                put("username", username)
            }
        }
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun signIn(email: String, password: String): Result<Unit> = try {
        auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}