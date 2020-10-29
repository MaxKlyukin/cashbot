package me.maxklyukin.cashbot.data

import kotlinx.serialization.KSerializer

interface DataStoreFactory {
    fun <T> make(name: String, serializer: KSerializer<T>): DataStore<T>
}