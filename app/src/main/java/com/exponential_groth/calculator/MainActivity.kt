package com.exponential_groth.calculator

import android.content.*
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.view.View.OnLongClickListener
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import com.exponential_groth.calculator.editor.Editor
import com.exponential_groth.calculator.editor.functionality.Function
import com.exponential_groth.calculator.editor.functionality.MathExpression
import com.exponential_groth.calculator.editor.functionality.Operator
import com.exponential_groth.calculator.editor.functionality.SingleSymbolExpression
import com.exponential_groth.calculator.parser.AngleUnit
import com.exponential_groth.calculator.parser.IllegalExpressionException
import com.exponential_groth.calculator.parser.MathException
import com.exponential_groth.calculator.parser.MathExceptionType
import com.exponential_groth.calculator.result.OutputType
import com.exponential_groth.calculator.result.Result
import com.exponential_groth.calculator.result.ResultManager
import com.exponential_groth.mathview.MathView
import kotlinx.coroutines.launch
import java.math.RoundingMode

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel

    private var storageIsClicked = false

    private val editor = Editor()
    private val resultManager = ResultManager()

    private var colorBackgroundStart = -1
    private var colorBackgroundEnd = -1
    private var orientationBackgroundGradient = ""

    private var colorUpperButtons = -1
    private var colorUpperButtonsStroke = -1
    private var widthUpperButtonsStroke = 1
    private var cornerRadiusUpperButtonsTop = -1
    private var cornerRadiusUpperButtonsBottom = -1

    private var colorLowerButtons = -1
    private var colorLowerButtonsStroke = -1
    private var widthLowerButtonsStroke = 2

    private var cornerRadiusLowerButtonsTop = -1
    private var cornerRadiusLowerButtonsBottom = -1

    private var colorDeletingButtons = -1
    private var colorDeletingButtonsStroke = -1
    private var widthDeletingButtonsStroke = 1

    private var colorNavigationButton = -1
    private var colorNavigationButtonStroke = -1
    private var widthNavigationButtonStroke = 0
    private var cornerRadiusNavigationButton = 85

    private var colorDisplay = -1
    private var colorDisplayStroke = -1
    private var widthDisplayStroke = 1

    private var textColorUpperButtons = -1
    private var textColorLowerButtons = -1
    private var textColorDeletingButtons = -1
    private var textColorSecondaryFunctionality = -1
    private var textColorVariables = -1


    private lateinit var calculationMV: MathView
    private lateinit var resultMV: MathView
    private lateinit var calculatorDisplay: TextView
    private lateinit var displayContainer: LinearLayout

    private lateinit var btn1: Button
    private lateinit var btn2: Button
    private lateinit var btn3: Button
    private lateinit var btn4: Button
    private lateinit var btn5: Button
    private lateinit var btn6: Button
    private lateinit var btn7: Button
    private lateinit var btn8: Button
    private lateinit var btn9: Button
    private lateinit var btn10: ImageButton
    private lateinit var btn11: Button
    private lateinit var btn12: Button
    private lateinit var btn13: Button
    private lateinit var btn14: Button
    private lateinit var btn15: Button
    private lateinit var btn16: Button
    private lateinit var btn17: Button
    private lateinit var btn18: Button
    private lateinit var btn19: Button
    private lateinit var btn20: Button
    private lateinit var btn21: Button
    private lateinit var btn22: ImageButton
    private lateinit var btn23: ImageButton
    private lateinit var btn24: ImageButton
    private lateinit var btn25: ImageButton
    private lateinit var btn26: ImageButton
    private lateinit var btn27: ImageButton
    private lateinit var btn28: ImageButton
    private lateinit var btn29: ImageButton
    private lateinit var btn30: ImageButton
    private lateinit var btn31: Button
    private lateinit var btn32: Button
    private lateinit var btn33: Button
    private lateinit var btn34: Button
    private lateinit var btn35: Button
    private lateinit var btn36: Button
    private lateinit var btn37: Button
    private lateinit var btn38: Button
    private lateinit var btn39: Button
    private lateinit var btn40: Button
    private lateinit var btn41: Button
    private lateinit var btn42: Button
    private lateinit var btnLeft: Button
    private lateinit var btnDown: Button
    private lateinit var btnRight: Button
    private lateinit var btnUp: Button

    private lateinit var navigationField:View

    private lateinit var imageView5: ImageView
    private lateinit var imageView6: ImageView
    private lateinit var imageView10: ImageView
    private lateinit var imageView25: ImageView
    private lateinit var imageView26: ImageView
    private lateinit var imageView27: ImageView
    private lateinit var imageView28: ImageView
    private lateinit var imageView29: ImageView
    private lateinit var imageView30: ImageView
    private lateinit var imageView34: ImageView
    private lateinit var imageView35: ImageView
    private lateinit var imageView36: ImageView
    private lateinit var imageView41: ImageView

    private lateinit var tV2: TextView
    private lateinit var tV3: TextView
    private lateinit var tV4: TextView
    private lateinit var tV7: TextView
    private lateinit var tV8: TextView
    private lateinit var tV16: TextView
    private lateinit var tV32: TextView
    private lateinit var tV38: TextView
    private lateinit var tV39: TextView
    private lateinit var tV40: TextView
    private lateinit var tvMMinus: TextView

    private lateinit var tVA: TextView
    private lateinit var tVB: TextView
    private lateinit var tVC: TextView
    private lateinit var tVD: TextView
    private lateinit var tVE: TextView
    private lateinit var tVF: TextView
    private lateinit var tVX: TextView
    private lateinit var tVY: TextView
    private lateinit var tVZ: TextView
    private lateinit var tVM: TextView

    private val singleTokenButtons = mapOf(
        R.id.button16 to false to SingleSymbolExpression._0,
        R.id.button17 to false to SingleSymbolExpression._1,
        R.id.button14 to false to SingleSymbolExpression._2,
        R.id.button11 to false to SingleSymbolExpression._3,
        R.id.button18 to false to SingleSymbolExpression._4,
        R.id.button15 to false to SingleSymbolExpression._5,
        R.id.button12 to false to SingleSymbolExpression._6,
        R.id.button1 to false to SingleSymbolExpression._7,
        R.id.button20 to false to SingleSymbolExpression._8,
        R.id.button19 to false to SingleSymbolExpression._9,
        R.id.button13 to false to SingleSymbolExpression.POINT,
        R.id.button31 to false to SingleSymbolExpression.UNARY_MINUS,
        R.id.button39 to false to SingleSymbolExpression.LEFT_BRACKET,
        R.id.button40 to false to SingleSymbolExpression.RIGHT_BRACKET,
        R.id.button16 to true to SingleSymbolExpression.PI,
        R.id.button24 to true to SingleSymbolExpression.E,
        )

    private val recallButtons = mapOf(
        R.id.button17 to true to "X",
        R.id.button14 to true to "Y",
        R.id.button11 to true to "Z",
        R.id.button18 to true to "D",
        R.id.button15 to true to "E",
        R.id.button12 to true to "F",
        R.id.button1 to true to "A",
        R.id.button20 to true to "B",
        R.id.button19 to true to "C",
        R.id.button37 to true to "M",
        R.id.button6 to false to "Ans",
    )
    private val storageButtons = mapOf(
        R.id.button17 to false to "X",
        R.id.button14 to false to "Y",
        R.id.button11 to false to "Z",
        R.id.button18 to false to "D",
        R.id.button15 to false to "E",
        R.id.button12 to false to "F",
        R.id.button1 to false to "A",
        R.id.button20 to false to "B",
        R.id.button19 to false to "C",
        R.id.button37 to true to "M",
    )

    private val otherExpressionButtons = mapOf<Pair<Int, Boolean>, MathExpression>(
        R.id.button13 to true to Operator.COMMA,
        R.id.button3 to false to Operator.MINUS,
        R.id.button4 to false to Operator.DIVISION,
        R.id.button4 to true to Operator.COMB,
        R.id.button7 to false to Operator.PLUS,
        R.id.button8 to false to Operator.MULTIPLICATION,
        R.id.button8 to true to Operator.PERM,
        R.id.button25 to false to Operator.FRACTION,
        R.id.button25 to true to Operator.MIXED_FRACTION,
        R.id.button32 to false to Operator.FACTORIAL,
        R.id.button40 to true to Operator.PERCENT,
        R.id.button28 to false to Operator.EXPONENTIATION,
        R.id.button23 to false to Operator.EXPONENTIATION__1,
        R.id.button27 to false to Operator.EXPONENTIATION_2,
        R.id.button22 to false to Operator.EXPONENTIATION_3,
        R.id.button29 to true to Operator.EXPONENTIATION_10,
        R.id.button30 to true to Operator.EXPONENTIATION_E,
        R.id.button10 to true to Operator.PERIOD,
        R.id.button26 to false to Function.SQRT,
        R.id.button22 to true to Function.CBRT,
        R.id.button28 to true to Function.RT,
        R.id.button24 to false to Function.LOGXY,
        R.id.button29 to false to Function.LOG,
        R.id.button30 to false to Function.LN,
        R.id.button7 to true to Function.POL,
        R.id.button3 to true to Function.REC,
        R.id.button21 to false to Function.ABS,
        R.id.button34 to false to Function.SIN,
        R.id.button34 to true to Function.ASIN,
        R.id.button35 to false to Function.COS,
        R.id.button35 to true to Function.ACOS,
        R.id.button36 to false to Function.TAN,
        R.id.button36 to true to Function.ATAN,
    )


    private lateinit var sharedPreferences: SharedPreferences
    private val onSharedPreferencesChangeListener =
        OnSharedPreferenceChangeListener { p0, p1 ->
            adaptToPreferences() // TODO("update only the variables influenced by p0")
        }


    private fun initViewModel() {
        val factory = Injector.provideMainViewModelFactory(this)
        viewModel = ViewModelProvider(this, factory) [MainViewModel::class.java]
        viewModel.init()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViewModel()

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        sharedPreferences.registerOnSharedPreferenceChangeListener(onSharedPreferencesChangeListener)

        calculationMV = findViewById(R.id.katexMathView)
        displayContainer = findViewById(R.id.myMVContainer)
        calculatorDisplay = findViewById(R.id.TVCalculation)
        resultMV = findViewById(R.id.MVResult)

        btn1 = findViewById(R.id.button1)
        btn2 = findViewById(R.id.button2)
        btn3 = findViewById(R.id.button3)
        btn4 = findViewById(R.id.button4)
        btn5 = findViewById(R.id.button5)
        btn6 = findViewById(R.id.button6)
        btn7 = findViewById(R.id.button7)
        btn8 = findViewById(R.id.button8)
        btn9 = findViewById(R.id.button9)
        btn10 = findViewById(R.id.button10)
        btn11 = findViewById(R.id.button11)
        btn12 = findViewById(R.id.button12)
        btn13 = findViewById(R.id.button13)
        btn14 = findViewById(R.id.button14)
        btn15 = findViewById(R.id.button15)
        btn16 = findViewById(R.id.button16)
        btn17 = findViewById(R.id.button17)
        btn18 = findViewById(R.id.button18)
        btn19 = findViewById(R.id.button19)
        btn20 = findViewById(R.id.button20)
        btn21 = findViewById(R.id.button21)
        btn22 = findViewById(R.id.button22)
        btn23 = findViewById(R.id.button23)
        btn24 = findViewById(R.id.button24)
        btn25 = findViewById(R.id.button25)
        btn26 = findViewById(R.id.button26)
        btn27 = findViewById(R.id.button27)
        btn28 = findViewById(R.id.button28)
        btn29 = findViewById(R.id.button29)
        btn30 = findViewById(R.id.button30)
        btn31 = findViewById(R.id.button31)
        btn32 = findViewById(R.id.button32)
        btn33 = findViewById(R.id.button33)
        btn34 = findViewById(R.id.button34)
        btn35 = findViewById(R.id.button35)
        btn36 = findViewById(R.id.button36)
        btn37 = findViewById(R.id.button37)
        btn38 = findViewById(R.id.button38)
        btn39 = findViewById(R.id.button39)
        btn40 = findViewById(R.id.button40)
        btn41 = findViewById(R.id.button41)
        btn42 = findViewById(R.id.button42)
        btnLeft = findViewById(R.id.buttonBlueLeft)
        btnDown = findViewById(R.id.buttonBlueBottom)
        btnRight = findViewById(R.id.buttonBlueRight)
        btnUp = findViewById(R.id.buttonBlueTop)

        navigationField = findViewById(R.id.navigationField)

        imageView5 = findViewById(R.id.imageView5)
        imageView6 = findViewById(R.id.imageView6)
        imageView10 = findViewById(R.id.imageView10)
        imageView25 = findViewById(R.id.imageView25)
        imageView26 = findViewById(R.id.imageView26)
        imageView27 = findViewById(R.id.imageView27)
        imageView28 = findViewById(R.id.imageView28)
        imageView29 = findViewById(R.id.imageView29)
        imageView30 = findViewById(R.id.imageView30)
        imageView34 = findViewById(R.id.imageView34)
        imageView35 = findViewById(R.id.imageView35)
        imageView36 = findViewById(R.id.imageView36)
        imageView41 = findViewById(R.id.imageView41)

        tV2 = findViewById(R.id.tV2)
        tV3 = findViewById(R.id.tV3)
        tV4 = findViewById(R.id.tV4)
        tV7 = findViewById(R.id.tV7)
        tV8 = findViewById(R.id.tV8)
        tV16 = findViewById(R.id.tV16)
        tV32 = findViewById(R.id.tV32)
        tV38 = findViewById(R.id.tV38)
        tV39 = findViewById(R.id.tV39)
        tV40 = findViewById(R.id.tV40)
        tvMMinus = findViewById(R.id.tv_m_minus)

        tVA = findViewById(R.id.tVA)
        tVB = findViewById(R.id.tVB)
        tVC = findViewById(R.id.tVC)
        tVD = findViewById(R.id.tVD)
        tVE = findViewById(R.id.tVE)
        tVF = findViewById(R.id.tVF)
        tVX = findViewById(R.id.tVX)
        tVY = findViewById(R.id.tVY)
        tVZ = findViewById(R.id.tVZ)
        tVM = findViewById(R.id.tVM)

        adaptToPreferences()
        initObservers()

        listOf(
            btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9,
            btn10, btn11, btn12, btn13, btn14, btn15, btn16, btn17, btn18, btn19,
            btn20, btn21, btn22, btn23, btn24, btn25, btn26, btn27, btn28, btn29,
            btn30, btn31, btn32, btn33, btn34, btn35, btn36, btn37, btn38, btn39,
            btn40, btn41, btn42, btnLeft, btnRight, btnUp, btnDown
        ).forEach {
            it.setOnClickListener(onBtnClickListener)
            it.setOnLongClickListener(onBtnLongClickListener)
        }

        editor.onClear = {
            resultManager.hide()
            setResultText()
        }
    }

    override fun onPause() {
        super.onPause()
        lifecycleScope.launch {
            viewModel.store()
        }
    }


    private fun onBtnClick(v: View, isLongClick: Boolean) {
        val id = v.id to isLongClick

        if (viewModel.job?.isActive == true) {
            when (id) {
                R.id.button5 to false, R.id.button9 to false -> {
                    viewModel.job!!.cancel()
                    return
                }
                else -> {
                    Toast.makeText(this, getString(R.string.still_calculating), Toast.LENGTH_SHORT).show()
                }
            }
        }

        if (storageIsClicked) {
            storageButtons[id]?.let {
                store(it)
            }
            storageIsClicked = false
            btn37.setTextColor(textColorUpperButtons)
            return
        }

        val updateDisplay = singleTokenButtons[id]?.let { editor.add(it) }
            ?: recallButtons[id]?.let { editor.add(it) }
            ?: otherExpressionButtons[id]?.let { editor.add(it) }
            ?: when (id) {
                R.id.buttonBlueLeft to false -> moveLeft()
                R.id.buttonBlueRight to false -> moveRight()
                R.id.buttonBlueTop to false -> moveUp()
                R.id.buttonBlueBottom to false -> moveDown()
                btn37.id to false -> {
                    storageIsClicked = true
                    btn37.setTextColor(getColor(R.color.greenOnClick))
                    false
                }
                R.id.button2 to true -> startSettingsActivity()
                R.id.button2 to false -> {
                    viewModel.evaluate(editor.getParsableInput(), editor.hasMultivaluedExpression)
                    false
                }
                R.id.button9 to false -> editor.del()
                R.id.button5 to false -> {
                    resultManager.hide(); setResultText()
                    editor.ac()
                }
                R.id.button5 to true -> {
                    share()
                    false
                }
                R.id.button6 to true -> startExponentialActivity()
                R.id.button10 to false -> {
                    editor.add(Operator.MULTIPLICATION)
                    editor.add(Operator.EXPONENTIATION_10)
                }
                R.id.button32 to true -> primeFactorization()
                R.id.button33 to false -> switchHyperbolic()
                R.id.button38 to false -> engineeringNotation(false)
                R.id.button38 to true -> engineeringNotation(true)
                R.id.button41 to false -> switchSAndD()
                R.id.button41 to true -> switchFractionType()
                R.id.button42 to true -> subtractFromM()
                R.id.button42 to false -> addToM()
                else -> false
            }
        if (updateDisplay) setCalculationText()
    }

    private val onBtnClickListener = OnClickListener {
        onBtnClick(it, false)
    }
    private val onBtnLongClickListener = OnLongClickListener {
        onBtnClick(it, true)
        true
    }


    private fun share() {
        if (resultManager.isHidden && editor.isEmpty) {
            Toast.makeText(this, getString(R.string.nothing_to_share), Toast.LENGTH_SHORT).show()
            return
        }
        val toShare = if (resultManager.isHidden)
            editor.getParsableInput()
                .filterNot { it == ' ' }
                .replace('{', '(')
                .replace('}', ')')
         else
            resultManager.stringToShare

        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, toShare)
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    private fun store(variable: String) {
        if (!storageIsClicked) return
        if (!resultManager.isHidden) {
            editor.ac()
            editor.add("Ans")
            editor.store(variable)
            setCalculationText()
        } else {
            editor.store(variable)
        }
        viewModel.evaluate(editor.getParsableInput(), editor.hasMultivaluedExpression)
    }


    private fun switchHyperbolic(): Boolean {
        btn33.text = if (editor.hyperbolic) "hyp" else "tri"
        editor.hyperbolic = !editor.hyperbolic
        return false
    }

    private fun switchSAndD(): Boolean {
        if (resultManager.isHidden) {
            Toast.makeText(this, getString(R.string.nothing_to_convert), Toast.LENGTH_SHORT)
                .show()
            return false
        }
        resultManager.switchSAndD()
        setResultText()
        return false
    }

    private fun switchFractionType(): Boolean {
        if (resultManager.isHidden) {
            Toast.makeText(this, getString(R.string.nothing_to_convert), Toast.LENGTH_SHORT)
                .show()
            return false
        }
        resultManager.switchFractionType()
        setResultText()
        return false
    }

    private fun primeFactorization(): Boolean {
        if (resultManager.isHidden) {
            Toast.makeText(this, getString(R.string.nothing_to_convert), Toast.LENGTH_SHORT).show()
        }
        try {
            resultManager.formatResult(OutputType.FACTORIZATION)
            setResultText()
        } catch (e: Result.Companion.FormattingException) {
            Toast.makeText(this, getString(R.string.no_factorization), Toast.LENGTH_SHORT).show()
        }
        return false
    }

    private fun engineeringNotation(direction: Boolean): Boolean {
        if (resultManager.isHidden) {
            Toast.makeText(this, getString(R.string.no_conversion_eng), Toast.LENGTH_SHORT).show()
            return false
        }
        if (resultManager.outputType != OutputType.ENGINEERING) {
            resultManager.formatResult(OutputType.ENGINEERING)
        } else {
            resultManager.shiftEngNotation(direction)
        }
        setResultText()
        return false
    }

    private fun subtractFromM(): Boolean {
        return if (editor.editM(false)) {
            viewModel.evaluate(editor.getParsableInput(), editor.hasMultivaluedExpression)
            true
        } else false
    }

    private fun addToM(): Boolean {
        return if (editor.editM(true)) {
            viewModel.evaluate(editor.getParsableInput(), editor.hasMultivaluedExpression)
            true
        } else false
    }

    private fun moveLeft(): Boolean {
        if (!resultManager.isHidden) {
            resultManager.hide()
            setResultText()
        }
        return editor.moveLeft()
    }
    private fun moveRight(): Boolean {
        if (!resultManager.isHidden) {
            resultManager.hide()
            setResultText()
        }
        return editor.moveRight()
    }
    private fun moveUp(): Boolean {
        if (!editor.hasEvaluated) return false
        resultManager.moveBack()
        setResultText()
        return editor.moveUp()
    }
    private fun moveDown(): Boolean {
        if (!editor.hasEvaluated) return false
        resultManager.moveForward()
        setResultText()
        return editor.moveDown()
    }


    private fun startSettingsActivity(): Boolean {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
        return false
    }

    private fun startExponentialActivity(): Boolean {
        val intent = Intent(this, ExpActivity::class.java)
        startActivity(intent)
        return false
    }


    private fun initObservers() {
        viewModel.parsed.observe(this) {
            if (it.size == 1) {
                resultManager.addResult(it.first())
            } else {
                resultManager.addResult(
                    outputType = getOutputTypeForMultivalued(it.last()),
                    numbers = it.dropLast(1)
                )
            }
            editor.hasEvaluated = true
            setCalculationText()
            setResultText()
        }

        viewModel.exception.observe(this) {
            Toast.makeText(
                this,
                getMsgForUser(it),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    private fun getOutputTypeForMultivalued(d: Double) = when (d) {
        MULTIVALUED_REC -> OutputType.CARTESIAN
        MULTIVALUED_POL -> OutputType.POLAR
        else -> throw Error("Unknown Encoding for multivalued output type")
    }
    private fun getMsgForUser(e: Exception) = when (e) {
        is IllegalExpressionException -> getString(R.string.illegal_expression_exception_msg)
        is MathException -> e.message?.let{
            getStringFromMathExceptionType(MathExceptionType.valueOf(it))
        }?: getString(R.string.unknown_error_msg)
        else -> getString(R.string.unknown_error_msg)
    }
    private fun getStringFromMathExceptionType(type: MathExceptionType) = when (type) {
        MathExceptionType.INFINITY -> getString(R.string.infinity_exception)
        else -> getString(R.string.domain_exception, type.toString().substring(7))
    }


    private fun setCalculationText() {
        val newText = "\\( ${editor.getTexInput(!editor.hasEvaluated)} \\)"
        if (calculationMV.text != newText) calculationMV.text = newText
    }
    private fun setResultText() {
        val newText = "\\( ${resultManager.stringRepresentation} \\)"
        if (resultMV.text != newText) resultMV.text = newText
    }


    private fun adaptToPreferences() {
        viewModel.parser.angleUnit = when (sharedPreferences.getString(getString(R.string.angle_key), "deg")) {
            "rad" -> AngleUnit.RADIAN
            "gon" -> AngleUnit.GRADIAN
            else -> AngleUnit.DEGREE
        }
        resultManager.defaultOutputType = OutputType.valueOf(sharedPreferences.getString(getString(R.string.display_result_key), "decimal")?.uppercase()?:"DECIMAL")
        resultManager.roundingMode = if (sharedPreferences.getBoolean(getString(R.string.round_result_key), true)) RoundingMode.HALF_UP else RoundingMode.DOWN
        resultManager.maxDecPlaces = sharedPreferences.getInt(getString(R.string.max_decimal_places_key), 15)

        adaptUIToPreferences()
    }

    private fun adaptUIToPreferences() {
        val upperImageButtons = listOf(btn22, btn23, btn24, btn25, btn26, btn27, btn28, btn29, btn30)
        val upperButtons = listOf(btn21, btn31, btn32, btn33, btn34, btn35, btn36, btn37, btn38, btn39, btn40, btn41, btn42)
        val lowerImageButtons = listOf(btn10)
        val lowerButtons = listOf(btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn11, btn12, btn13, btn14, btn15, btn16, btn17, btn18, btn19, btn20)
        val deletingButtons = listOf(btn5, btn9)
        val secFunctionalityIVs = listOf(imageView5, imageView6, imageView10, imageView25, imageView26, imageView27, imageView28, imageView29, imageView30, imageView34, imageView35, imageView36, imageView41)
        val secFunctionalityTVs = listOf(tV2, tV3, tV4, tV7, tV8, tV16, tV32, tV38, tV39, tV40, tvMMinus)
        val variableTVs = listOf(tVA, tVB, tVC, tVD, tVE, tVF, tVX, tVY, tVZ, tVM)

        //        background
        colorBackgroundStart = sharedPreferences.getInt(getString(R.string.color_start_background_key), getColor(R.color.startingColorBackground))
        colorBackgroundEnd = sharedPreferences.getInt(getString(R.string.color_end_background_key), getColor(R.color.endingColorBackground))
        orientationBackgroundGradient = sharedPreferences.getString(getString(R.string.orientation_background_key), getString(R.string.t_b)).toString()
        val appBackgroundDrawable = when (orientationBackgroundGradient) {
            getString(R.string.bl_tr) -> {
                GradientDrawable(GradientDrawable.Orientation.BL_TR, intArrayOf(colorBackgroundStart, colorBackgroundEnd))
            }
            getString(R.string.l_r) -> {
                GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, intArrayOf(colorBackgroundStart, colorBackgroundEnd))
            }
            getString(R.string.tl_br) -> {
                GradientDrawable(GradientDrawable.Orientation.TL_BR, intArrayOf(colorBackgroundStart, colorBackgroundEnd))
            }
            else -> {
                GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, intArrayOf(colorBackgroundStart, colorBackgroundEnd))
            }
        }
        val layoutParent = findViewById<ConstraintLayout>(R.id.layoutParent)
        layoutParent.background = appBackgroundDrawable

//        upper Buttons
        colorUpperButtons = sharedPreferences.getInt(getString(R.string.color_upper_buttons_key), getColor(R.color.colorUpperButtons))
        colorUpperButtonsStroke = sharedPreferences.getInt(getString(R.string.color_upper_buttons_stroke_key), getColor(R.color.colorUpperButtonsStroke))
        widthUpperButtonsStroke = sharedPreferences.getInt(getString(R.string.width_upper_buttons_stroke_key), 0)
        cornerRadiusUpperButtonsTop = sharedPreferences.getInt(getString(R.string.top_corner_radii_upper_buttons_key), 45)
        cornerRadiusUpperButtonsBottom = sharedPreferences.getInt(getString(R.string.bottom_corner_radii_upper_buttons_key), 80)

        val upperButtonsDrawable = GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, intArrayOf(colorUpperButtons, colorUpperButtons)).apply {
            setStroke(widthUpperButtonsStroke, colorUpperButtonsStroke)
            shape = GradientDrawable.RECTANGLE
            cornerRadii = FloatArray(8) { if (it < 4) cornerRadiusUpperButtonsTop.toFloat() else cornerRadiusUpperButtonsBottom.toFloat() }
        }

        for (upperButton in upperButtons) {
            upperButton.background = upperButtonsDrawable
        }
        for (upperImageButton in upperImageButtons) {
            upperImageButton.background = upperButtonsDrawable
        }


//      lower Buttons
        colorLowerButtons = sharedPreferences.getInt(getString(R.string.color_lower_buttons_key), getColor(R.color.colorBottomButtons))
        colorLowerButtonsStroke = sharedPreferences.getInt(getString(R.string.color_lower_buttons_stroke_key), getColor(R.color.colorBottomButtonsStroke))
        widthLowerButtonsStroke = sharedPreferences.getInt(getString(R.string.width_lower_buttons_stroke_key), 2)
        cornerRadiusLowerButtonsTop = sharedPreferences.getInt(getString(R.string.top_corner_radii_lower_buttons_key), 45)
        cornerRadiusLowerButtonsBottom = sharedPreferences.getInt(getString(R.string.bottom_corner_radii_lower_buttons_key), 80)

        val lowerButtonsDrawable = GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, intArrayOf(colorLowerButtons, colorLowerButtons)).apply {
            setStroke(widthLowerButtonsStroke, colorLowerButtonsStroke)
            shape = GradientDrawable.RECTANGLE
            cornerRadii =  FloatArray(8) { if (it < 4) cornerRadiusLowerButtonsTop.toFloat() else cornerRadiusLowerButtonsBottom.toFloat() }
        }
        for (lowerButton in lowerButtons) {
            lowerButton.background = lowerButtonsDrawable
        }
        for (lowerImageButton in lowerImageButtons) {
            lowerImageButton.background = lowerButtonsDrawable
        }


