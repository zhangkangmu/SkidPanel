package com.hong.zyh.skidpanel;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.hong.zyh.skidpanel.ui.SlideMenu;

public class MainActivity extends Activity implements View.OnClickListener {

    private ImageButton ib_back;
    private SlideMenu slide_menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ib_back = findViewById(R.id.ib_back);
        //返回键设置的点击
        ib_back.setOnClickListener(this);
        slide_menu = findViewById(R.id.slide_menu);
    }

    @Override
    public void onClick(View v) {
        //执行的是自定义侧滑面板的id里的方法
        slide_menu.switchState();
    }
}
