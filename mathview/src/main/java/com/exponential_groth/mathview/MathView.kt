package com.exponential_groth.mathview

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.MotionEvent
import android.webkit.WebSettings
import android.webkit.WebView

import com.x5.template.Chunk
import com.x5.template.Theme
import com.x5.template.providers.AndroidTemplates


// The package com.exponential_groth.mathview is highly inspired by https://github.com/jianzhongli/MathView from jianzhongli. The only changes made are:
//      - converting MathView.java to Kotlin
//      - adding symbols to katex.min.js


class MathView(context: Context, attrs: AttributeSet?): WebView(context, attrs) {
    private var mText: String = ""
    private var mConfig: String? = null
    private var mEngine = 0

    var text: String
        get() = mText
        set(text) {
            mText = text
            val chunk = chunk
            chunk.set(TAG_FORMULA, mText)
            chunk.set(TAG_CONFIG, mConfig)
            loadDataWithBaseURL(null, chunk.toString(), "text/html", "utf-8", "about:blank")
        }

    init {
        settings.javaScriptEnabled = true
        settings.cacheMode = WebSettings.LOAD_NO_CACHE
        setBackgroundColor(Color.TRANSPARENT)
        val mTypeArray = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.MathView,
            0, 0
        )
        try { // the order of execution of setEngine() and setText() matters
            setEngine(mTypeArray.getInteger(R.styleable.MathView_engine, 0))
            text = mTypeArray.getString(R.styleable.MathView_text)?:""
        } finally {
            mTypeArray.recycle()
        }
    }

    // disable touch event on MathView
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return false
    }

    private val chunk: Chunk
        get() {
            val loader = AndroidTemplates(context)
            val template = when (mEngine) {
                Engine.MATHJAX -> TEMPLATE_MATHJAX
                else -> TEMPLATE_KATEX
            }
            return Theme(loader).makeChunk(template)
        }


    /**
     * Tweak the configuration of MathJax.
     * The `config` string is a call statement for MathJax.Hub.Config().
     * For example, to enable auto line breaking, you can call:
     * config.("MathJax.Hub.Config({
     * CommonHTML: { linebreaks: { automatic: true } },
     * "HTML-CSS": { linebreaks: { automatic: true } },
     * SVG: { linebreaks: { automatic: true } }
     * });");
     *
     * This method should be call BEFORE setText() and AFTER setEngine().
     * PLEASE PAY ATTENTION THAT THIS METHOD IS FOR MATHJAX ONLY.
     * @param config
     */
    fun config(config: String?) {
        if (mEngine == Engine.MATHJAX) {
            mConfig = config
        }
    }

    /**
     * Set the js engine used for rendering the formulas.
     * @param engine must be one of the constants in class Engine
     *
     * This method should be call BEFORE setText().
     */
    fun setEngine(engine: Int) {
        mEngine = when (engine) {
            Engine.KATEX -> {
                Engine.KATEX
            }

            Engine.MATHJAX -> {
                Engine.MATHJAX
            }

            else -> Engine.KATEX
        }
    }

    object Engine {
        const val KATEX = 0
        const val MATHJAX = 1
    }

    private companion object {
        const val TEMPLATE_KATEX = "katex"
        const val TEMPLATE_MATHJAX = "mathjax"
        const val TAG_FORMULA = "formula"
        const val TAG_CONFIG = "config"
    }
}