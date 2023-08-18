package com.example.ninesquaregridview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.example.ninesquaregridview.MathUtil.Companion.checkInRound


class NineSquareGridView : View {


    private var mPoints: Array<Array<Point?>> = Array(3) { Array<Point?>(3, { null }) }

    // 外圆的半径
    private var mDotRadius: Int = 0
    private var mInnerDotRadius = 0

    //画笔
    private lateinit var mLinePaint: Paint
    private lateinit var mPressedPaint: Paint
    private lateinit var mErrorPaint: Paint
    private lateinit var mNormalPaint: Paint
    private lateinit var mArrowPaint: Paint

    // 颜色
    private val mOuterPressedColor = 0xff8cbad8.toInt()
    private val mInnerPressedColor = 0xff0596f6.toInt()

    private val mOuterNormalColor = 0xffd9d9d9.toInt()
    private val mInnerNormalColor = 0xff929292.toInt()

    private val mOuterErrorColor = 0xff901032.toInt()
    private val mInnerErrorColor = 0xffea0945.toInt()

    //按下的时候是否按在点上
    private var mIsTouchPoint = false
    //存放已经点过的点
    private var mSelectedPoint = ArrayList<Point>()


    //是否初始化，确保只初始化一次
    private var mIsInit = false

    private var mIsErrorStatus = false

