package com.folioreader.ui.view

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.ComponentName
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import com.folioreader.Config
import com.folioreader.R
import com.folioreader.util.UiUtil

class FolioSearchView : SearchView {

    companion object {
        @JvmField
        val LOG_TAG: String = FolioSearchView::class.java.simpleName
    }

    @SuppressLint("RestrictedApi")
    private lateinit var searchAutoComplete: SearchAutoComplete
    private lateinit var searchMagIcon: ImageView
    private lateinit var searchCloseButton: ImageView

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    @SuppressLint("RestrictedApi")
    private fun init() {
        Log.v(LOG_TAG, "-> init")

        // Create the search magnifying glass icon
        searchMagIcon = ImageView(context)
        searchMagIcon.id = View.generateViewId() // Generate a unique ID
        searchMagIcon.layoutParams = LinearLayout.LayoutParams(0, 0) // Hide it initially
        addView(searchMagIcon)

        // Create the search edit frame
        val searchEditFrame = LinearLayout(context)
        searchEditFrame.id = View.generateViewId()
        searchEditFrame.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        addView(searchEditFrame)

        // Create the search AutoComplete text view
        searchAutoComplete = SearchAutoComplete(context)
        searchAutoComplete.id = View.generateViewId()
        searchEditFrame.addView(searchAutoComplete, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))

        // Create the search close button
        searchCloseButton = ImageView(context)
        searchCloseButton.id = View.generateViewId()
        // Set a default drawable for the close button
        searchCloseButton.setImageResource(R.drawable.ic_close_green_24dp)
        addView(searchCloseButton)

        // Set layout parameters for searchEditFrame
        (searchEditFrame.layoutParams as MarginLayoutParams).leftMargin = 0

        // Set the search view properties
        setIconifiedByDefault(false)
    }

    fun init(componentName: ComponentName, config: Config) {
        Log.v(LOG_TAG, "-> init")

        val searchManager: SearchManager =
            context.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        setSearchableInfo(searchManager.getSearchableInfo(componentName))

        adjustLayout()
        applyTheme(config)
    }

    private fun adjustLayout() {
        Log.v(LOG_TAG, "-> adjustLayout")

        // Hide searchHintIcon
        searchMagIcon.visibility = View.GONE
    }

    private fun applyTheme(config: Config) {
        Log.v(LOG_TAG, "-> applyTheme")

        // Ensure the drawable is not null before attempting to modify it
        val drawable = searchCloseButton.drawable
        if (drawable != null) {
            UiUtil.setColorIntToDrawable(config.currentThemeColor, drawable)
        } else {
            Log.e(LOG_TAG, "Drawable for searchCloseButton is null")
            // Optionally, you can set a default drawable here as well
        }

        UiUtil.setEditTextCursorColor(searchAutoComplete, config.currentThemeColor)
        UiUtil.setEditTextHandleColor(searchAutoComplete, config.currentThemeColor)
        searchAutoComplete.highlightColor =
            ColorUtils.setAlphaComponent(config.currentThemeColor, 85)

        if (config.isNightMode) {
            searchAutoComplete.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.night_title_text_color
                )
            )
            searchAutoComplete.setHintTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.night_text_color
                )
            )
        } else {
            searchAutoComplete.setHintTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.edit_text_hint_color
                )
            )
        }
    }

    fun setDayMode() {
        searchAutoComplete.setTextColor(ContextCompat.getColor(context, R.color.black))
    }

    fun setNightMode() {
        searchAutoComplete.setTextColor(ContextCompat.getColor(context, R.color.white))
    }
}
