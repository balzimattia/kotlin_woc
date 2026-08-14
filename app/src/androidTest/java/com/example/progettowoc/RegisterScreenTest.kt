package com.example.progettowoc

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.rule.GrantPermissionRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test


@HiltAndroidTest
class RegisterScreenTest {

    private val homeTag = "home_screen"


    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val permissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(android.Manifest.permission.POST_NOTIFICATIONS)

    @get:Rule(order = 2)
    val composeRule = createAndroidComposeRule<MainActivity>()


    @Before
    fun setup() {
        hiltRule.inject()
    }


    @OptIn(ExperimentalTestApi::class)
    @Test
    fun whenRegisterValidCredentials_thenNavigateToHome() {
        val casualEmail = "test_${System.currentTimeMillis()}@example.it"

        composeRule.waitUntilAtLeastOneExists(hasTestTag(homeTag))
        composeRule.onNodeWithTag("registrati").performClick()

        composeRule.onNodeWithTag("Nome e cognome").performTextInput("test cancella")
        composeRule.onNodeWithTag("Email").performTextInput(casualEmail)
        composeRule.onNodeWithTag("Ruolo").performClick()
        composeRule.onNodeWithTag("Ruolo_Cliente").performClick()
        composeRule.onNodeWithTag("Password").performTextInput("provatest")
        composeRule.onNodeWithTag("Conferma password").performTextInput("provatest")

        composeRule.onNodeWithTag("Registrati_button").performClick()

        composeRule.waitUntilAtLeastOneExists(hasTestTag(homeTag), timeoutMillis = 10000)

        composeRule.onNodeWithTag(homeTag).assertIsDisplayed()
    }
}