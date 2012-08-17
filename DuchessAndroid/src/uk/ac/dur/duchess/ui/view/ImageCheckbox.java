package uk.ac.dur.duchess.ui.view;

import uk.ac.dur.duchess.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.CompoundButton;

public class ImageCheckbox extends CompoundButton
{
	public final static int TOPRIGHT = 1;
	public final static int BOTTOMRIGHT = 2;
	
	private Paint paint;
	
	private Bitmap checkedImage;
	private Bitmap uncheckedImage;
	private int checkboxAnchor;
	
	public ImageCheckbox(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		checkedImage = BitmapFactory.decodeResource(getResources(), R.drawable.btn_check_on);
		uncheckedImage = BitmapFactory.decodeResource(getResources(), R.drawable.btn_check_on_disable);
		initAttr(context, attrs);
		paint = new Paint();
	}
	
	public ImageCheckbox(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		checkedImage = BitmapFactory.decodeResource(getResources(), R.drawable.btn_check_on);
		uncheckedImage = BitmapFactory.decodeResource(getResources(), R.drawable.btn_check_on_disable);
		initAttr(context, attrs);
		paint = new Paint();
	}

	private void initAttr(Context context, AttributeSet attrs)
	{
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ImageCheckbox);
		
		final int numberOfAttributes = a.getIndexCount();
		for (int i = 0; i < numberOfAttributes; i++)
		{
			int attr = a.getIndex(i);
			switch (attr)
			{
			case R.styleable.ImageCheckbox_checkboxAnchor:
				checkboxAnchor = a.getInt(attr, BOTTOMRIGHT);
				break;
			case R.styleable.ImageCheckbox_checkboxCheckedImage:
				int checkedImageResource = a.getResourceId(attr, R.drawable.btn_check_on);
				checkedImage = BitmapFactory.decodeResource(getResources(), checkedImageResource);
				break;
			case R.styleable.ImageCheckbox_checkboxUncheckedImage:
				int uncheckedImageResource = a.getResourceId(attr, R.drawable.btn_check_on_disable);
				uncheckedImage = BitmapFactory.decodeResource(getResources(), uncheckedImageResource);
				break;
			}
		}
		
		a.recycle();
	}
	
	@Override
	public void setChecked(boolean checked)
	{
		invalidate();
		super.setChecked(checked);
	}
	
	@Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
    }
	
	@Override
	protected void onDraw(Canvas canvas)
	{
		if (checkedImage == null || uncheckedImage == null) return;
		super.onDraw(canvas);
		
		Drawable topDrawable = getCompoundDrawables()[1];
		if (topDrawable == null) return;
		
		Rect rect = new Rect();
		
		switch(checkboxAnchor)
		{
			case TOPRIGHT:
				rect.top = 0;
				rect.right = (getWidth()/2) + (topDrawable.getIntrinsicWidth()/2);
				rect.bottom = (isChecked() ? checkedImage.getHeight() : uncheckedImage.getHeight());
				rect.left = rect.right - (isChecked() ? checkedImage.getWidth() : uncheckedImage.getWidth());
				break;
			case BOTTOMRIGHT:
				rect.right = (getWidth()/2) + (topDrawable.getIntrinsicWidth()/2);
				rect.bottom = topDrawable.getIntrinsicHeight();
				rect.top = rect.bottom - (isChecked() ? checkedImage.getHeight() : uncheckedImage.getHeight());;
				rect.left = rect.right - (isChecked() ? checkedImage.getWidth() : uncheckedImage.getWidth());
				break;
		}
		
		if (isChecked())
		{
			canvas.drawBitmap(checkedImage, null, rect, paint);
		}
		else
		{
			canvas.drawBitmap(uncheckedImage, null, rect, paint);
		}
		
	}

}
