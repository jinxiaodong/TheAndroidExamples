package com.androidkits.example.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;

public class DragScaleView extends AppCompatImageView implements View.OnTouchListener {
    protected int screenWidth;
    protected int screenHeight;
    protected int lastX;
    protected int lastY;
    private int oriLeft;
    private int oriRight;
    private int oriTop;
    private int oriBottom;
    private int dragDirection;
    private static final int TOP = 0x15;
    private static final int LEFT = 0x16;
    private static final int BOTTOM = 0x17;
    private static final int RIGHT = 0x18;
    private static final int LEFT_TOP = 0x11;
    private static final int RIGHT_TOP = 0x12;
    private static final int LEFT_BOTTOM = 0x13;
    private static final int RIGHT_BOTTOM = 0x14;
    private static final int TOUCH_TWO = 0x21;
    private static final int CENTER = 0x19;
    private int offset = 0; //可超出其父控件的偏移量
    protected Paint paint = new Paint();
    private static final int touchDistance = 80; //触摸边界的有效距离

    // 初始的两个手指按下的触摸点的距离
    private float oriDis = 1f;
    private float oriRotation = 0;

    /**
     * 初始化获取屏幕宽高
     */
    protected void initScreenW_H() {
        screenHeight = getResources().getDisplayMetrics().heightPixels - 40;
        screenWidth = getResources().getDisplayMetrics().widthPixels;
    }

