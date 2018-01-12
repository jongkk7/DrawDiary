package com.nainfox.drawview.draw;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.os.Parcelable;
import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.nainfox.drawview.R;
import com.nainfox.drawview.data.HistoryPath;
import com.nainfox.drawview.data.Point;
import com.nainfox.drawview.data.ResizeBehaviour;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by yjk on 2018. 1. 2..
 */

public class DrawView extends View implements View.OnTouchListener {
    private static final String TAG ="### DrawView ###";

    private static final float DEFAULT_STROKE_WIDTH = 4;
    private static final int DEFAULT_COLOR = Color.parseColor("#FF000000");
    private static final int DEFAULT_ALPHA = 255;

    private Paint currentPaint;
    private Paint penPaint, crayonPaint, eraserPaint;
    private Path currentPath;

    private Bitmap oldBitmap; // 예전 그림

    private ResizeBehaviour resizeBehaviour;

    private ArrayList<Point> points = new ArrayList<>();
    private ArrayList<HistoryPath> paths = new ArrayList<>();
    private ArrayList<HistoryPath> cancelPaths = new ArrayList<>();

    private int paintStyle = 1; // 1: pen , 2: crayon , 3: eraser
    private int paintColor = DEFAULT_COLOR;
    private int paintAlpha = DEFAULT_ALPHA;
    private Bitmap bitmap; // 크래파스 이미지
    private BitmapDrawable bitmapDrawable;

    private int lastDimensionW = -1;
    private int lastDimensionH = -1;

    private boolean finishPath = false;

    private PathDrawnListener mPathDrawnListener;
    private PathRedoUndoCountChangeListener mPathRedoUndoCountChangeListener;

