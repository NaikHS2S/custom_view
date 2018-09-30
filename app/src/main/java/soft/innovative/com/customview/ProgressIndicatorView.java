package soft.innovative.com.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

public class ProgressIndicatorView extends View {

    private int progress = 0;
    private final int progressColor;
    private final int progressBackColor;
    private final int progressTextColor;
    private final float progressTextSize;
    private final float progressBarHeight;
    private Paint progressBarPaint;
    private Paint progressBackPaint;
    private Paint progressTextPaint;
    final RectF backProgressRect = new RectF(0, 0, 0, 0);
    final RectF progressRectF = new RectF(0, 0, 0, 0);


    private ProgressListener progressListener;

    public ProgressIndicatorView(Context context) {
        this(context, null);
    }

    public ProgressIndicatorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressIndicatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ProgressIndicatorView, defStyleAttr, 0);
        progressColor = attributes.getColor(R.styleable.ProgressIndicatorView_progress_color, Color.rgb(0, 0, 100));
        progressBackColor = attributes.getColor(R.styleable.ProgressIndicatorView_progress_back_color, Color.rgb(0, 100, 100));
        progressTextColor = attributes.getColor(R.styleable.ProgressIndicatorView_progress_text_color, Color.rgb(80, 100, 200));
        progressTextSize = attributes.getDimension(R.styleable.ProgressIndicatorView_progress_text_size, spToPixel(10));
        progressBarHeight = attributes.getDimension(R.styleable.ProgressIndicatorView_progress_bar_height, dpToPixel(10f));

        attributes.recycle();
        initPaints();
    }


    @Override
    protected int getSuggestedMinimumWidth() {
        return (int) progressTextSize;
    }

    @Override
    protected int getSuggestedMinimumHeight() {
        return Math.max((int) progressTextSize, Math.max((int) progressBarHeight, (int) progressBarHeight));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measure(widthMeasureSpec, true), measure(heightMeasureSpec, false));
    }

    private int measure(int measureSpec, boolean isWidth) {
        int result;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        int padding = isWidth ? getPaddingLeft() + getPaddingRight() : getPaddingTop() + getPaddingBottom();
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            result = isWidth ? getSuggestedMinimumWidth() : getSuggestedMinimumHeight();
            result += padding;
            if (mode == MeasureSpec.AT_MOST) {
                if (isWidth) {
                    result = Math.max(result, size);
                } else {
                    result = Math.min(result, size);
                }
            }
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        final String progressDisplayText = new StringBuilder().append(progress).append("%").toString();
        final float displayedTextWidth = progressTextPaint.measureText(progressDisplayText);
        float displayedTextStartPosition;

        float displayedTextEndPosition;

        if (progress == 0) {
            displayedTextStartPosition = getPaddingLeft();
            backProgressRect.left = getPaddingLeft();
            backProgressRect.right = getWidth() - getPaddingRight();
            backProgressRect.top = getHeight() / 2f + -progressBarHeight / 2f;
            backProgressRect.bottom = getHeight() / 2f + progressBarHeight / 2f;
        } else {
            progressRectF.left = getPaddingLeft();
            progressRectF.top = getHeight() / 2f - progressBarHeight / 2f;
            progressRectF.right = (getWidth() - getPaddingLeft() - getPaddingRight()) / (100f) * progress + getPaddingLeft();
            progressRectF.bottom = getHeight() / 2f + progressBarHeight / 2f;
            if (progress > 2)
                displayedTextStartPosition = (progressRectF.right - displayedTextWidth / 2f);
            else
                displayedTextStartPosition = getPaddingLeft();

            backProgressRect.left = progressRectF.right;
            backProgressRect.right = getWidth() - getPaddingRight();
            backProgressRect.top = getHeight() / 2f + -progressBarHeight / 2f;
            backProgressRect.bottom = getHeight() / 2f + progressBarHeight / 2f;

        }

        displayedTextEndPosition = (int) ((getHeight() / 2f) - ((progressTextPaint.descent() + progressTextPaint.ascent()) / 2f));

        if ((displayedTextStartPosition + displayedTextWidth) >= getWidth() - getPaddingRight()) {
            displayedTextStartPosition = getWidth() - getPaddingRight() - displayedTextWidth;
        }

        if (progress > 0) {
            canvas.drawRect(progressRectF, progressBarPaint);
        }

        canvas.drawRect(backProgressRect, progressBackPaint);

        canvas.drawText(progressDisplayText, displayedTextStartPosition,
                displayedTextEndPosition + dpToPixel(-20), progressTextPaint);

    }

    private void initPaints() {
        progressBarPaint = new Paint();
        progressBarPaint.setColor(progressColor);
        progressBarPaint.setStrokeCap(Paint.Cap.ROUND);

        progressBackPaint = new Paint();
        progressBackPaint.setColor(progressBackColor);
        progressBackPaint.setStrokeCap(Paint.Cap.ROUND);

        progressTextPaint = new Paint();
        Typeface bold = Typeface.create("Arial", Typeface.ITALIC);
        progressTextPaint.setColor(progressTextColor);
        progressTextPaint.setTypeface(bold);
        progressTextPaint.setTextSize(progressTextSize);
    }

    public void setProgress(int progress) {
        if (progress <= 100 && progress >= 0) {
            this.progress = progress;
            invalidate();
        }
    }

    public int getProgress() {
       return progress;
    }

    public void incrementProgress(int incrementProgress) {
        if (incrementProgress > 0) {
            setProgress(progress + incrementProgress);
        }

        if (progressListener != null) {
            progressListener.onProgressUpdate(progress, 100);
        }
    }

    private float dpToPixel(float dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

    private float spToPixel(float sp) {
        final float scale = getResources().getDisplayMetrics().scaledDensity;
        return sp * scale;
    }

    public void setOnProgressBarListener(ProgressListener progressListener) {
        this.progressListener = progressListener;
    }

}
