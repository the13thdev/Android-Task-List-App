package me.vijitdhingra.tasklist;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * Created by Vijit on 10-06-2016.
 */

public class DisplayHelper {

    //Assumes corner radius of cardView is 0
    public static void adjustCardViewDisplay(CardView cardView)
    {
        if(Build.VERSION.SDK_INT<21)
        {
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams)cardView.getLayoutParams();
            layoutParams.leftMargin=layoutParams.leftMargin - (int)(cardView.getMaxCardElevation());
            layoutParams.rightMargin=layoutParams.rightMargin- (int)(cardView.getMaxCardElevation());
            layoutParams.topMargin=layoutParams.topMargin-(int)(1.5*cardView.getMaxCardElevation());
            layoutParams.bottomMargin=layoutParams.bottomMargin-(int)(1.5*cardView.getMaxCardElevation());
        }
    }
}