//        deleting Buttons
        colorDeletingButtons = sharedPreferences.getInt(getString(R.string.color_deleting_buttons_key), getColor(R.color.colorDeletingButtons))
        colorDeletingButtonsStroke = sharedPreferences.getInt(getString(R.string.color_deleting_buttons_stroke_key), getColor(R.color.colorDeletingButtonsStroke))
        widthDeletingButtonsStroke = sharedPreferences.getInt(getString(R.string.width_deleting_buttons_stroke_key), 1)

        val deletingButtonsDrawable = GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, intArrayOf(colorDeletingButtons, colorDeletingButtons)).apply {
            setStroke(widthDeletingButtonsStroke, colorDeletingButtonsStroke)
            shape = GradientDrawable.RECTANGLE
            cornerRadii = FloatArray(8) { if (it < 4) cornerRadiusLowerButtonsTop.toFloat() else cornerRadiusLowerButtonsBottom.toFloat() }
        }
        for (deletingButton in deletingButtons) {
            deletingButton.background = deletingButtonsDrawable
        }


//        navigation Button
        colorNavigationButton = sharedPreferences.getInt(getString(R.string.color_navigation_button_key), getColor(R.color.colorNavigationButton))
        colorNavigationButtonStroke = sharedPreferences.getInt(getString(R.string.color_navigation_button_stroke_key), getColor(R.color.colorNavigationButtonStroke))
        widthNavigationButtonStroke = sharedPreferences.getInt(getString(R.string.width_navigation_button_stroke_key), 3)
        cornerRadiusNavigationButton = sharedPreferences.getInt(getString(R.string.corner_radius_navigation_button_key), 125)
        navigationField.background = GradientDrawable().apply {
            setColor(colorNavigationButton)
            setStroke(widthNavigationButtonStroke, colorNavigationButtonStroke)
            shape = GradientDrawable.RECTANGLE
            cornerRadius = cornerRadiusNavigationButton.toFloat()
        }
        btnLeft.setBackgroundColor(colorNavigationButton)
        btnUp.setBackgroundColor(colorNavigationButton)
        btnRight.setBackgroundColor(colorNavigationButton)
        btnDown.setBackgroundColor(colorNavigationButton)


