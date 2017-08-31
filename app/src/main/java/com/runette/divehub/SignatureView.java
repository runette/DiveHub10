package com.runette.divehub;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class SignatureView extends View {
	
	

	  private static final float STROKE_WIDTH = 5f;

	  /** Need to track this so the dirty region can accommodate the stroke. **/
	  private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;

	  private Paint paint = new Paint();
	  private Path path = new Path();


	  /**   * Optimizes painting by invalidating the smallest possible area.   */
	  private float lastTouchX;
	  private float lastTouchY;
	  Signature signature ;
	  private final RectF dirtyRect = new RectF();

	  public SignatureView(Context context, AttributeSet attrs) {
	    super(context, attrs);
	    
	    paint.setAntiAlias(true);
	    paint.setColor(Color.WHITE);
	    paint.setStyle(Paint.Style.STROKE);
	    paint.setStrokeJoin(Paint.Join.ROUND);
	    paint.setStrokeWidth(STROKE_WIDTH);
	    

	    
	  }
	  
	  public SignatureView(Context contexts) {
		    super(contexts);
		    
		    paint.setAntiAlias(true);
		    paint.setColor(Color.WHITE);
		    paint.setStyle(Paint.Style.STROKE);
		    paint.setStrokeJoin(Paint.Join.ROUND);
		    paint.setStrokeWidth(STROKE_WIDTH);
		    

		    
		  }

	  /**   * Erases the signature.   */
	  public void clear() {
		signature.clear();
	    resetPath();
	    invalidate();
	  }
	  
	  public void setSignature(Signature input){

		  signature = input;
		  resetPath();
	  }
	  public void resetPath() {
		
			  path.set(signature.getPath());
		  
	  }
	  
	  @Override 
	  protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec){
		  int width = View.MeasureSpec.getSize(widthMeasureSpec);
		  int height = (int) (width*Signature.Y_SIZE/Signature.X_SIZE);
		  heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.getMode(heightMeasureSpec));	
		  super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	  }
	  
	  @Override
	  protected void onLayout (boolean changed, int left, int top, int right, int bottom) {
		  super.onLayout(changed, left, top, right, bottom);
		  if (signature != null ) {
			  signature.setSize(changed, left, top, right, bottom);
		  
			  if (changed) {
				  resetPath();
			  }
		  }
		  return;
	  }
	  

	  @Override
	  protected void onDraw(Canvas canvas) {
		  canvas.drawPath(path, paint);
	  }
	  
	  
	  public Signature getSignature() {
		  return signature;
	  }

	  @Override
	  public boolean onTouchEvent(MotionEvent event) {
	    float eventX = event.getX();
	    float eventY = event.getY();

	    

	    switch (event.getAction()) {
	      case MotionEvent.ACTION_DOWN:
	        path.moveTo(eventX, eventY);
	        lastTouchX = eventX;
	        lastTouchY = eventY;
	        signature.addPoint(eventX, eventY, Signature.LINE_START);
	        // There is no end point yet, so don't waste cycles invalidating.
	        return true;

	      case MotionEvent.ACTION_MOVE:
	      case MotionEvent.ACTION_UP:
	        // Start tracking the dirty region.
	        resetDirtyRect(eventX, eventY);

	        // When the hardware tracks events faster than they are delivered, the
	        // event will contain a history of those skipped points.
	        int historySize = event.getHistorySize();
	        for (int i = 0; i < historySize; i++) {
	          float historicalX = event.getHistoricalX(i);
	          float historicalY = event.getHistoricalY(i);
	          expandDirtyRect(historicalX, historicalY);
	          path.lineTo(historicalX, historicalY);
	          signature.addPoint(historicalX, historicalY, Signature.NOT_LINE_START);
	        }

	        // After replaying history, connect the line to the touch point.
	        path.lineTo(eventX, eventY);
	        signature.addPoint(eventX, eventY, Signature.NOT_LINE_START);
	        break;

	      default:
	        debug("Ignored touch event: " + event.toString());
	        return false;
	    }

	    // Include half the stroke width to avoid clipping.
	    invalidate(
	        (int) (dirtyRect.left - HALF_STROKE_WIDTH),
	        (int) (dirtyRect.top - HALF_STROKE_WIDTH),
	        (int) (dirtyRect.right + HALF_STROKE_WIDTH),
	        (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));
	    
	    lastTouchX = eventX;
	    lastTouchY = eventY;

	    return true;
	  }

	  private void debug(String string) {
		// TODO Auto-generated method stub
		
	}

	/**   * Called when replaying history to ensure the dirty region includes all   * points.   */
	  private void expandDirtyRect(float historicalX, float historicalY) {
	    if (historicalX < dirtyRect.left) {
	      dirtyRect.left = historicalX;
	    } else if (historicalX > dirtyRect.right) {
	      dirtyRect.right = historicalX;
	    }
	    if (historicalY < dirtyRect.top) {
	      dirtyRect.top = historicalY;
	    } else if (historicalY > dirtyRect.bottom) {
	      dirtyRect.bottom = historicalY;
	    }
	  }

	  /**   * Resets the dirty region when the motion event occurs.   */
	  private void resetDirtyRect(float eventX, float eventY) {

	    // The lastTouchX and lastTouchY were set when the ACTION_DOWN
	    // motion event occurred.
	    dirtyRect.left = Math.min(lastTouchX, eventX);
	    dirtyRect.right = Math.max(lastTouchX, eventX);
	    dirtyRect.top = Math.min(lastTouchY, eventY);
	    dirtyRect.bottom = Math.max(lastTouchY, eventY);
	  }}

