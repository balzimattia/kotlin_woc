package com.example.progettowoc.ui.navigation

import com.example.progettowoc.programs.data.ProgramSheet
import com.example.progettowoc.users.data.User
import kotlinx.serialization.Serializable


@Serializable
object AuthRoutes {
    @Serializable
    object LoginRoute

    @Serializable
    object RegisterRoute
}


@Serializable
object ProgramRoutes {
    @Serializable
    object ProgramsRoute

    @Serializable
    object ProgramDayRoute

    @Serializable
    data class ProgramExerciseRoute(
        val exerciseIndex: Int
    )
}


@Serializable
object UserRoutes {
    @Serializable
    object UserRoute

    @Serializable
    object SettingsRoute
}


@Serializable
object MainRoutes {
    @Serializable
    object HomeRoute

    @Serializable
    object NotificationsRoute
}


@Serializable
object CoachingRequestRoutes {
    @Serializable
    object SearchCoachRoute

    @Serializable
    data class CoachingRequestRoute(
        val coach: User
    )

    @Serializable
    data class ClienteRequestRoute(
        val cliente: User
    )

    @Serializable
    object RequestsListRoute
}


@Serializable
object CoachingRelationRoutes {
    @Serializable
    object CoachClientiListRoute

    @Serializable
    data class CoachCLienteInfoRoute(
        val cliente: User
    )

    @Serializable
    data class EditProgramRoute(
        val clienteId: String,
        val program: ProgramSheet?
    )

    @Serializable
    object OwnCoachRelationRoute
}



// liste eventuali
val rootScreens = listOf(
    MainRoutes.HomeRoute::class,
    ProgramRoutes.ProgramsRoute::class,
    UserRoutes.UserRoute::class,
    CoachingRelationRoutes.CoachClientiListRoute::class
)

val noScaffoldScreens = listOf(
    AuthRoutes.LoginRoute::class,
    AuthRoutes.RegisterRoute::class
)

val onlyTopBar = listOf(
    CoachingRelationRoutes.EditProgramRoute::class,
    ProgramRoutes.ProgramExerciseRoute::class
)