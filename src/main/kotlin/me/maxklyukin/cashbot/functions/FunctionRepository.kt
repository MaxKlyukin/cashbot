package me.maxklyukin.cashbot.functions

class FunctionRepository {
    private var functions: MutableMap<String, Function> = HashMap()

    fun add(function: Function) {
        functions[function.name] = function
    }

    fun search(name: String): Function? {
        return functions[name]
    }

    fun getAll(): List<Function> {
        return functions.values.toList()
    }
}