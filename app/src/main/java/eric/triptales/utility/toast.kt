package eric.triptales.utility

import android.content.Context
import android.widget.Toast

/**
 * Utility object for displaying Toast messages.
 *
 * Simplifies the process of showing Toast messages by optionally using a globally
 * initialized application context. Ensures safe use of Toast messages across the app.
 */
object ToastUtil {
    private var appContext: Context? = null

    /**
     * Initializes the ToastUtil with an application context.
     *
     * This should be called during application startup to allow showing Toasts without
     * needing to pass a `Context` every time.
     *
     * @param context The application context to initialize with.
     */
    fun initialize(context: Context) {
        appContext = context.applicationContext
    }

    /**
     * Displays a Toast message.
     *
     * Can use the globally initialized application context if no specific context is provided.
     * Throws an exception if no context is available.
     *
     * @param context Optional specific context for the Toast. Defaults to the initialized app context.
     * @param message The message to display in the Toast.
     * @throws IllegalStateException If no context is available for displaying the Toast.
     */
    fun showToast(context: Context? = null, message: String) {
        val safeContext = context ?: appContext
        if (safeContext != null) {
            Toast.makeText(safeContext, message, Toast.LENGTH_SHORT).show()
        } else {
            throw IllegalStateException("ToastUtil is not initialized with a Context.")
        }
    }
}