    public DragScaleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setOnTouchListener(this);
        initScreenW_H();
    }

    public DragScaleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnTouchListener(this);
        initScreenW_H();
    }

    public DragScaleView(Context context) {
        super(context);
        setOnTouchListener(this);
        initScreenW_H();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(Color.GRAY);
        paint.setStrokeWidth(4.0f);
        paint.setStyle(Paint.Style.STROKE);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction() & MotionEvent.ACTION_MASK;
        checkDragPoint();
        if (action == MotionEvent.ACTION_DOWN) {
            oriLeft = v.getLeft();
            oriRight = v.getRight();
            oriTop = v.getTop();
            oriBottom = v.getBottom();
            lastY = (int) event.getRawY();
            lastX = (int) event.getRawX();
            oriRotation = v.getRotation();
            dragDirection = getDirection(v, (int) event.getX(),
                    (int) event.getY());
        }
//        if (action == MotionEvent.ACTION_POINTER_DOWN){
//            oriLeft = v.getLeft();
//            oriRight = v.getRight();
//            oriTop = v.getTop();
//            oriBottom = v.getBottom();
//            lastY = (int) event.getRawY();
//            lastX = (int) event.getRawX();
//            dragDirection = TOUCH_TWO;
//            oriDis = distance(event);
//        }
        // 处理拖动事件
        delDrag(v, event, action);
        invalidate();
        return false;
    }

    /**
     * 处理拖动事件
     *
     * @param v
     * @param event
     * @param action
     */
    protected void delDrag(View v, MotionEvent event, int action) {
        switch (action) {
            case MotionEvent.ACTION_MOVE:
                int dx = (int) event.getRawX() - lastX;
                int dy = (int) event.getRawY() - lastY;
                switch (dragDirection) {
//                    case LEFT: // 左边缘
//                        left(v, dx);
//                        break;
//                    case RIGHT: // 右边缘
//                        right(v, dx);
//                        break;
//                    case BOTTOM: // 下边缘
//                        bottom(v, dy);
//                        break;
//                    case TOP: // 上边缘
//                        top(v, dy);
//                        break;
                    case CENTER: // 点击中心-->>移动
                        center(v, dx, dy);
                        break;
//                    case LEFT_BOTTOM: // 左下
//                        left(v, dx);
//                        bottom(v, dy);
//                        break;
//                    case LEFT_TOP: // 左上
//                        left(v, dx);
//                        top(v, dy);
//                        break;
                    case RIGHT_BOTTOM: // 右下

                        if ((dx > 0 && dy > 0) || (dx < 0 && dy < 0)) {
                            //左上、右下滑动
                            right(v, dx);
                            bottom(v, dy);
                            if (dragDirection != CENTER) {
                                v.layout(oriLeft, oriTop, oriRight, oriBottom);
                            }
                        } else {
                            Point center = new Point(oriLeft + (oriRight - oriLeft) / 2, oriTop + (oriBottom - oriTop) / 2);
                            Point first = new Point(lastX, lastY);
                            Point second = new Point((int) event.getRawX(), (int) event.getRawY());
                            oriRotation += angle(center, first, second);

                            v.setRotation(oriRotation);
                        }

                        break;
//                    case RIGHT_TOP: // 右上
//                        right(v, dx);
//                        top(v, dy);
//                        break;
//                    case TOUCH_TWO: //双指操控
//                        float newDist =distance(event);
//                        float scale = newDist / oriDis;
//                        //控制双指缩放的敏感度
//                        int distX = (int) (scale*(oriRight-oriLeft)-(oriRight-oriLeft))/50;
//                        int distY = (int) (scale*(oriBottom-oriTop)-(oriBottom-oriTop))/50;
//                        if (newDist>10f){//当双指的距离大于10时，开始相应处理
//                            left(v, -distX);
//                            top(v, -distY);
//                            right(v, distX);
//                            bottom(v, distY);
//                        }
//                        break;

                }

                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                dragDirection = 0;
                break;
        }
    }


    /**
     * 触摸点为中心->>移动
     *
     * @param v
     * @param dx
     * @param dy
     */
    private void center(View v, int dx, int dy) {
        int left = v.getLeft() + dx;
        int top = v.getTop() + dy;
        int right = v.getRight() + dx;
        int bottom = v.getBottom() + dy;
        if (left < -offset) {
            left = -offset;
            right = left + v.getWidth();
        }
        if (right > screenWidth + offset) {
            right = screenWidth + offset;
            left = right - v.getWidth();
        }
        if (top < -offset) {
            top = -offset;
            bottom = top + v.getHeight();
        }
        if (bottom > screenHeight + offset) {
            bottom = screenHeight + offset;
            top = bottom - v.getHeight();
        }
        Log.d("raydrag", left + "  " + top + "  " + right + "  " + bottom + "  " + dx);
        v.layout(left, top, right, bottom);
    }

    /**
     * 触摸点为上边缘
     *
     * @param v
     * @param dy
     */
    private void top(View v, int dy) {
        oriTop += dy;
        if (oriTop < -offset) {
            //对view边界的处理，如果子view达到父控件的边界，offset代表允许超出父控件多少
            oriTop = -offset;
        }
        if (oriBottom - oriTop - 2 * offset < 200) {
            oriTop = oriBottom - 2 * offset - 200;
        }
    }

    /**
     * 触摸点为下边缘
     *
     * @param v
     * @param dy
     */
    private void bottom(View v, int dy) {
        oriBottom += dy;
        if (oriBottom > screenHeight + offset) {
            oriBottom = screenHeight + offset;
        }
        if (oriBottom - oriTop - 2 * offset < 200) {
            oriBottom = 200 + oriTop + 2 * offset;
        }
    }

    /**
     * 触摸点为右边缘
     *
     * @param v
     * @param dx
     */
    private void right(View v, int dx) {
        oriRight += dx;
        if (oriRight > screenWidth + offset) {
            oriRight = screenWidth + offset;
        }
        if (oriRight - oriLeft - 2 * offset < 200) {
            oriRight = oriLeft + 2 * offset + 200;
        }
    }

    /**
     * 触摸点为左边缘
     *
     * @param v
     * @param dx
     */
    private void left(View v, int dx) {
        oriLeft += dx;
        if (oriLeft < -offset) {
            oriLeft = -offset;
        }
        if (oriRight - oriLeft - 2 * offset < 200) {
            oriLeft = oriRight - 2 * offset - 200;
        }
    }

    /**
     * 获取触摸点flag
     *
     * @param v
     * @param x
     * @param y
     * @return
     */
    protected int getDirection(View v, int x, int y) {
        int left = v.getLeft();
        int right = v.getRight();
        int bottom = v.getBottom();
        int top = v.getTop();
        if (x < touchDistance && y < touchDistance) {
            return LEFT_TOP;
        }
        if (y < touchDistance && right - left - x < touchDistance) {
            return RIGHT_TOP;
        }
        if (x < touchDistance && bottom - top - y < touchDistance) {
            return LEFT_BOTTOM;
        }
        if (right - left - x < touchDistance && bottom - top - y < touchDistance) {
            return RIGHT_BOTTOM;
        }
        if (x < touchDistance) {
            return LEFT;
        }
        if (y < touchDistance) {
            return TOP;
        }
        if (right - left - x < touchDistance) {
            return RIGHT;
        }
        if (bottom - top - y < touchDistance) {
            return BOTTOM;
        }
        return CENTER;
    }

    /**
     * 计算两个手指间的距离
     *
     * @param event 触摸事件
     * @return 放回两个手指之间的距离
     */
    private float distance(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);//两点间距离公式
    }


    /**
     * 检查属于哪个象限
     * <p>
     * 0+++++++1
     * +       +
     * +       +
     * +       +
     * +       +
     * 3+++++++4
     *
     * @return
     */
    private int checkDragPoint() {
//        oriLeft;
//        oriRight = v.getRight();
//        oriTop = v.getTop();
//        oriBottom = v.getBottom();

        Point center = new Point(oriLeft + (oriRight - oriLeft) / 2, oriTop + (oriBottom - oriTop) / 2);
        int r = (int) Math.hypot(oriRight - center.x, oriBottom - center.y);
        Log.e("初始点坐标", "x ==" + oriRight + ",y ==" + oriBottom);
        float angle = angle(center, new Point(center.x + r, center.y), new Point(oriRight, oriBottom));
        int x1 = (int) (center.x + r * Math.cos(Math.toRadians(angle)));
        int y1 = (int) (center.y + r * Math.sin(Math.toRadians(angle)));

        Log.e("计算的坐标", "x1 ==" + x1 + ", y1 ==" + y1);
        return -1;
    }

    /**
     * 算出该点与水平的角度的值，用移动点角度减去起始点角度就是旋转角度。
     */
    private double getAngle(double xTouch, double yTouch, Point center) {
        double x = xTouch - center.x;
        double y = yTouch - center.y;
        return (Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI);
    }


    public float angle(Point cen, Point first, Point second) {
        float dx1, dx2, dy1, dy2;

        dx1 = first.x - cen.x;
        dy1 = first.y - cen.y;
        dx2 = second.x - cen.x;
        dy2 = second.y - cen.y;

        // 计算三边的平方
        float ab2 = (second.x - first.x) * (second.x - first.x) + (second.y - first.y) * (second.y - first.y);
        float oa2 = dx1 * dx1 + dy1 * dy1;
        float ob2 = dx2 * dx2 + dy2 * dy2;

        // 根据两向量的叉乘来判断顺逆时针
        boolean isClockwise = ((first.x - cen.x) * (second.y - cen.y) - (first.y - cen.y) * (second.x - cen.x)) > 0;

        // 根据余弦定理计算旋转角的余弦值
        double cosDegree = (oa2 + ob2 - ab2) / (2 * Math.sqrt(oa2) * Math.sqrt(ob2));

        // 异常处理，因为算出来会有误差绝对值可能会超过一，所以需要处理一下
        if (cosDegree > 1) {
            cosDegree = 1;
        } else if (cosDegree < -1) {
            cosDegree = -1;
        }

        // 计算弧度
        double radian = Math.acos(cosDegree);

        // 计算旋转过的角度，顺时针为正，逆时针为负
        return (float) (isClockwise ? Math.toDegrees(radian) : -Math.toDegrees(radian));

    }

}