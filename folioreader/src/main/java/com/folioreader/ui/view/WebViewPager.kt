package com.folioreader.ui.view

import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GestureDetectorCompat
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.folioreader.R

class WebViewPager : ViewPager {

    companion object {
        @JvmField
        val LOG_TAG: String = WebViewPager::class.java.simpleName
    }

    internal var horizontalPageCount: Int = 0
    private var folioWebView: FolioWebView? = null
    private var takeOverScrolling: Boolean = false
    var isScrolling: Boolean = false
        private set
    private var uiHandler: Handler? = null
    private var gestureDetector: GestureDetectorCompat? = null

    private var lastGestureType: LastGestureType? = null

    private enum class LastGestureType {
        OnSingleTapUp, OnLongPress, OnFling, OnScroll
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    private fun init() {

        uiHandler = Handler()
        gestureDetector = GestureDetectorCompat(context, GestureListener())

        addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

                isScrolling = true

                if (takeOverScrolling && folioWebView != null) {
                    val scrollX =
                        folioWebView!!.getScrollXPixelsForPage(position) + positionOffsetPixels
                    folioWebView!!.scrollTo(scrollX, 0)
                }

                if (positionOffsetPixels == 0) {
                    takeOverScrolling = false
                    isScrolling = false
                }
            }


            override fun onPageSelected(position: Int) {
            }


            override fun onPageScrollStateChanged(state: Int) {
            }
        })
    }
    fun setHorizontalPageCount(horizontalPageCount: Int) {
        this.horizontalPageCount = horizontalPageCount
        adapter = WebViewPagerAdapter()
        currentItem = 0

        if (folioWebView == null)
            folioWebView = (parent as View).findViewById(R.id.folioWebView)
    }

    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {

        override fun onDown(e: MotionEvent): Boolean {
            super@WebViewPager.onTouchEvent(e)
            return true
        }

        override fun onSingleTapUp(e: MotionEvent): Boolean {
            //Log.d(LOG_TAG, "-> onSingleTapUp");
            lastGestureType = LastGestureType.OnSingleTapUp
            return false
        }

        override fun onLongPress(e: MotionEvent) {
            super.onLongPress(e)
            lastGestureType = LastGestureType.OnLongPress
        }

        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            lastGestureType = LastGestureType.OnScroll
            return false
        }

        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            lastGestureType = LastGestureType.OnFling
            return false
        }

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null)
            return false

        if (gestureDetector == null)
            return false

        val gestureReturn = gestureDetector!!.onTouchEvent(event)
        if (gestureReturn)
            return true

        val superReturn = super.onTouchEvent(event)

        if (event.action == MotionEvent.ACTION_UP) {
            if (lastGestureType == LastGestureType.OnScroll || lastGestureType == LastGestureType.OnFling) {
                //Log.d(LOG_TAG, "-> onTouchEvent -> takeOverScrolling = true, " + "lastGestureType = " + lastGestureType);
                takeOverScrolling = true
            }
            lastGestureType = null
        }

        return superReturn
    }

    private inner class WebViewPagerAdapter : PagerAdapter() {

        override fun getCount(): Int {
            return horizontalPageCount
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {

            val view = LayoutInflater.from(container.context)
                .inflate(R.layout.view_webview_pager, container, false)
            container.addView(view)
            return view
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }
    }
}
