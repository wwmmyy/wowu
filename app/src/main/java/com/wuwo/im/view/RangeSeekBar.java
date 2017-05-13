package com.wuwo.im.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.wuwo.im.util.LogUtils;
import im.imxianzhi.com.imxianzhi.R;

import java.util.List;
import java.util.Locale;
import java.util.Vector;

public class RangeSeekBar extends View {

	private static final String TAG = "RangeSeekBar";
	
	public static final int HORIZONTAL = 0;
	public static final int VERTICAL = 1;
	
	private static final int DEFAULT_THUMBS = 3;
	private static final int DEFAULT_THUMB_WIDTH = 20;
	private static final int DEFAULT_THUMB_HEIGHT = 20;
	private static final float DEFAULT_STEP = 0.5f;
	
	private RangeSeekBarListener listener;
	
	private List<Thumb> thumbs;
	private float thumbWidth;
	private float thumbHeight;
	private float thumbHalf;
	private float pixelRangeMin;
	private float pixelRangeMax;
	private int orientation;
	private boolean limitThumbRange;
	private int viewWidth;
	private int viewHeight;
	private float scaleRangeMin;
	private float scaleRangeMax;
	private float scaleStep;
	
	private Drawable track;
	private Drawable range;
	private Drawable thumb;
	
	private boolean firstRun;
	private boolean isSeeking;
	
	private void initDefaults() {
		orientation = HORIZONTAL;
		limitThumbRange = true;
		scaleRangeMin = 0;
		scaleRangeMax = 100;
		scaleStep = DEFAULT_STEP;
		
		viewWidth = 0;
		viewHeight = 0;
		
		thumbWidth = DEFAULT_THUMB_WIDTH;
		thumbHeight = DEFAULT_THUMB_HEIGHT;

		thumbs = new Vector<Thumb>();
		
		this.setFocusable(true);
		this.setFocusableInTouchMode(true);
		
		this.setBackgroundDrawable(getResources().getDrawable(R.drawable.rangeseekbar));
//		this.setBackgroundColor(getResources().getColor(R.color.white));
//		this.setBackgroundColor(getResources().getColor(R.color.text_unselected));

		thumb = getResources().getDrawable(R.drawable.rangeseekbar_thumb);
        range = getResources().getDrawable(R.drawable.rangeseekbar_rangegradient);
        track = getResources().getDrawable(R.drawable.rangeseekbar_trackgradient);
//		track = getResources().getDrawable(R.drawable.rangeseekbar_rangegradient);
		firstRun = true;
		isSeeking = false;
	}
	
	
	public RangeSeekBar(Context context) {
		super(context);
		initDefaults();
		
		initThumbs(DEFAULT_THUMBS);
	}

	/**
     * Construct object, initializing with any attributes we understand from a
     * layout file. These attributes are defined in
     * SDK/assets/res/any/classes.xml.
     * 
     * @see View#View(Context, AttributeSet)
     */
    public RangeSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initDefaults();

