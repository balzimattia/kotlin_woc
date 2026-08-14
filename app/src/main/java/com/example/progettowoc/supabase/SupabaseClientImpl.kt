package com.example.progettowoc.supabase


import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
object SupabaseClientImpl {
    private val supabase = createSupabaseClient(
        supabaseUrl = "https://tunfngmgyczbsiivzynk.supabase.co",
        supabaseKey = "sb_publishable_s9g8EqpZ5ayr98lJFZivRQ_IcVslvnl"
    ) {
        install(Postgrest)
        install(Auth)
    }

    fun getSupabaseClient(): SupabaseClient {
        return supabase
    }
}