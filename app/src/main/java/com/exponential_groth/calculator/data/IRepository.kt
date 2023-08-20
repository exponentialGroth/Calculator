package com.exponential_groth.calculator.data

interface IRepository {
    suspend fun getVariables(): Map<String, Double>
    suspend fun addVariables(newVariables: Map<String, Double>)
}