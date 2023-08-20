package com.exponential_groth.calculator.data

class Repository private constructor(private val variablesDao: VariablesDao): IRepository {
    override suspend fun getVariables(): Map<String, Double> {
        return variablesDao.getVariables().associate { it.name to it.value }
    }

    override suspend fun addVariables(newVariables: Map<String, Double>) {
        variablesDao.addVariables(newVariables.entries.map { Variable(it.key, it.value) })
    }


    companion object {
        @Volatile private var instance: Repository? = null

        fun getInstance(variablesDao: VariablesDao) =
            instance ?: synchronized(this) {
                instance ?: Repository(variablesDao).also { instance = it }
            }
    }
}