package android.view

open class View() {
    // Soporte para listener Java-style: android.view.View.OnClickListener
    interface OnClickListener {
        fun onClick(v: View?)
    }

    private var clickListenerInterface: OnClickListener? = null
    private var clickListenerLambda: ((View?) -> Unit)? = null

    fun setOnClickListener(listener: OnClickListener?) {
        clickListenerInterface = listener
        clickListenerLambda = null
    }

    // Kotlin-friendly overload that accepts a lambda
    fun setOnClickListener(listener: ((View?) -> Unit)?) {
        clickListenerLambda = listener
        clickListenerInterface = null
    }

    fun performClick(): Boolean {
        clickListenerInterface?.onClick(this)
        clickListenerLambda?.invoke(this)
        return clickListenerInterface != null || clickListenerLambda != null
    }

    // Non-generic overload to match Android framework signature (avoids NoSuchMethodError in tests)
    fun findViewById(id: Int): View? = null

    // Generic variant for Kotlin callers
    fun <T> findViewById(id: Int): T? = null
}
