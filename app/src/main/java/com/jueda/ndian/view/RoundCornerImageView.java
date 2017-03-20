package com.jueda.ndian.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.jueda.ndian.R;
import com.jueda.ndian.utils.LogUtil;

/**
 * 圆角
 */
public class RoundCornerImageView extends ImageView {
	private float angle;
	public RoundCornerImageView(Context context) {
		super(context);
		}
		public RoundCornerImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
			TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.RoundCornerImageView);
			angle=a.getFloat(R.styleable.RoundCornerImageView_Round, 15.0f);
			a.recycle();
		}
		public RoundCornerImageView(Context context, AttributeSet attrs,int defStyle) {
		super(context, attrs, defStyle);

		}
		@Override
		protected void onDraw(Canvas canvas) {

		Path clipPath = new Path();
		int w = this.getWidth();
		int h = this.getHeight();
			new LogUtil("=========",angle+"");
		clipPath.addRoundRect(new RectF(0, 0, w, h), angle, angle, Path.Direction.CW);
		canvas.clipPath(clipPath);
		super.onDraw(canvas);
		}


}