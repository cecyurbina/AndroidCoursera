package com.example.root.modernartui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;


public class MainActivity extends ActionBarActivity {
    private CanvasView canvas;
    int[] all_fibonacci = {377,233,144,89,55,34,21,13, 8, 5, 3, 2, 1, 1};
    ArrayList<Integer> fibonacci_list =  new ArrayList<Integer>();
    ArrayList<Integer> sub_fibonacci = new ArrayList<Integer>();
    int num_rectangles = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LinearLayout canvasLayout = (LinearLayout)findViewById(R.id.canvas);
        SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);
        canvas = new CanvasView(this);
        canvasLayout.addView(canvas);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                int stepSize = 7;
                progress = ((int)Math.round(progress/stepSize))*stepSize;
                seekBar.setProgress(progress);
                canvas.invalidate();

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }


    class CanvasView extends View {
        int heightMax;
        int lenSquare;
        Canvas mCanvas;

        public CanvasView(Context context) {
            super(context);
        }

        protected void onDraw(Canvas canvas) {
            mCanvas = canvas;
            heightMax = canvas.getHeight();
            get_sublist(num_rectangles);
            calculatePoints();
        }

        protected void calculatePoints(){

            lenSquare = (int) Math.ceil(heightMax/sub_fibonacci.get(0));
            Paint paint =new Paint();
            paint.setStyle(Paint.Style.FILL);
            String nextDirection = null;
            int past_x_origin = 0;
            int past_y_origin = 0;
            int past_len = 0;
            int x_origin = 0;
            int y_origin = 0;
            int lenRectangle;
            for (int item : sub_fibonacci) {
                Random rnd = new Random();
                System.out.println("Count is: " + item);
                lenRectangle = lenSquare * item;
                if (sub_fibonacci.get(0) == item){
                    paint.setARGB(200, rnd.nextInt(256), rnd.nextInt(256), 255);
                    x_origin = 0;
                    y_origin = 0;
                    mCanvas.drawRect(x_origin, y_origin, lenRectangle, lenRectangle, paint);
                    nextDirection = "right";
                }
                else {
                    if (nextDirection.equals("right")){
                        paint.setARGB(220, 255, rnd.nextInt(256), rnd.nextInt(256));
                        nextDirection = "bottom";
                        x_origin = past_x_origin+past_len;
                        y_origin = past_y_origin;
                        mCanvas.drawRect(x_origin, y_origin,
                                    (past_len+past_x_origin)+(lenRectangle),
                                    (lenRectangle)+(past_y_origin), paint);
                    }
                    else if(nextDirection.equals("bottom")){
                        paint.setARGB(255, rnd.nextInt(256), rnd.nextInt(256), 255);
                        nextDirection = "left";
                        x_origin = past_x_origin+past_len-lenRectangle;
                        y_origin = past_y_origin+past_len;
                        mCanvas.drawRect(x_origin, y_origin,
                                past_x_origin+past_len,
                                past_y_origin+past_len+(lenRectangle), paint);
                    }
                    else if(nextDirection.equals("left")){
                        paint.setARGB(255, 255, 255, 255);
                        nextDirection = "up";
                        x_origin = past_x_origin-lenRectangle;
                        y_origin =  past_y_origin+past_len-lenRectangle;
                        mCanvas.drawRect(x_origin, y_origin,
                                past_x_origin,
                                past_y_origin+past_len, paint);
                    }
                    else if(nextDirection.equals("up")) {
                        paint.setARGB(255, 255, rnd.nextInt(256), rnd.nextInt(256));
                        nextDirection = "right";
                        x_origin = past_x_origin;
                        y_origin =  past_y_origin-(lenRectangle);
                        mCanvas.drawRect(x_origin, y_origin,
                                past_x_origin+(lenRectangle),
                                past_y_origin, paint);
                    }
                }
                past_x_origin = x_origin;
                past_y_origin = y_origin;
                past_len = lenRectangle;
            }

        }

        public ArrayList<Integer> get_sublist(int limit){
            for (int item : all_fibonacci) {
                fibonacci_list.add(item);
            }
            sub_fibonacci = new ArrayList<Integer>(fibonacci_list.subList(all_fibonacci.length-limit, all_fibonacci.length));
            return sub_fibonacci;
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            AlertDialog.Builder adb = new AlertDialog.Builder(this);


            adb.setTitle("Visit moma");


            adb.setIcon(android.R.drawable.ic_dialog_alert);


            adb.setPositiveButton("Visit MOMA", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    String url = "http://www.moma.org";
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                } });


            adb.setNegativeButton("Not now", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    return;
                } });
            adb.show();
            return true;
        }
        if (id == R.id.add){
            if (num_rectangles < 14) {
                num_rectangles = num_rectangles + 1;
            }
            canvas.invalidate();
        }
        if (id == R.id.remove){
            if (num_rectangles > 5) {
                num_rectangles = num_rectangles - 1;
            }
            canvas.invalidate();
        }

        return super.onOptionsItemSelected(item);
    }
}
