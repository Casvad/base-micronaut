package com.carlos.utils

object FileManagerUtil {

    private val files = mutableMapOf<String, String>()

    fun getQuery(name: String): String {

        return this.getFile("/queries/", name)
    }

    fun getFile(path: String, name: String): String {

        return this.files[name] ?: kotlin.run {
            val resource = FileManagerUtil::class.java.getResource("$path$name")?.readText() ?: ""
            this.files[name] = resource
            resource
        }
    }
}