        // Obtain our styled custom attributes from xml
        TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.RangeSeekBar);
        
        
        String s = a.getString(R.styleable.RangeSeekBar_orientation);
        if(s != null)
        	orientation = s.toLowerCase(Locale.ENGLISH).contains("vertical") ? VERTICAL : HORIZONTAL;
        
        limitThumbRange = a.getBoolean(R.styleable.RangeSeekBar_limitThumbRange, true);
        
        scaleRangeMin = a.getFloat(R.styleable.RangeSeekBar_scaleMin, 0);
        scaleRangeMax = a.getFloat(R.styleable.RangeSeekBar_scaleMax, 100);
        scaleStep = Math.abs(a.getFloat(R.styleable.RangeSeekBar_scaleStep, DEFAULT_STEP));
        
        Drawable aThumb = a.getDrawable(R.styleable.RangeSeekBar_thumb);
        if(aThumb != null)
        	thumb = aThumb;
        
        Drawable aRange = a.getDrawable(R.styleable.RangeSeekBar_range);
        if(aRange != null)
        	range = aRange;
        
        Drawable aTrack = a.getDrawable(R.styleable.RangeSeekBar_strack);
        if(aTrack != null)
        	track = aTrack;
        
        // Register desired amount of thumbs
        int noThumbs = a.getInt(R.styleable.RangeSeekBar_thumbs, DEFAULT_THUMBS);
        thumbWidth = a.getDimension(R.styleable.RangeSeekBar_thumbWidth, DEFAULT_THUMB_WIDTH);
		thumbHeight = a.getDimension(R.styleable.RangeSeekBar_thumbHeight, DEFAULT_THUMB_HEIGHT);
		
		initThumbs(noThumbs);
        
        a.recycle();
    }

	/**
	 * {@inheritDoc}
     * @see View#measure(int, int)
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    	super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    	viewWidth = measureWidth(widthMeasureSpec);
    	viewHeight = measureHeight(heightMeasureSpec);
        setMeasuredDimension(viewWidth,viewHeight);


		//LogUtils.i(TAG,"viewWidth ：：："+viewWidth);
        // 
        thumbHalf = (orientation == VERTICAL) ? (thumbHeight/2) : (thumbWidth/2);
//    	pixelRangeMin = 0 + thumbHalf;
		pixelRangeMin = 0 ;
    	pixelRangeMax = (orientation == VERTICAL) ? viewHeight : viewWidth;
    	//pixelRangeMax -= thumbHalf;
    	
    	if(firstRun) {
            distributeThumbsEvenly();
            // Fire listener callback
    		if(listener != null)
    			listener.onCreate(this, currentThumb, getThumbValue(currentThumb));
    		firstRun = false;
    	}
    }
    
    /**
     * Draw
     * 
     * @see View#onDraw(Canvas)
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);	// 1. Make sure parent view get to draw it's components
        
        drawGutter(canvas);		// 2. Draw slider gutter
        drawRange(canvas);		// 3. Draw range in gutter
        drawThumbs(canvas);		// 4. Draw thumbs
        
    }
    
    private int currentThumb = 0;
    private float lowLimit = pixelRangeMin;
	private float highLimit = pixelRangeMax;
    /**
     *  {@inheritDoc}
     */
    @Override
    public boolean onTouchEvent (MotionEvent event) {
    	if(!thumbs.isEmpty()) {

    		float coordinate = (orientation == VERTICAL) ? event.getY() : event.getX();
    		int action = event.getAction();
			
    		// Find thumb closest to event coordinate on screen touch
	    	if(action == MotionEvent.ACTION_DOWN) {
	    		currentThumb = getClosestThumb(coordinate);
	    		//LogUtils.i(TAG,"Closest "+currentThumb);
	    		lowLimit = getLowerThumbRangeLimit(currentThumb);
	    		highLimit = getHigherThumbRangeLimit(currentThumb);
	    	}
	    	
	    	if(action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
	    		//
	    	}
				
			// Update thumb position
			// Make sure we stay in our tracks's bounds or limited by other thumbs
    		if(coordinate < lowLimit) {
    			if(lowLimit == highLimit && currentThumb >= thumbs.size()-1) {
    				currentThumb = getUnstuckFrom(currentThumb);
    				setThumbPos(currentThumb,coordinate);
    				lowLimit = getLowerThumbRangeLimit(currentThumb);
    	    		highLimit = getHigherThumbRangeLimit(currentThumb);
    			} else
    				setThumbPos(currentThumb,lowLimit);
				//LogUtils.i(TAG,"Setting low "+lowLimit);
    		} else if(coordinate > highLimit) {
				setThumbPos(currentThumb,highLimit);
				//LogUtils.i(TAG,"Setting high "+highLimit);
			} else {
				coordinate = asStep(coordinate);
				setThumbPos(currentThumb,coordinate);
				//LogUtils.i(TAG,"Setting coordinate "+coordinate);
			}

    		float thumbValue = getThumbValue(currentThumb);
    		
			// Fire listener callbacks
    		if(listener != null) {
    			
    			// Find thumb closest to event coordinate on screen touch
    	    	if(action == MotionEvent.ACTION_DOWN) {
    	    		listener.onSeekStart(this, currentThumb, thumbValue);
	    			isSeeking = true;
    	    	} else if(action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
    	    		listener.onSeekStop(this, currentThumb, thumbValue);
	    			isSeeking = false;
    	    	} else
	    			listener.onSeek(this, currentThumb, thumbValue);
    		}
    		// Tell the view we want a complete redraw
			//invalidate();
			
	    	// Tell the system we've handled this event
	    	return true;
    	}
    	return false;
    }
    
    private int getUnstuckFrom(int index) {
    	int unstuck = 0;
    	float lastVal = thumbs.get(index).val;
    	for(int i = index-1; i >= 0; i--) {
    		Thumb th = thumbs.get(i);
    		if(th.val != lastVal)
    			return i+1;
    	}
    	return unstuck;
    }
    
    private float asStep(float pixelValue) {
    	return stepScaleToPixel(pixelToStep(pixelValue));
    }
    
    private float pixelToScale(float pixelValue) {
		float pixelRange = (pixelRangeMax - pixelRangeMin);
		float scaleRange = (scaleRangeMax - scaleRangeMin);
		float scaleValue = (((pixelValue - pixelRangeMin) * scaleRange) / pixelRange) + scaleRangeMin;
		return scaleValue;
    }
    
    private float scaleToPixel(float scaleValue) {
		float pixelRange = (pixelRangeMax - pixelRangeMin);
		float scaleRange = (scaleRangeMax - scaleRangeMin);
		float pixelValue = (((scaleValue - scaleRangeMin) * pixelRange) / scaleRange) + pixelRangeMin;
		return pixelValue;
    }
    
    private float pixelToStep(float pixelValue) {
    	float stepScaleMin = 0;
    	float stepScaleMax = (float) Math.floor((scaleRangeMax-scaleRangeMin)/scaleStep);
		float pixelRange = (pixelRangeMax - pixelRangeMin);
		float stepScaleRange = (stepScaleMax - stepScaleMin);
		float stepScaleValue = (((pixelValue - pixelRangeMin) * stepScaleRange) / pixelRange) + stepScaleMin;
		//LogUtils.i(TAG,"scaleVal: "+stepScaleRange+" smin: "+stepScaleMin+" smax: "+stepScaleMax);
		return Math.round(stepScaleValue);
    }
    
    private float stepScaleToPixel(float stepScaleValue) {
    	float stepScaleMin = 0;
    	float stepScaleMax = (float) Math.floor((scaleRangeMax-scaleRangeMin)/scaleStep);
		float pixelRange = (pixelRangeMax - pixelRangeMin);
		float stepScaleRange = (stepScaleMax - stepScaleMin);
		float pixelValue = (((stepScaleValue - stepScaleMin) * pixelRange) / stepScaleRange) + pixelRangeMin;
		//LogUtils.i(TAG,"pixelVal: "+pixelValue+" smin: "+stepScaleMin+" smax: "+stepScaleMax);
		return pixelValue;
    }
    
    private void calculateThumbValue(int index) {
    	if(index < thumbs.size() && !thumbs.isEmpty()) {
    		Thumb th = thumbs.get(index);
    		th.val = pixelToScale(th.pos);
    	}
    }
    
    private void calculateThumbPos(int index) {
    	if(index < thumbs.size() && !thumbs.isEmpty()) {
    		Thumb th = thumbs.get(index);
    		th.pos = scaleToPixel(th.val);
    	}
    }

    private float getLowerThumbRangeLimit(int index) {
    	float limit = pixelRangeMin; 
    	if(limitThumbRange && index < thumbs.size() && !thumbs.isEmpty()) {
    		Thumb th = thumbs.get(index);
    		for(int i = 0; i < thumbs.size(); i++) {
	    		if(i < index) {
	    			Thumb tht = thumbs.get(i);
	    			if(tht.pos <= th.pos && tht.pos > limit) {
	    				limit = tht.pos;
	    				//LogUtils.i(TAG,"New low limit: "+limit+" i:"+i+" index: "+index);
	    			}
				}
	    	}
    	}
    	return limit;
    }

    private float getHigherThumbRangeLimit(int index) {
    	float limit = pixelRangeMax; 
    	if(limitThumbRange && index < thumbs.size() && !thumbs.isEmpty()) {
    		Thumb th = thumbs.get(index);
    		for(int i = 0; i < thumbs.size(); i++) {
	    		if(i > index) {
	    			Thumb tht = thumbs.get(i);
	    			if(tht.pos >= th.pos && tht.pos < limit) {
	    				limit = tht.pos;
	    				//LogUtils.i(TAG,"New high limit: "+limit+" i:"+i+" index: "+index);
	    			}
				}
	    	}
    	}
    	return limit;
    }
    
    public void distributeThumbsEvenly() {
    	if(!thumbs.isEmpty()) {
    		int noThumbs = thumbs.size();
    		float even = pixelRangeMax/noThumbs;
    		float lastPos = even/2;
    		for(int i = 0; i < thumbs.size(); i++) {
    			setThumbPos(i, asStep(lastPos));
    			//LogUtils.i(TAG,"lp: "+lastPos);
    			lastPos += even;
    		}
    	}
    }
    
    public float getThumbValue(int index) {
    	return thumbs.get(index).val;
    }
    
    public void setThumbValue(int index, float value) {
    	thumbs.get(index).val = value;
    	calculateThumbPos(index);
    	// Tell the view we want a complete redraw
		invalidate();
    }
    
    private void setThumbPos(int index, float pos) {
    	thumbs.get(index).pos = pos;
    	calculateThumbValue(index);
    	// Tell the view we want a complete redraw
		invalidate();
    }

	private int getClosestThumb(float coordinate) {
		int closest = 0;
		if(!thumbs.isEmpty()) {
			float shortestDistance = pixelRangeMax+thumbHalf+((orientation == VERTICAL) ? (getPaddingTop()+getPaddingBottom()) : (getPaddingLeft() + getPaddingRight()));
			// Oldschool for-loop to have access to index
			for(int i = 0; i < thumbs.size(); i++) {
	    		// Find thumb closest to x coordinate
				float tcoordinate = thumbs.get(i).pos;
				float distance = Math.abs(coordinate-tcoordinate);
				if(distance <= shortestDistance) {
					shortestDistance = distance;
					closest = i;
					//LogUtils.i(TAG,"shDist: "+shortestDistance+" thumb i: "+closest);
				}
	    	}
		}
		return closest;
	}
	
    private void drawGutter(Canvas canvas) {
    	if(track != null) {
//    		//LogUtils.i(TAG,"gutterbg: "+gutterBackground.toString());
    		Rect area1 = new Rect();
            area1.left = 0 + getPaddingLeft();
            area1.top = 0 + getPaddingTop();
            area1.right = getMeasuredWidth() - getPaddingRight();
            area1.bottom = getMeasuredHeight() - getPaddingBottom();
    		track.setBounds(area1);
    		track.draw(canvas);

			//LogUtils.i(TAG,"gutterbg: "+area1.left +":::"+area1.right);
    	}
    }
    
    /*
	RectF area = new RectF();
	area.left = 0 + getPaddingLeft() + minPos;
    area.top = 0 + getPaddingTop();
    area.right = getMeasuredWidth() - getPaddingRight() + maxPos;
    area.bottom = getMeasuredHeight() - getPaddingBottom();
    
	Paint p = new Paint();
	p.setAntiAlias(true);
    p.setColor(gutterColor);
    canvas.drawRoundRect(area, 7.5f, 7.5f, p);
    */
    
    private void drawRange(Canvas canvas) {
    	if(!thumbs.isEmpty()) {
	    	Thumb thLow = thumbs.get(getClosestThumb(0));
	    	Thumb thHigh = thumbs.get(getClosestThumb(pixelRangeMax));
	    	
	    	// If we only have 1 thumb - choose to draw from 0 in scale
	    	if(thumbs.size() == 1)
	    		thLow = new Thumb();
	    	//LogUtils.i(TAG,"l: "+thLow.pos+" h: "+thHigh.pos);
//			此处可以改变现实的初始值xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
//			thLow.pos=100;
//			thHigh.pos=300;
			int width=getMeasuredWidth() - getPaddingRight()-getPaddingLeft();
			thLow.pos=getCharacterLeft()*width;
			thHigh.pos=getCharacterRight()*width;


	    	if(range != null) {
	    		Rect area1 = new Rect();
	    		
	    		if(orientation == VERTICAL) {
	    			area1.left = 0 + getPaddingLeft();
		            area1.top = (int) thLow.pos;
		            area1.right = getMeasuredWidth() - getPaddingRight();
		            area1.bottom = (int) thHigh.pos;
	    		} else {
	    			area1.left = (int) thLow.pos;
//					area1.left = 2;
		            area1.top = 0 + getPaddingTop();
		            area1.right = (int) thHigh.pos;
		            area1.bottom = getMeasuredHeight() - getPaddingBottom();
	    		}
	    		range.setBounds(area1);
	    		range.draw(canvas);

				if(characterRight!=0.901f && currentCount<2){
					currentCount++;
					invalidate();
				}
	    	}
    	}
    }

	float characterLeft=0.05f;
	float characterRight=0.901f;
