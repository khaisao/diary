package nv.module.brushdraw.ui.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Date;

public class HandWriteCanvasView extends View {
    public int width, height;
    private Bitmap bitmap;
    private Path path;
    private Canvas mCanvas;
    private Context context;
    private Paint paint;
    private float mX, mY;
    private static final float TOLERANCE = 4;
    private ArrayList<Path> pathArrayList;
    private ArrayList<Path> pathArrayListAll;
    private ArrayList<Path> undoPath;
    private ArrayList<Path> redoPath;
    private ArrayList<Integer> undoInk;
    private int currentLength = 0;
    private long startDrawTime = 0, currentDrawTime = 0;
    private ArrayList<Ink> inks;
    private Ink ink;
    private HandWriteCVCallback listener;

    public void setWriteCVCallback(HandWriteCVCallback listener) {
        this.listener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startTouch(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                moveTouch(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                upTouch();
                invalidate();
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        canvas.save();
        for (Path p : pathArrayList) {
            canvas.drawPath(p, paint);
        }

//        canvas.drawBitmap(bitmap, 0, 0, paint);
//        canvas.restore();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        try {
            if (w > 0 && h > 0) {
                bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
                mCanvas = new Canvas(bitmap);
            }
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
    }

    public void setColor(int color){
        paint.setColor(color);
    }

    public HandWriteCanvasView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.context = context;
        path = new Path();
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(10f);
        pathArrayList = new ArrayList<>();
        pathArrayListAll = new ArrayList<>();
        undoPath = new ArrayList<>();
        undoInk = new ArrayList<>();
        inks = new ArrayList<>();
    }

    public void init(DisplayMetrics metrics) {
        int height = metrics.heightPixels;
        int width = metrics.widthPixels;

        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(bitmap);

    }

    private void startTouch(float x, float y) {
        ink = new Ink();
        path = new Path();
        pathArrayList.add(path);
        pathArrayListAll.add(path);
        if (listener != null)
            listener.onUndo(true);

        path.reset();

        path.moveTo(x, y);
        mX = x;
        mY = y;
        ink.addPoint(x, y);

        currentLength = 1;
        if (startDrawTime == 0) {
            startDrawTime = new Date().getTime();
            ink.addTime(1F);
        } else {
            currentDrawTime = new Date().getTime();
            ink.addTime(currentDrawTime - startDrawTime);
        }
        undoPath.clear();
    }

    private void moveTouch(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);

        if (dx >= TOLERANCE || dy >= TOLERANCE) {
            path.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
        currentDrawTime = new Date().getTime();
        ink.addPoint(x, y);
        currentLength++;
        ink.addTime(currentDrawTime - startDrawTime);
    }

    public void clearCanvas() {
        path.reset();
        pathArrayList.clear();
        pathArrayListAll.clear();
        undoInk.clear();
        invalidate();
        if (ink != null) ink.clear();
        if (inks != null) inks.clear();
    }

    private void upTouch() {
        inks.add(ink);
        undoInk.add(currentLength);
        path.lineTo(mX, mY);


    }

    private int number = 0;
    public void undo() {
        if (inks.size() > 0) {
            if (pathArrayList.size() > 0) {
                undoPath.add(pathArrayList.remove(pathArrayList.size() - 1));
                number = pathArrayList.size();
                invalidate();
                listener.onUndo(true);
                listener.onRedo(true);

            }

            if ( pathArrayList.size()  == 0){
                    listener.onUndo(false);
            }
        }
    }

    public void reDo() {
        if (number < pathArrayListAll.size()) {
            pathArrayList.add(number, pathArrayListAll.get(number));
            number++;
            invalidate();
            if (listener != null)
                listener.onRedo(true);
        }

        if (number == pathArrayListAll.size()) {
            listener.onRedo(false);
            listener.onUndo(true);
        }
    }
}
