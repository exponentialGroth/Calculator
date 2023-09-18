package com.exponential_groth.calculator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.widget.LinearLayoutCompat
import com.exponential_groth.calculator.parser.Constant
import com.exponential_groth.calculator.parser.ConstantsType
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ConstantsBottomSheet(val onConstantSelected: (c: Constant) -> Unit): BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? = inflater.inflate(R.layout.constants_bottom_sheet_content, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val importantConstantsView: LinearLayoutCompat = view.findViewById(R.id.constants_sheet_important_ll)
        val particleConstantsView: LinearLayoutCompat = view.findViewById(R.id.constants_sheet_particles_ll)
        val quantumConstantsView: LinearLayoutCompat = view.findViewById(R.id.constants_sheet_quantum_ll)
        val otherConstantsView: LinearLayoutCompat = view.findViewById(R.id.constants_sheet_other_ll)
        val constantTitles = resources.getStringArray(R.array.constants)

        fun getViewGroup(type: ConstantsType) = when (type) {
            ConstantsType.IMPORTANT -> importantConstantsView
            ConstantsType.PARTICLE -> particleConstantsView
            ConstantsType.QUANTUM_MECHANICS -> quantumConstantsView
            ConstantsType.OTHER -> otherConstantsView
        }

        Constant.values().forEachIndexed { i, constant ->
            val button = (View.inflate(requireContext(), R.layout.text_button, null) as Button).also {
                it.text = constantTitles[i]
                it.setOnClickListener {
                    onConstantSelected(constant)
                    close()
                }
            }
            getViewGroup(constant.type).addView(button)
        }
    }

    private fun close() {
        val behavior = (dialog as BottomSheetDialog).behavior
        behavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    companion object {
        const val TAG = "Constants Bottom Sheet"
    }
}