//	float characterLeft=0.5f;
//	float characterRight=0.5f;
	int currentCount=0;
	private float getCharacterLeft() {
		return characterLeft;
	}
	private float getCharacterRight() {
		return characterRight;
	}

	public void setCharacter(float characterLeft,float characterRight){
		this.characterLeft=characterLeft;
		this.characterRight=characterRight;
		this.invalidate();
	}




	private void drawThumbs(Canvas canvas) {
    	if(!thumbs.isEmpty()) {
    		//Paint p = new Paint();
    		
    		
    		for(Thumb th : thumbs) {
    			Rect area1 = new Rect();
    			//LogUtils.i(TAG,""+th.pos);
    			if(orientation == VERTICAL) {
    				area1.left = 0 + getPaddingLeft();
    	            area1.top = (int) ((th.pos - thumbHalf) + getPaddingTop());
    	            area1.right = getMeasuredWidth() - getPaddingRight();
    	            area1.bottom = (int) ((th.pos + thumbHalf) - getPaddingBottom());
    	            //LogUtils.i(TAG,"th: "+th.pos);
	    		} else {
	    			area1.left = (int) ((th.pos - thumbHalf) + getPaddingLeft());
    	            area1.top = 0 + getPaddingTop();
    	            area1.right = (int) ((th.pos + thumbHalf) - getPaddingRight());
    	            area1.bottom = getMeasuredHeight() - getPaddingBottom();
    	            //LogUtils.i(TAG,"th: "+area1.toString());
	    		}
	            
    			if(thumb != null) {
		    		thumb.setBounds(area1);
		    		thumb.draw(canvas);
    			}
        	}
    	}
    }
    

    /**
     * Determines the width of this view
     * @param measureSpec A measureSpec packed into an int
     * @return The width of the view, honoring constraints from measureSpec
     */
    private int measureWidth(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            // We were told how big to be
        	//LogUtils.i(TAG,"measureWidth() EXACTLY");
            result = specSize;
        } else {
            // Measure
        	//LogUtils.i(TAG,"measureWidth() not EXACTLY");
        	result = specSize + getPaddingLeft() + getPaddingRight();
            if (specMode == MeasureSpec.AT_MOST) {
                // Respect AT_MOST value if that was what is called for by measureSpec
            	//LogUtils.i(TAG,"measureWidth() AT_MOST");
                result = Math.min(result, specSize);
                // Add our thumbWidth to the equation if we're vertical
                if(orientation == VERTICAL) {
		            int h = (int) (thumbWidth+ getPaddingLeft() + getPaddingRight());
		            result = Math.min(result, h);
                }
            }
        }

        return result;
    }

    /**
     * Determines the height of this view
     * @param measureSpec A measureSpec packed into an int
     * @return The height of the view, honoring constraints from measureSpec
     */
    private int measureHeight(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            // We were told how big to be
        	//LogUtils.i(TAG,"measureHeight() EXACTLY");
            result = specSize;
        } else {
            // Measure
        	//LogUtils.i(TAG,"measureHeight() not EXACTLY");
        	result = specSize + getPaddingTop() + getPaddingBottom(); 
            if (specMode == MeasureSpec.AT_MOST) {
                // Respect AT_MOST value if that was what is called for by measureSpec
            	//LogUtils.i(TAG,"measureHeight() AT_MOST");
                result = Math.min(result, specSize);
                // Add our thumbHeight to the equation if we're horizontal
                if(orientation == HORIZONTAL) {
                	int h = (int) (thumbHeight+ getPaddingTop() + getPaddingBottom());
                    result = Math.min(result, h);
                }
            }
        }
        
        return result;
    }
    
    public class Thumb {
    	public float val;
    	public float pos;

    	public Thumb() {
    		val = 0;
    		pos = 0;
    	}
    }

    public interface RangeSeekBarListener {
    	public void onCreate(RangeSeekBar rangeSeekBar, int index, float value);
    	public void onSeek(RangeSeekBar rangeSeekBar, int index, float value);
    	public void onSeekStart(RangeSeekBar rangeSeekBar, int index, float value);
    	public void onSeekStop(RangeSeekBar rangeSeekBar, int index, float value);
    }
    
    public void setListener(RangeSeekBarListener listener) {
		this.listener = listener;
	}
    
	public int getOrientation() {
		return orientation;
	}

	public void setOrientation(int orientation) {
		this.orientation = orientation;
	}

	public float getThumbWidth() {
		return thumbWidth;
	}

	public void setThumbWidth(float thumbWidth) {
		this.thumbWidth = thumbWidth;
	}

	public float getThumbHeight() {
		return thumbHeight;
	}

	public void setThumbHeight(float thumbHeight) {
		this.thumbHeight = thumbHeight;
	}

	public boolean isLimitThumbRange() {
		return limitThumbRange;
	}

	public void setLimitThumbRange(boolean limitThumbRange) {
		this.limitThumbRange = limitThumbRange;
	}

	public float getScaleRangeMin() {
		return scaleRangeMin;
	}

	public void setScaleRangeMin(float scaleRangeMin) {
		this.scaleRangeMin = scaleRangeMin;
	}

	public float getScaleRangeMax() {
		return scaleRangeMax;
	}

	public void setScaleRangeMax(float scaleRangeMax) {
		this.scaleRangeMax = scaleRangeMax;
	}

	public float getScaleStep() {
		return scaleStep;
	}

	public void setScaleStep(float scaleStep) {
		this.scaleStep = scaleStep;
	}

	public Drawable getTrack() {
		return track;
	}

	public void setTrack(Drawable track) {
		this.track = track;
	}

	public Drawable getRange() {
		return range;
	}

	public void setRange(Drawable range) {
		this.range = range;
	}

	public Drawable getThumb() {
		return thumb;
	}

	public void setThumb(Drawable thumb) {
		this.thumb = thumb;
	}

	public void initThumbs(int noThumbs) {
		if(thumbs != null) {
			thumbs.clear();
	        for(int i = 0; i < noThumbs; i++) {
	        	Thumb th = new Thumb();
	        	thumbs.add(th);
	        }
		}
	}
	
	public boolean isSeeking() {
        return isSeeking;
	}
}
