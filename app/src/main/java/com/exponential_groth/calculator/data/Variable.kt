package com.exponential_groth.calculator.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Variable(
    @PrimaryKey val name: String,
    val value: Double,
)
