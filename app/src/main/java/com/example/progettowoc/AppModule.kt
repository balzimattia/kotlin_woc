package com.example.progettowoc

import android.content.Context
import com.example.progettowoc.auth.data.AuthRepository
import com.example.progettowoc.auth.data.AuthRepositoryInterface
import com.example.progettowoc.coaching.data.CoachingRelationRepositoryInterface
import com.example.progettowoc.coaching.data.CoachingRepository
import com.example.progettowoc.coaching.data.CoachingRequestRepositoryInterface
import com.example.progettowoc.notifications.data.NotificationPreferencesDataStore
import com.example.progettowoc.notifications.data.NotificationRepository
import com.example.progettowoc.notifications.data.NotificationsRepositoryInterface
import com.example.progettowoc.programs.data.ClienteProgramsRepositoryInterface
import com.example.progettowoc.programs.data.CoachProgramsRepositoryInterface
import com.example.progettowoc.programs.data.ProgramsRepository
import com.example.progettowoc.programs.data.ProgramsRepositoryInterface
import com.example.progettowoc.supabase.SupabaseClientImpl
import com.example.progettowoc.users.data.ClienteUserRepositoryInterface
import com.example.progettowoc.users.data.CoachUserRepositoryInterface
import com.example.progettowoc.users.data.SettingPreferencesDataStore
import com.example.progettowoc.users.data.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideSupabaseClient(): SupabaseClient {
        return SupabaseClientImpl.getSupabaseClient()
    }

    @Singleton
    @Provides
    fun provideAuthRepository(
        supabase: SupabaseClient,
        @ApplicationContext context: Context
    ): AuthRepositoryInterface {
        return AuthRepository(supabase = supabase, context = context)
    }

    @Singleton
    @Provides
    fun provideCoachingRepository(supabase: SupabaseClient): CoachingRepository {
        return CoachingRepository(supabase = supabase)
    }


    @Singleton
    @Provides
    fun provideCoachingRequestRepository(coachingRepository: CoachingRepository): CoachingRequestRepositoryInterface = coachingRepository


    @Singleton
    @Provides
    fun provideCoachingRelationRepository(coachingRepository: CoachingRepository): CoachingRelationRepositoryInterface = coachingRepository


    @Singleton
    @Provides
    fun provideUserRepository(supabase: SupabaseClient): UserRepository {
        return UserRepository(supabase = supabase)
    }


    @Singleton
    @Provides
    fun provideClienteUserRepository(userRepository: UserRepository): ClienteUserRepositoryInterface = userRepository


    @Singleton
    @Provides
    fun provideCoachUserRepository(userRepository: UserRepository): CoachUserRepositoryInterface = userRepository


    @Singleton
    @Provides
    fun notificationRepository(
        supabase: SupabaseClient
    ): NotificationsRepositoryInterface {
        return NotificationRepository(supabase = supabase)
    }


    @Provides
    @Singleton
    fun provideNotificationPreferencesDataStore(
        @ApplicationContext context: Context
    ): NotificationPreferencesDataStore {
        return NotificationPreferencesDataStore(context)
    }


    @Provides
    @Singleton
    fun provideSettingPreferencesDataStore(
        @ApplicationContext context: Context
    ): SettingPreferencesDataStore {
        return SettingPreferencesDataStore(context)
    }


    @Singleton
    @Provides
    fun provideProgramsRepository(supabase: SupabaseClient): ProgramsRepository {
        return ProgramsRepository(supabase = supabase)
    }


    @Singleton
    @Provides
    fun provideProgramsRepositoryInterface(programsRepository: ProgramsRepository): ProgramsRepositoryInterface = programsRepository


    @Singleton
    @Provides
    fun provideClienteProgramsRepository(programsRepository: ProgramsRepository): ClienteProgramsRepositoryInterface = programsRepository


    @Singleton
    @Provides
    fun provideCoachProgramsRepository(programsRepository: ProgramsRepository): CoachProgramsRepositoryInterface = programsRepository
}