//        display
        colorDisplay = sharedPreferences.getInt(getString(R.string.color_display_key), getColor(R.color.colorDisplay))
        colorDisplayStroke = sharedPreferences.getInt(getString(R.string.color_display_stroke_key), getColor(R.color.colorDisplayStroke))
        widthDisplayStroke = sharedPreferences.getInt(getString(R.string.width_display_stroke_key), 10)
        calculatorDisplay.background = GradientDrawable().apply {
            setColor(colorDisplay)
            setStroke(widthDisplayStroke, colorDisplayStroke)
            shape = GradientDrawable.RECTANGLE
            cornerRadii = floatArrayOf(20F, 20F, 20F, 20F, 30F, 30F, 30F, 30F)
        }

//        text upper Buttons
        textColorUpperButtons = sharedPreferences.getInt(getString(R.string.text_color_upper_buttons_key), getColor(R.color.colorTextUpperButtons))
        for (btn in upperButtons) {
            btn.setTextColor(textColorUpperButtons)
        }
        for (btn in upperImageButtons) {
            btn.drawable.setTint(textColorUpperButtons)
        }

//        text lower buttons
        textColorLowerButtons = sharedPreferences.getInt(getString(R.string.text_color_lower_buttons_key), getColor(R.color.colorTextBottomButtons) )
        for (btn in lowerButtons) {
            btn.setTextColor(textColorLowerButtons)
        }
        for (imageButton in lowerImageButtons) {
            imageButton.drawable.setTint(textColorLowerButtons)
        }

//        text deleting buttons
        textColorDeletingButtons = sharedPreferences.getInt(getString(R.string.text_color_deleting_buttons_key), getColor(R.color.black))

        for (delButton in deletingButtons) {
            delButton.setTextColor(textColorDeletingButtons)
        }

//        text secondary functionality
        textColorSecondaryFunctionality = sharedPreferences.getInt(getString(R.string.text_color_secondary_funs_key), getColor(R.color.colorTextSecondaryFuns) )
        for (imgView in secFunctionalityIVs) {
            imgView.drawable.setTint(textColorSecondaryFunctionality)
        }
        for (tV in secFunctionalityTVs) {
            tV.setTextColor(textColorSecondaryFunctionality)
        }

//        text variables
        textColorVariables = sharedPreferences.getInt(getString(R.string.text_color_variables_key), getColor(R.color.colorVariables))
        for (tV in variableTVs) {
            tV.setTextColor(textColorVariables)
        }
    }

}