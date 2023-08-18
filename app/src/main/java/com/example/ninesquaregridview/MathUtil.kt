package com.example.ninesquaregridview

import kotlin.math.sqrt
import com.example.ninesquaregridview.NineSquareGridView.Point

class MathUtil {

    companion object{
        /**
         * 检查某点是否在某个圆中
         * @param mPoint 某点
         * @param toPoint 圆心
         * @param r 圆的半径
         */
        fun checkInRound(mPoint: Point, toPoint: Point, r: Float): Boolean {
            return sqrt(((mPoint.x - toPoint.x) * (mPoint.x - toPoint.x) +
                    (mPoint.y - toPoint.y) * (mPoint.y - toPoint.y)).toDouble()) < r
        }


        fun checkInRound(mx: Float, my: Float, x: Float, y: Float, r: Float): Boolean {
            return sqrt(((mx - x) * (mx - x) + (my - y) * (my - y)).toDouble()) <r
        }

        /**
         * 两点间的距离
         */
        fun distance(x: Float, y: Float, toX: Float, toY: Float): Float {
            return sqrt((toY - y) * (toY - y) + (toX - x) * (toX - x))
        }
    }
}