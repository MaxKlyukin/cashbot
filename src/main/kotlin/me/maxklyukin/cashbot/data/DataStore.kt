package me.maxklyukin.cashbot.data

interface DataStore<T> {
    fun get(): T?
    fun set(data: T)
}