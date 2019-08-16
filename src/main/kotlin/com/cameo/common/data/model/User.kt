package com.cameo.common.data.model

data class User(val id: Int,
                val username: String,
                val passwordHash: String)