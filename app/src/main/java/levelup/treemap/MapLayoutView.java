/*
 * Copyright 2013 Robert Theis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package levelup.treemap;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

public class MapLayoutView extends View
{

    private AbstractMapLayout mapLayout;
    private Mappable[] mappableItems;
    private Paint mRectBackgroundPaint;
    private Paint mRectBorderPaint;
    private Paint mTextPaint;
    private int custom_color;    //application white for the moment

    public MapLayoutView(Context context, AttributeSet attributeSet)
    {
        super(context);
    }

    public MapLayoutView(Context context, TreeModel model) {
        super(context);

        custom_color = context.getResources().getColor(Color.WHITE);

        mapLayout = new SquarifiedLayout();

        mappableItems = model.getTreeItems();//getItems();

        // Set up the Paint for the rectangle background
        mRectBackgroundPaint = new Paint();
        mRectBackgroundPaint.setColor(Color.CYAN);
        mRectBackgroundPaint.setStyle(Paint.Style.FILL);

        // Set up the Paint for the rectangle border
        mRectBorderPaint = new Paint();
        mRectBorderPaint.setColor(custom_color);
        mRectBorderPaint.setStyle(Paint.Style.STROKE); // outline the rectangle
        mRectBorderPaint.setStrokeWidth(pixelize(2)); // some reasonable outline

        // Set up the Paint for the text label
        mTextPaint = new Paint();
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(custom_color);
        mTextPaint.setTextSize(20);
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh){
        super.onSizeChanged(w, h, oldw, oldh);

        // Lay out the placement of the rectangles within the area available to this view
        mapLayout.layout(mappableItems, new Rect(0, 0, w, h));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw all the rectangles and their labels
        for (int i = 0; i < mappableItems.length; i++) {
            AndroidMapItem item = (AndroidMapItem) mappableItems[i];
            drawRectangle(canvas, item.getBoundsRectF(), item.getColor());
            drawText(canvas, item.getLabel(), item.getBoundsRectF());
        }
    }

    private void drawRectangle(Canvas canvas, RectF rectF, int color)
    {
        //paint in a specific color
        mRectBackgroundPaint.setColor(color);
        // Draw the rectangle's background
        canvas.drawRect(rectF, mRectBackgroundPaint);

        // Draw the rectangle's border
        canvas.drawRect(rectF, mRectBorderPaint);
    }

    private void drawText(Canvas canvas, String text, RectF rectF)
    {
        // Don't draw text for small rectangles


        if (rectF.width() > 30)
        {
            float textSize = Math.max(rectF.width() / 7, 11);   //scale font accordin to rectangle
            mTextPaint.setTextSize(textSize);
            android.graphics.Rect bounds = new android.graphics.Rect();
            mTextPaint.getTextBounds(text, 0, text.length(), bounds);
            int half_height = bounds.height() / 2;

            // turn text depending on whether rectangle is "lying" or "standing"
            if(rectF.width() < rectF.height())
            {
                canvas.save();
                canvas.rotate(270, rectF.centerX(), rectF.centerY());
                canvas.drawText(text, rectF.centerX(), rectF.centerY() + half_height, mTextPaint);
                canvas.restore();
            }
            else
            {
                canvas.drawText(text, rectF.centerX(), rectF.centerY() + half_height, mTextPaint);
            }
        }
    }

    private int pixelize(int dps)
    {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dps,
                getResources().getDisplayMetrics());
    }
}