    public DrawView(Context context) {
        this(context, null);
    }
    public DrawView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public DrawView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setOnTouchListener(this);
        initPaints();
    }

    // 종료 전 인스턴스 저장
    @Override
    protected Parcelable onSaveInstanceState() {

        // Get the superclass parcelable state
        Parcelable superState = super.onSaveInstanceState();

        if (points.size() > 0) {// Currently doing a line, save it's current path
            createHistoryPathFromPoints();
        }

        return new DrawSavedState(superState, paths, cancelPaths,
                getPaintWidth(), getPaintColor(), getPaintAlpha(),
                getResizeBehaviour(), lastDimensionW, lastDimensionH);
    }

    // 다시 시작 시 인스턴스 불러오기
    @Override
    protected void onRestoreInstanceState(Parcelable state) {

        // If not instance of my state, let the superclass handle it
        if (!(state instanceof DrawSavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        DrawSavedState savedState = (DrawSavedState) state;
        // Superclass restore state
        super.onRestoreInstanceState(savedState.getSuperState());

        // My state restore
        paths = savedState.getPaths();
        cancelPaths = savedState.getCanceledPaths();
        currentPaint = savedState.getCurrentPaint();

        setPaintWidthPx(savedState.getCurrentPaintWidth());
        setPaintColor(savedState.getPaintColor());
        setPaintAlpha(savedState.getPaintAlpha());

        setResizeBehaviour(savedState.getResizeBehaviour());

        // Restore the last dimensions, so that in onSizeChanged i can calculate the
        // height and width change factor and multiply every point x or y to it, so that if the
        // View is resized, it adapt automatically it's points to the new width/height
        lastDimensionW = savedState.getLastDimensionW();
        lastDimensionH = savedState.getLastDimensionH();

        notifyRedoUndoCountChanged();
    }

    // paint style
    public void setPaintStyle(int style){
        paintStyle = style;
        if(style == 3){
            setPaintColor(Color.WHITE);
        }
    }
    // paint color
    public void setPaintColor(@ColorInt int color) {

        invalidate();

        paintColor = color;

        currentPaint.setColor(paintColor);
        currentPaint.setAlpha(paintAlpha); // Restore the previous alpha
    }
    public int getPaintColor(){ return paintColor; }
    public int getPaintColorWithAlpha(){ return currentPaint.getColor(); }

    // paint width
    public void setPaintWidthPx(@FloatRange(from = 0) float widthPx){
        if(widthPx > 0){
            invalidate();
            currentPaint.setStrokeWidth(widthPx);
            crayonPaint.setStrokeWidth(widthPx);
        }
    }
    public void setPaintWidthDp(float dp){
        setPaintWidthPx(DrawHelper.convertDpToPixels(dp));
    }
    public float getPaintWidth(){
        return currentPaint.getStrokeWidth();
    }

    // paint alpha
    public void setPaintAlpha(@IntRange(from = 0, to = 255) int alpha){
        invalidate();

        paintAlpha = alpha;
        currentPaint.setAlpha(paintAlpha);
    }
    public int getPaintAlpha(){
        return paintAlpha;
    }

    // resize view
    public void setResizeBehaviour(ResizeBehaviour newBehavior){
        resizeBehaviour = newBehavior;
    }
    public ResizeBehaviour getResizeBehaviour(){
        return resizeBehaviour;
    }


    // redo , undo
    public void undoLast(){
        if(paths.size() > 0){
            finishPath = true;
            invalidate();

            cancelPaths.add(paths.get(paths.size()-1));
            paths.remove(paths.size()-1);
            invalidate();

            notifyRedoUndoCountChanged();
        }
    }
    public void redoLast(){
        if(cancelPaths.size() > 0){
            paths.add(cancelPaths.get(cancelPaths.size() - 1));
            cancelPaths.remove(cancelPaths.size()-1);
            invalidate();

            notifyRedoUndoCountChanged();
        }
    }

    public void undoAll(){
        Collections.reverse(paths);
        cancelPaths.addAll(paths);
        paths = new ArrayList<>();
        invalidate();

        notifyRedoUndoCountChanged();
    }
    public void redoAll(){
        if(cancelPaths.size() > 0){
            paths.addAll(cancelPaths);
            cancelPaths = new ArrayList<>();
            invalidate();

            notifyRedoUndoCountChanged();
        }
    }

    // undo size
    public int getUndoCount(){
        return paths.size();
    }

    // redo size
    public int getRedoCount(){
        return paths.size();
    }

    // total count
    public int getPathCount(boolean drawingPath){
        int size = paths.size();

        if(drawingPath && paths.size()>0){
            size++;
        }
        return size;
    }


    // listener
    public void setOnPathDrawnListener(PathDrawnListener listener){
        mPathDrawnListener = listener;
    }
    public void removePathDrawnListener() {
        mPathDrawnListener = null;
    }
    public void setPathRedoUndoCountChangeListener(PathRedoUndoCountChangeListener listener) {
        mPathRedoUndoCountChangeListener = listener;
    }
    public void removePathRedoUndoCountChangeListener() {
        mPathRedoUndoCountChangeListener = null;
    }


    public void clearAll(){
        clearDraw();
        clearHistory();
    }
    private void clearDraw() {
        points = new ArrayList<>();
        paths = new ArrayList<>();

        notifyRedoUndoCountChanged();

        invalidate();
    }
    private void clearHistory() {
        cancelPaths = new ArrayList<>();

        notifyRedoUndoCountChanged();

        invalidate();
    }

    // State
    public DrawSerializableState getCurrentViewStateAsSerializable(){
        return new DrawSerializableState(cancelPaths, paths, getPaintColor(), getPaintAlpha(),
                getPaintWidth(), getResizeBehaviour(), lastDimensionW, lastDimensionH);
    }
    public void restoreStateFromSerializable(DrawSerializableState state) {
        if (state != null) {
            if (state.getCanceledPaths() != null) {
                cancelPaths = state.getCanceledPaths();
            }

            if (state.getPaths() != null) {
                paths = state.getPaths();
            }

            paintColor = state.getPaintColor();
            paintAlpha = state.getPaintAlpha();

            currentPaint.setColor(state.getPaintColor());
            currentPaint.setAlpha(state.getPaintAlpha());
            setPaintWidthPx(state.getPaintWidth());

            resizeBehaviour = state.getResizeBehaviour();

            if (state.getLastDimensionW() >= 0) {
                lastDimensionW = state.getLastDimensionW();
            }

            if (state.getLastDimensionH() >= 0) {
                lastDimensionH = state.getLastDimensionH();
            }

            notifyRedoUndoCountChanged();
            invalidate();
        }
    }


    // Internal methods
    private void notifyPathStart() {
        if (mPathDrawnListener != null) {
            mPathDrawnListener.onPathStart();
        }
    }
    private void notifyPathDrawn() {
        if (mPathDrawnListener != null) {
            mPathDrawnListener.onNewPathDrawn();
        }
    }
    private void notifyRedoUndoCountChanged() {
        if (mPathRedoUndoCountChangeListener != null) {
            mPathRedoUndoCountChangeListener.onRedoCountChanged(getRedoCount());
            mPathRedoUndoCountChangeListener.onUndoCountChanged(getUndoCount());
        }
    }

    // 예전 그림 저장
    public void setOldBitmap(Bitmap bitmap){
        oldBitmap = bitmap;
    }

    private void initPaints(){
        currentPaint = DrawHelper.createPaint();

        currentPaint.setColor(DEFAULT_COLOR);
        currentPaint.setAlpha(DEFAULT_ALPHA);
        currentPaint.setStrokeWidth(DEFAULT_STROKE_WIDTH);
        DrawHelper.setupStrokePaint(currentPaint);


        crayonPaint = new Paint();
        ColorFilter filter = new PorterDuffColorFilter(DEFAULT_COLOR, PorterDuff.Mode.SRC_IN);
        crayonPaint.setColorFilter(filter);
        crayonPaint.setStrokeWidth(DEFAULT_STROKE_WIDTH);
        bitmapDrawable = (BitmapDrawable) getResources().getDrawable(R.drawable.aa);
        bitmap = bitmapDrawable.getBitmap();

    }



    @Override
    protected void onDraw(Canvas canvas) {
        if(paths.size() == 0 && points.size() == 0) return;

        final boolean finishedPath = finishPath;
        finishPath = false;


        // 예전 그림 그려주기
        if(oldBitmap != null){
            canvas.drawBitmap(oldBitmap, 0, 0, currentPaint);
        }

        for(HistoryPath currentPath : paths){
            if(currentPath.getPaintStyle() == 2){
                bitmap = Bitmap.createScaledBitmap(bitmap, (int)currentPath.getPaintWidth(), (int)currentPath.getPaintWidth(), false);
            }
            if(currentPath.isPoint()){
                if(currentPath.getPaintStyle() == 1) {
                    canvas.drawCircle(currentPath.getOriginX(), currentPath.getOriginY(),
                            currentPath.getPaint().getStrokeWidth() / 2, currentPath.getPaint());
                }else if (currentPath.getPaintStyle() == 2) {
                    for (int i = 0; i < currentPath.getPoints().size(); i++) {
                        canvas.drawBitmap(bitmap, currentPath.getPoints().get(i).x, currentPath.getPoints().get(i).y, currentPath.getPaint());
                    }
                }else if (currentPath.getPaintStyle() == 3){
                    canvas.drawCircle(currentPath.getOriginX(), currentPath.getOriginY(),
                            currentPath.getPaint().getStrokeWidth() / 2, currentPath.getPaint());
                }
            } else {// Else draw the complete path
                if(currentPath.getPaintStyle() == 1) {
                    canvas.drawPath(currentPath.getPath(), currentPath.getPaint());
                }else if(currentPath.getPaintStyle() == 2) {
                    for (int i = 0; i < currentPath.getPoints().size(); i++) {
                        canvas.drawBitmap(bitmap, currentPath.getPoints().get(i).x, currentPath.getPoints().get(i).y, currentPath.getPaint());
                    }
                }else if (currentPath.getPaintStyle() == 3){
                    canvas.drawPath(currentPath.getPath(), currentPath.getPaint());
                }
            }
        }

        if(currentPath == null){
            currentPath = new Path();
        }else{
            currentPath.rewind();
        }

        ColorFilter filter = new PorterDuffColorFilter(paintColor, PorterDuff.Mode.SRC_IN);
        crayonPaint.setColorFilter(filter);
        crayonPaint.setStrokeWidth(getPaintWidth());
        bitmap = Bitmap.createScaledBitmap(bitmap, (int)getPaintWidth(), (int)getPaintWidth(), false);



        if(points.size() == 1 || DrawHelper.isAPoint(points)){
            if(paintStyle == 1){
                canvas.drawCircle(points.get(0).x, points.get(0).y,
                        currentPaint.getStrokeWidth()/2,
                        createAndCopyColorAndAlphaForFillPaint(currentPaint, false));
            }else if(paintStyle == 2) {
                canvas.drawBitmap(bitmap, points.get(0).x, points.get(0).y, crayonPaint);
            }else if(paintStyle == 3){
                canvas.drawCircle(points.get(0).x, points.get(0).y,
                        currentPaint.getStrokeWidth()/2,
                        createAndCopyColorAndAlphaForFillPaint(currentPaint, false));
            }
        }else if(points.size() != 0){
            boolean first = true;

            for (Point point : points){
                if(first){
                    currentPath.moveTo(point.x, point.y);
                    first = false;
                }else{
                    if(paintStyle == 1){
                        currentPath.lineTo(point.x, point.y);
                    }else if(paintStyle == 2){
                        canvas.drawBitmap(bitmap, point.x, point.y, crayonPaint);
                    }else if(paintStyle == 3){
                        currentPath.lineTo(point.x, point.y);
                    }
                }

            }

            canvas.drawPath(currentPath, currentPaint);
        }

        if(finishedPath && points.size() > 0){
            createHistoryPathFromPoints();
        }
    }

    private Paint createAndCopyColorAndAlphaForFillPaint(Paint from, boolean copyWidth) {
        Paint paint = DrawHelper.createPaint();
        DrawHelper.setupFillPaint(paint);
        paint.setColor(from.getColor());
        paint.setAlpha(from.getAlpha());
        if (copyWidth) {
            paint.setStrokeWidth(from.getStrokeWidth());
        }
        return paint;
    }
    private void createHistoryPathFromPoints() {
        HistoryPath historyPath = new HistoryPath(points, new Paint(currentPaint));;
        switch (paintStyle){
            case 1:
                historyPath = new HistoryPath(points, new Paint(currentPaint));
                break;
            case 2:
                historyPath = new HistoryPath(points, new Paint(crayonPaint), paintStyle);
                break;
            case 3:

                break;

        }
        historyPath.setPaintStyle(paintStyle);
        paths.add(historyPath);

        points = new ArrayList<>();

        notifyPathDrawn();
        notifyRedoUndoCountChanged();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            notifyPathStart();
        }
        if (getParent() != null) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }

        // Clear all the history when restarting to draw
        cancelPaths = new ArrayList<>();

        if ((motionEvent.getAction() != MotionEvent.ACTION_UP) &&
                (motionEvent.getAction() != MotionEvent.ACTION_CANCEL)) {
            Point point;
            for (int i = 0; i < motionEvent.getHistorySize(); i++) {
                point = new Point();
                point.x = motionEvent.getHistoricalX(i);
                point.y = motionEvent.getHistoricalY(i);
                points.add(point);
            }
            point = new Point();
            point.x = motionEvent.getX();
            point.y = motionEvent.getY();
            points.add(point);

            finishPath = false;
        } else
            finishPath = true;

        invalidate();
        return true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        float xMultiplyFactor = 1;
        float yMultiplyFactor = 1;


        if (lastDimensionW == -1) {
            lastDimensionW = w;
        }

        if (lastDimensionH == -1) {
            lastDimensionH = h;
        }

        if (w >= 0 && w != oldw && w != lastDimensionW) {
            xMultiplyFactor = (float) w / lastDimensionW;
            lastDimensionW = w;
        }

        if (h >= 0 && h != oldh && h != lastDimensionH) {
            yMultiplyFactor = (float) h / lastDimensionH;
            lastDimensionH = h;
        }

        multiplyPathsAndPoints(xMultiplyFactor, yMultiplyFactor);
    }

    @SuppressWarnings("SuspiciousNameCombination")
    private void multiplyPathsAndPoints(float xMultiplyFactor, float yMultiplyFactor) {

        // If both factors == 1 or <= 0 or no paths/points to apply things, just return
        if ((xMultiplyFactor == 1 && yMultiplyFactor == 1)
                || (xMultiplyFactor <= 0 || yMultiplyFactor <= 0) ||
                (paths.size() == 0 && cancelPaths.size() == 0 && points.size() == 0)) {
            return;
        }

        if (resizeBehaviour == ResizeBehaviour.CLEAR) {// If clear, clear all and return
            paths = new ArrayList<>();
            cancelPaths = new ArrayList<>();
            points = new ArrayList<>();
            return;
        } else if (resizeBehaviour == ResizeBehaviour.CROP) {
            xMultiplyFactor = yMultiplyFactor = 1;
        }

        // Adapt drawn paths
        for (HistoryPath historyPath : paths) {

            if (historyPath.isPoint()) {
                historyPath.setOriginX(historyPath.getOriginX() * xMultiplyFactor);
                historyPath.setOriginY(historyPath.getOriginY() * yMultiplyFactor);
            } else {
                for (Point point : historyPath.getPoints()) {
                    point.x *= xMultiplyFactor;
                    point.y *= yMultiplyFactor;
                }
            }

            historyPath.generatePath();
        }

        // Adapt canceled paths
        for (HistoryPath historyPath : cancelPaths) {

            if (historyPath.isPoint()) {
                historyPath.setOriginX(historyPath.getOriginX() * xMultiplyFactor);
                historyPath.setOriginY(historyPath.getOriginY() * yMultiplyFactor);
            } else {
                for (Point point : historyPath.getPoints()) {
                    point.x *= xMultiplyFactor;
                    point.y *= yMultiplyFactor;
                }
            }

            historyPath.generatePath();
        }

        // Adapt drawn points
        for (Point point : points) {
            point.x *= xMultiplyFactor;
            point.y *= yMultiplyFactor;
        }
    }
}
