package nv.module.brushdraw.ui.customview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Stack;

import nv.module.brushdraw.R;


public class HandWriteOfflineCanvasView extends View {
    public int width;
    public int height;
    private Path mPath;
    private Stack<Path> mPathStack, mRedoStack;
    private Stack<InputStroke> mRedoStrokeStack;
    private Context context;
    private Paint mPaint;
    private float mX, mY;
    private static final float TOLERANCE = 5;
    private float startX, startY, lastX, lastY;
    private AddingStrokeListener mStrokeAddListener;
    private DataLoadedListener mDataLoadedListener;
    private ArrayList<InputStroke> mStrokeArrayList;
//    private ArrayList<ArrayList<ArrayList<Integer>>> mInk;


    public void setStrokeAddListener(AddingStrokeListener mStrokeAddListener) {
        this.mStrokeAddListener = mStrokeAddListener;
    }

    public void setDataLoadedListener(DataLoadedListener mDataLoadedListener) {
        this.mDataLoadedListener = mDataLoadedListener;
    }


    private InputStream databaseStream;
    private boolean isOpened = false;

    public HandWriteOfflineCanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initCanvas();
        setFocusable(false);
    }

    public void initCanvas() {
        // set a new Path
        mPath = new Path();
        mPathStack = new Stack<>();
        mRedoStack = new Stack<>();
        mRedoStrokeStack = new Stack<>();

        mStrokeArrayList = new ArrayList<>();
        // we set a new Paint with the desired attributes
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
    }

    @SuppressLint("StaticFieldLeak")
    public void openRecognizeFile() {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void[] params) {
                try {
                    databaseStream = context.getAssets().open("strokes-20100823.xml");
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
                return true;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                if (result != null) {
                    isOpened = result;
                    if (mDataLoadedListener != null)
                        mDataLoadedListener.onDataLoaded();
                }
            }
        }.execute();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // draw the mPath with the mPaint on the canvas when onDraw
        for (Path p : mPathStack)
            canvas.drawPath(p, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isOpened) {
            openRecognizeFile();
            return false;
        }
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
                upTouch(x, y);
                invalidate();
                break;
        }
        return true;
    }


    /********
     * On event listener
     *********************/
    protected void startTouch(float x, float y) {
        mPath = new Path();
        mPathStack.push(mPath);
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
        setStartCoordinate(x, y);
    }

    private void moveTouch(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOLERANCE || dy >= TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }

    private void upTouch(float xEvent, float yEvent) {
        mPath.lineTo(mX, mY);
        setEndCoordinate(xEvent, yEvent);

        InputStroke stroke = new InputStroke(startX, startY, lastX, lastY);
        mStrokeArrayList.add(stroke);
        mRedoStrokeStack.clear();
        mRedoStack.clear();
        if (mStrokeAddListener != null)
            mStrokeAddListener.onStrokeAddListener();
    }


    /********
     * Access canvas
     *********************/

    public void clearCanvas() {
        mPathStack.clear();
        mStrokeArrayList.clear();
        mRedoStrokeStack.clear();
        mRedoStack.clear();
//        if (mInk != null) {
//            mInk.clear();
//        }
        if (mStrokeAddListener != null)
            mStrokeAddListener.onStrokeAddListener();
        invalidate();
    }

    public void undoCanvas() {
        if (!mPathStack.empty()) {
            mRedoStack.push(mPathStack.pop());
            if (!mStrokeArrayList.isEmpty()) {
                int index = mStrokeArrayList.size() - 1;
                mRedoStrokeStack.push(mStrokeArrayList.get(index));
                mStrokeArrayList.remove(index);
            }
        }
        if (mStrokeAddListener != null)
            mStrokeAddListener.onStrokeAddListener();
        invalidate();
    }

    public void redoCanvas() {
        if (!mRedoStack.empty()) {
            mPathStack.push(mRedoStack.pop());
            if (!mRedoStrokeStack.empty()) {
                mStrokeArrayList.add(mRedoStrokeStack.pop());
            }
        }
        if(mStrokeAddListener != null)
            mStrokeAddListener.onStrokeAddListener();
        invalidate();
    }

    /************
     * Get infor
     *****************/


    public int getStroke() {
        if (mStrokeArrayList != null)
            return mStrokeArrayList.size();
        return 0;
    }

    public ArrayList<String> getKanjiResultList() {
        InputStroke[] inputStrokes = new InputStroke[mStrokeArrayList.size()];
        for (int i = 0; i < mStrokeArrayList.size(); i++) {
            inputStrokes[i] = mStrokeArrayList.get(i);
        }

        ArrayList<String> mKanjiResultList = new ArrayList<>(); //new String[mKanjiMatches.length];
        invalidate();
        return mKanjiResultList;
    }


    /************
     * Models
     *****************/

    public void setStartCoordinate(float startX, float startY) {
        this.startX = startX;
        this.startY = startY;
    }

    public void setEndCoordinate(float endX, float endY) {
        this.lastX = endX;
        this.lastY = endY;
    }


}