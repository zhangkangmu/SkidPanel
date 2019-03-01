package com.hong.zyh.skidpanel.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * Created by shuaihong on 2019/3/1.
 * 侧滑面板控件, 抽屉面板.
 *   测量             摆放     绘制
 measure   ->  layout  ->  draw
 |           |          |
 onMeasure -> onLayout -> onDraw 重写这些方法, 实现自定义控件

 View流程
 onMeasure() (在这个方法里指定自己的宽高) -> onDraw() (绘制自己的内容)

 ViewGroup流程
 onMeasure() (指定自己的宽高, 所有子View的宽高)-> onLayout() (摆放所有子View) -> onDraw() (绘制内容)
 */

public class SlideMenu extends ViewGroup {

    private float downX;
    private float moveX;
    public static final int MAIN_STATE = 0;
    public static final int MENU_STATE = 1;
    private int currentState = MAIN_STATE; // 当前模式
    //滚动数值模拟器，用于侧滑动画
    private Scroller scroller;

    public SlideMenu(Context context) {
        super(context);
        init();
    }

    public SlideMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SlideMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // 初始化滚动器, 数值模拟器
        scroller = new Scroller(getContext());
    }

    /**
     * 测量并设置 所有子View的宽高
     * @param widthMeasureSpec：当前控件的宽度测量
     * @param heightMeasureSpec: 当前控件的高度测量规则
     * 因为有时候布局不是写了多少宽高都会指定来分配的，有时候会被挤出去之类的，因此需要设置一下
     * 控件都是通过这个方法measure()实现设置宽高的
     *  1.先进行这个测量
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        View leftMenu = getChildAt(0);

        // 指定左面板的宽高
        leftMenu.measure(leftMenu.getLayoutParams().width,heightMeasureSpec);

        // 指定主面板的宽高
        View mainContent = getChildAt(1);
        mainContent.measure(widthMeasureSpec, heightMeasureSpec);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * changed: 当前控件的尺寸大小, 位置 是否发生了变化
     * left:当前控件 左边距(距离左方的距离)
     * top:当前控件 顶边距(距离上方的距离)
     * right:当前控件 右边界(距离左方的距离)
     * bottom:当前控件 下边界(距离上方的距离)
     * 2.再进行这个摆放
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // 摆放内容, 左面板
        View leftMenu = getChildAt(0);
        leftMenu.layout(-leftMenu.getMeasuredWidth(), 0, 0, b);

        // 主面板
        getChildAt(1).layout(l, t, r, b);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d("ACTION_MOVEX",""+getScrollX());
                Log.d("ACTION_MOVEY",""+getScrollY());
                Log.d("getMeasuredWidth",""+getChildAt(0).getMeasuredWidth());

                moveX = event.getX();
                // 将要发生的偏移量/变化量
                int scrollX = (int) (downX - moveX);

                // 计算将要滚动到的位置, 判断是否会超出去, 超出去了.不执行scrollBy
                // getScrollX() 当前滚动到的位置（是一个负数），newScrollPosition则是目前位置
                int newScrollPosition = getScrollX() + scrollX;
                // 计算将要滚动到的位置, 判断是否会超出去, 超出去了.不执行scrollBy

                Log.d("(newScrollPosition < -getChildAt(0).getMeasuredWidth())",
                        ""+(newScrollPosition < -getChildAt(0).getMeasuredWidth()));
                //可以想象是没有滑动的时候，那么隐藏的部分就是-240，通过判断当前的位置和-240相比，如果在-240左边也就是小于-240了，这种情况就是拉的太出来了，所以应该设为-240,0坐标
                if (newScrollPosition < -getChildAt(0).getMeasuredWidth()){ // 限定左边界
                    // 也就是< -240
                    //这时候是向右滑动到最右方
                    scrollTo(-getChildAt(0).getMeasuredWidth(), 0);
                } else if (newScrollPosition > 0){ // 限定右边界
                    // > 0
                    scrollTo(0, 0);
                } else {
                    // 让变化量生效
                    scrollBy(scrollX, 0);
                }
                downX = moveX;
                break;
            case MotionEvent.ACTION_UP:
                // 根据当前滚动到的位置, 和左面板的一半进行比较
                int leftCenter = (int) (-getChildAt(0).getMeasuredWidth() / 2.0f);

                if (getScrollX() < leftCenter) {
                    // 打开, 切换成菜单面板
                    currentState = MENU_STATE;
                    updateCurrentContent();
                } else {
                    // 关闭, 切换成主面板
                    currentState = MAIN_STATE;
                    updateCurrentContent();
                }
                break;
            default:

                break;
        }
        return true;
    }

    /**
     * 根据当前的状态, 执行 关闭/开启 的动画
     */
    private void updateCurrentContent() {
        //getScrollX()为负数
        int startX = getScrollX();
        int dx = 0;

        // 平滑滚动
        if(currentState == MENU_STATE){
            // 打开菜单
            //scrollTo(-getChildAt(0).getMeasuredWidth(), 0);

            dx = -getChildAt(0).getMeasuredWidth() - startX;
        }else{
            // 恢复主界面
            //scrollTo(0, 0);

            dx = 0 - startX;
        }
        // startX: 开始的x值
        // startY: 开始的y值
        // dx: 将要发生的水平变化量. 移动的x距离
        // dy: 将要发生的竖直变化量. 移动的y距离
        // duration : 数据模拟持续的时长

        // 1. 开始平滑的数据模拟,这只是第一步，还不行执行动画，还得重写computeScroll（）方法
        int duration = Math.abs(dx * 2); // 0 -> 1200
        scroller.startScroll(startX, 0, dx, 0, duration);

        // 重绘界面 -> drawChild() -> computeScroll();(会执行这些方法)，一定要加上这个
        invalidate();
    }

    //2. 维持动画的继续
    @Override
    public void computeScroll() {
        super.computeScroll();
        if(scroller.computeScrollOffset()){ // 直到duration事件以后, 结束
            // true, 动画还没有结束，500之内为true，超过500毫秒是false
            // 获取当前模拟的数据, 也就是要滚动到的位置
            int currX = scroller.getCurrX();
            System.out.println("currX: " + currX);
            scrollTo(currX, 0); // 滚过去

            invalidate(); // 重绘界面-> drawChild() -> computeScroll();循环，一定要这样书写，相当于一针一针地绘制
        }
    }
    public void open(){
        currentState = MENU_STATE;
        updateCurrentContent();
    }
    public void close(){
        currentState = MAIN_STATE;
        updateCurrentContent();
    }

    public void switchState(){
        if(currentState == MAIN_STATE){
            open();
        }else {
            close();
        }
    }
}
