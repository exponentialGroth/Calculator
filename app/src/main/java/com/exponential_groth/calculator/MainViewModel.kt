package com.exponential_groth.calculator

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.exponential_groth.calculator.data.Repository
import com.exponential_groth.calculator.parser.AngleUnit
import com.exponential_groth.calculator.parser.Parser
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainViewModel(private val repository: Repository): ViewModel() {
    private val storage = mutableMapOf<String, Double>()
    val parsed = MutableLiveData<List<Double>>()
    val exception = MutableLiveData<Exception>()
    var job: Job? = null

    private val parser = Parser(AngleUnit.DEGREE)


    fun init() {
        viewModelScope.launch {
            storage.putAll(repository.getVariables())
            if (storage.isEmpty()) {
                for (i in listOf("A", "B", "C", "D", "E", "F", "X", "Y", "Z", "M", "Ans")) {
                    storage[i] = 0.0
                }
            }
        }
    }

    fun evaluate(input: String, multiValued: Boolean) {
        if (storage.isEmpty()) return
        job = viewModelScope.launch {
            val storageAction = input.takeLast(3).toStorageAction()
            try {
                if (multiValued) {  // if storageAction != null, then !multiValued (because there is another expression (">>Variable" or "M+-") after the otherwise multivalued function)
                    parsed.value = parser.parseMultiValuedExpr(input, storage).plus(
                        if (input.startsWith("Pol")) MULTIVALUED_POL else MULTIVALUED_REC
                    )
                } else {
                    parsed.value = listOf(parser.parse(input.takeIf { storageAction == null }?: input.dropLast(3), storage))
                }

                storageAction?.let {
                    storage[it.variable] =
                        it.act(storage[storageAction.variable]!!,
                            parsed.value!!.first())
                }
                storage["Ans"] = parsed.value!!.first()
            } catch (e: Exception) {
                exception.value = e
            }
        }
    }

    suspend fun store() {
        repository.addVariables(storage)
    }


    class MainViewModelFactory(private val repository: Repository): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
    }
}