    //判断当前是否上锁
    private var mIsLocked= false

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context?, attributeSet: AttributeSet, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    )

    override fun onDraw(canvas: Canvas) {
        //初始化
        if (!mIsInit) {
            initDot()
            initPaint()
            mIsInit = true
        }
        //绘制
        drawShow(canvas)
    }


    /**
     * 初始化绘画
     */
    private fun drawShow(canvas: Canvas) {
        for (i in 0..2) {
            for (point in mPoints[i]) {
                if (point!!.isStatusNormal()) {
                    //先画外圆
                    mNormalPaint.color = mOuterNormalColor
                    canvas?.drawCircle(
                        point!!.x.toFloat(),
                        point.y.toFloat(),
                        mDotRadius.toFloat(),
                        mNormalPaint
                    )
                    //绘制内点
                    mNormalPaint.color = mInnerNormalColor
                    canvas?.drawCircle(
                        point!!.x.toFloat(),
                        point.y.toFloat(),
                        mDotRadius / 6.toFloat(),
                        mNormalPaint
                    )
                }
                if (point.isStatusPressed()) {
                    //先画外圆
                    mPressedPaint.color = mOuterPressedColor
                    canvas?.drawCircle(
                        point!!.x.toFloat(),
                        point.y.toFloat(),
                        mDotRadius.toFloat(),
                        mPressedPaint
                    )
                    //绘制内点
                    mPressedPaint.color = mInnerPressedColor
                    canvas?.drawCircle(
                        point!!.x.toFloat(),
                        point.y.toFloat(),
                        mDotRadius / 6.toFloat(),
                        mPressedPaint
                    )
                }
                if (point.isStatusError()) {
                    //先画外圆
                    mErrorPaint.color = mOuterErrorColor
                    canvas?.drawCircle(
                        point!!.x.toFloat(),
                        point.y.toFloat(),
                        mDotRadius.toFloat(),
                        mErrorPaint
                    )
                    //绘制内点
                    mErrorPaint.color = mInnerErrorColor
                    canvas?.drawCircle(
                        point!!.x.toFloat(),
                        point.y.toFloat(),
                        mDotRadius / 6.toFloat(),
                        mErrorPaint
                    )
                }
            }
        }

        //绘制连线和箭头
        drawLine(canvas)

    }

    private fun drawLine(canvas: Canvas) {
        if (mSelectedPoint.size >= 1) {
            //两点之间绘制连线
            var lastPoint = mSelectedPoint[0]

            for (i in 0..<mSelectedPoint.size) {
                //画一条线
                drawLine(lastPoint, mSelectedPoint[i], canvas, mLinePaint)
                //画箭头
                drawArrow(lastPoint, mSelectedPoint[i], canvas, mArrowPaint,(mDotRadius / 4).toFloat(), 38)
                lastPoint = mSelectedPoint[i]
            }
            //绘制最后一个点到手指的线
            val mNowPoint = Point(mMovingX.toInt(), mMovingY.toInt())
            //手指按的地方不在内圆内，并且有按到外圆内
            if (!checkInRound(mNowPoint, lastPoint, mInnerDotRadius * 3 / 2.toFloat())&&mIsTouchPoint) {
                drawLine(lastPoint, mNowPoint, canvas, mLinePaint)
            }
        }

    }

    private fun drawArrow(
        start: Point,
        end: Point,
        canvas: Canvas,
        paint: Paint,
        arrowHeight: Float,
        angle: Int
    ) {
        val d = MathUtil.distance(
            start.x.toFloat(),
            start.y.toFloat(),
            end.x.toFloat(),
            end.y.toFloat()
        )
        val sin_B = ((end.x - start.x) / d).toFloat()
        val cos_B = ((end.y - start.y) / d).toFloat()
        val tan_A = Math.tan(Math.toRadians(angle.toDouble())).toFloat()
        val h = (d - arrowHeight.toDouble() - mDotRadius * 1.1).toFloat()
        val l = arrowHeight * tan_A
        val a = l * sin_B
        val b = l * cos_B
        val x0 = h * sin_B
        val y0 = h * cos_B
        val x1 = start.x + (h + arrowHeight) * sin_B
        val y1 = start.y + (h + arrowHeight) * cos_B
        val x2 = start.x + x0 - b
        val y2 = start.y.toFloat() + y0 + a
        val x3 = start.x.toFloat() + x0 + b
        val y3 = start.y + y0 - a
        val path = Path()
        path.moveTo(x1, y1)
        path.lineTo(x2, y2)
        path.lineTo(x3, y3)
        path.close()
        canvas.drawPath(path, paint)

    }

    private fun drawLine(startPoint: Point, endPoint: Point, canvas: Canvas, paint: Paint) {
        //dis两点间距离
        val distance = MathUtil.distance(
            startPoint.x.toFloat(), startPoint.y.toFloat(),
            endPoint.x.toFloat(), endPoint.y.toFloat()
        )

        //两点的水平间距和垂直间距
        val dx = startPoint.x - endPoint.x
        val dy = startPoint.y - endPoint.y

        //通过相似求取圆心到内圈边的距离
        val rx = (mInnerDotRadius / distance) * dx.toFloat()
        val ry = (mInnerDotRadius / distance) * dy.toFloat()

        canvas.drawLine(
            startPoint.x - rx, startPoint.y - ry,
            endPoint.x + rx, endPoint.y + ry, paint
        )


    }

    /**
     * 初始化画笔
     * 三个点的画笔，线的画笔，箭头的画笔
     */
    private fun initPaint() {
        // 线的画笔
        mLinePaint = Paint()
        mLinePaint.color = mInnerPressedColor
        mLinePaint.style = Paint.Style.STROKE
        mLinePaint.isAntiAlias = true
        mLinePaint.strokeWidth = (mDotRadius / 9).toFloat()
        // 按下的画笔
        mPressedPaint = Paint()
        mPressedPaint.style = Paint.Style.STROKE
        mPressedPaint.isAntiAlias = true
        mPressedPaint.strokeWidth = (mDotRadius / 6).toFloat()
        // 错误的画笔
        mErrorPaint = Paint()
        mErrorPaint.style = Paint.Style.STROKE
        mErrorPaint.isAntiAlias = true
        mErrorPaint.strokeWidth = (mDotRadius / 6).toFloat()
        // 默认的画笔
        mNormalPaint = Paint()
        mNormalPaint.style = Paint.Style.STROKE
        mNormalPaint.isAntiAlias = true
        mNormalPaint.strokeWidth = (mDotRadius / 9).toFloat()
        // 箭头的画笔
        mArrowPaint = Paint()
        mArrowPaint.color = mInnerPressedColor
        mArrowPaint.style = Paint.Style.FILL
        mArrowPaint.isAntiAlias = true
    }

    /**
     * 初始化点
     */
    private fun initDot() {
        var width = this.width
        var height = this.height

        //兼容问题，判断横竖屏
        var squareWidth = 0
        var unitWidth = 0
        var offSetY = 0
        var offSetX = 0
        if (height > width) {
            squareWidth = width / 3
            offSetY = (height - width) / 2
        } else {
            squareWidth = height / 3
            offSetX = (width - height) / 2
        }
        unitWidth = squareWidth / 2
        mDotRadius = width / 12
        mInnerDotRadius = mDotRadius / 6


        //九个点，存起来
        //回调密码，点有下标
        mPoints[0][0] = Point(offSetX + unitWidth, offSetY + unitWidth, 0)
        mPoints[0][1] = Point(offSetX + unitWidth * 3, offSetY + unitWidth, 1)
        mPoints[0][2] = Point(offSetX + unitWidth * 5, offSetY + unitWidth, 2)
        mPoints[1][0] = Point(offSetX + unitWidth, offSetY + unitWidth * 3, 3)
        mPoints[1][1] = Point(offSetX + unitWidth * 3, offSetY + unitWidth * 3, 4)
        mPoints[1][2] = Point(offSetX + unitWidth * 5, offSetY + unitWidth * 3, 5)
        mPoints[2][0] = Point(offSetX + unitWidth, offSetY + unitWidth * 5, 6)
        mPoints[2][1] = Point(offSetX + unitWidth * 3, offSetY + unitWidth * 5, 7)
        mPoints[2][2] = Point(offSetX + unitWidth * 5, offSetY + unitWidth * 5, 8)
    }

    //记录手指位置
    private var mMovingX = 0f
    private var mMovingY = 0f

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        mMovingX = event!!.x
        mMovingY = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                //判断是否按在一个宫格里
                val point = point
                if (point != null) {
                    mIsTouchPoint = true
                    mSelectedPoint.add(point)
                    //改变当前点的状态
                    point.setStatusPressed()
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (mIsTouchPoint) {
                    val point = point
                    if (point != null) {
                        if (mSelectedPoint.contains(point).not()) {
                            mSelectedPoint.add(point!!)
                            //改变当前点的状态
                        }
                        point!!.setStatusPressed()
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                if (mIsTouchPoint) {
                    //回调密码获取监听
                    if (mSelectedPoint.size == 1) {
                        // 清空选择
                        clearSelectPoints()
                    } else if (mSelectedPoint.size <= 4) {
                        // 太短显示错误
                        showSelectError()
                    } else {
                        // 成功回调
                        if (mListener != null) {
                            lockCallBack()
                        }
                    }
                    mIsTouchPoint = false
                }
            }
        }
        invalidate()
        return true
    }

    /**
     * 宫格点类
     */
    class Point(var x: Int, var y: Int, var index: Int = 9) {
        private val STATUS_NORMAL = 1
        private val STATUS_PRESSED = 2
        private val STATUS_ERROR = 3

        //当前状态
        private var status = STATUS_NORMAL
        fun setStatusNormal() {
            status = STATUS_NORMAL
        }

        fun setStatusPressed() {
            status = STATUS_PRESSED
        }

        fun setStatusError() {
            status = STATUS_ERROR
        }


        fun isStatusNormal(): Boolean {
            return status == STATUS_NORMAL
        }

        fun isStatusPressed(): Boolean {
            return status == STATUS_PRESSED
        }

        fun isStatusError(): Boolean {
            return status == STATUS_ERROR
        }

    }

    private val point: Point?
        get() {
            //判断按压的位置是否在某个外圈内
            for (i in 0..2)
                for (point in mPoints[i]) {
                    if (MathUtil.checkInRound(
                            mMovingX, mMovingY,
                            point!!.x.toFloat(), point.y.toFloat(), mDotRadius.toFloat()
                        )
                    ) return point
                }
            return null
        }


    /**
     * 回调
     */
    private fun lockCallBack() {
        var password = ""
        for (selectPoint in mSelectedPoint) {
            password += selectPoint.index
        }
        //没有设置密码的情况
        if (!mIsLocked) {
            mListener!!.lock(password)
            mIsLocked = true
        }
        else{
            mListener!!.unLock(password)
        }

    }

    /**
     * 显示错误
     */
    fun showSelectError() {
        for (selectPoint in mSelectedPoint) {
            selectPoint.setStatusError()
        }

        postDelayed({
            clearSelectPoints()
            invalidate()
        }, 1000)
    }

    /**
     * 清空所有的点
     */
    private fun clearSelectPoints() {
        for (selectPoint in mSelectedPoint) {
            selectPoint.setStatusNormal()
        }
        mSelectedPoint.clear()
    }

    /**
     * 清空所有的点并重绘
     */
    fun clearSelect() {
        for (selectPoint in mSelectedPoint) {
            selectPoint.setStatusNormal()
        }
        mSelectedPoint.clear()
        invalidate()
    }

    private var mListener: LockPatternListener? = null
    fun setLockPatternListener(listener: LockPatternListener) {
        this.mListener = listener
    }

    interface LockPatternListener {
        fun lock(password: String)
        fun unLock(password: String)
    }
}