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
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.util.ArrayList;
import java.util.List;

import levelup.app.gui_layer.custom_adapters.AnimationListenerAdapter;
import levelup.app.model_layer.PlayerValueHolder;


public class TreeMapController
{
    private ViewGroup container;
    private Context context;
    private List<PlayerValueHolder> player_values;
    private List<Pair<String, Float>> match_values;
    private List<Pair<String, Float>> first_half_values;
    private List<Pair<String, Float>> second_half_values;
    private DisplayPeriod period_displayed;

    public TreeMapController(Context context, ViewGroup tree_container)
    {
        container = tree_container;
        this.context = context;
        period_displayed = DisplayPeriod.MATCH;

    }

    public void set_player_values(List<PlayerValueHolder> holders)
    {
        player_values = holders;
        match_values = null;    //force the app to reload them when it is suppose to display
        first_half_values = null;
        second_half_values = null;
        switch (period_displayed)
        {
            case MATCH:
                show_full_match();
                break;
            case FIRST_HALF:
                show_first_half();
                break;
            case SECOND_HALF:
                show_second_half();
                break;
            default:
                break;
        }
    }

    public void show_full_match()
    {
        TreeMapDataPopulator populator = new TreeMapDataPopulator();
        period_displayed = DisplayPeriod.MATCH;
        if(match_values == null)
        {
            extract_values(period_displayed);
        }
        populator.populate(match_values);
        display_map(populator.getModel());
    }

    public void show_first_half()
    {
        TreeMapDataPopulator populator = new TreeMapDataPopulator();
        period_displayed = DisplayPeriod.FIRST_HALF;
        if(first_half_values == null)
        {
            extract_values(period_displayed);
        }
        populator.populate(first_half_values);
        display_map(populator.getModel());
    }

    public void show_second_half()
    {
        TreeMapDataPopulator populator = new TreeMapDataPopulator();
        period_displayed = DisplayPeriod.SECOND_HALF;
        if(second_half_values == null)
        {
            extract_values(period_displayed);
        }
        populator.populate(second_half_values);
        display_map(populator.getModel());
    }

    private void display_map(TreeModel model)
    {

        final MapLayoutView map = new MapLayoutView(context, model);
        Animation anim = AnimationUtils.loadAnimation(context, R.anim.fade_out);
        final Animation anim2 = AnimationUtils.loadAnimation(context, R.anim.fade_in_fast);
        anim.setAnimationListener(new AnimationListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animation animation)
            {
                container.removeAllViews();
                container.addView(map);
                map.setVisibility(View.VISIBLE);
                container.startAnimation(anim2);

            }
        });
//        container.addView(map);
//        map.setVisibility(View.VISIBLE);
//        container.invalidate();
        container.startAnimation(anim);
    }

    private void extract_values(DisplayPeriod period)
    {
        List<Pair<String, Float>> vals;
        switch (period)
        {
            case MATCH:
                match_values = new ArrayList<Pair<String, Float>>();
                vals = match_values;
                break;
            case FIRST_HALF:
                first_half_values = new ArrayList<Pair<String, Float>>();
                vals = first_half_values;
                break;
            case SECOND_HALF:
                second_half_values = new ArrayList<Pair<String, Float>>();
                vals = second_half_values;
                break;
            default:
                vals = null;
                break;
        }

        for(PlayerValueHolder holder : player_values)
        {
            float value;
            switch (period)
            {
                case MATCH:
                    value = holder.get_both_halves_values();
                    break;
                case FIRST_HALF:
                    value = holder.get_first_half_value();
                    break;
                case SECOND_HALF:
                    value = holder.get_second_half_value();
                    break;
                default:
                    value = 1;
                    break;
            }
            log("value for => " + period + " is => " + value + " player name => " + holder.getName());

            if(value > 0)
            {
                vals.add(new Pair<String, Float>(holder.getShortenedName(), value));
            }
        }
    }

    private void log(String text)
    {
        Log.d("TreeMapController", text);
    }

    private enum DisplayPeriod
    {
        MATCH,
        FIRST_HALF,
        SECOND_HALF;
    }

}
