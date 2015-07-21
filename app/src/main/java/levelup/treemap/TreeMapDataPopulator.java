package levelup.treemap;

import android.graphics.Color;
import android.util.Pair;

import java.util.List;

/**
 * Created by Arin on 2015-01-01.
 */
public class TreeMapDataPopulator
{
    private TreeModel model;
    private final static int[] colors = { 0xFF8c2b0e, 0xFFd95129, 0xFFf28a35, 0xFFf3d4b5, 0xFF00668e, 0xFF6eb8db, 0xFF9ad3e6, 0xFFc0dfe4, 0xFF3d3020,
            0xFF534741, 0xFFd9987c, 0xFFd18156, 0xFFdac1a8, 0xFF77665c, 0xFFafa89e, 0xFF447488, 0xFF83aec1, 0xFFa4c5ce, 0xFFc4d3d6, 0xFF514d4a,
            0xFF676362, 0xFFF15F2D };


    public void populate(List<Pair<String, Float>> values)
    {
        float full_value = 0;
        for(Pair<String, Float> p : values)
        {
            full_value += p.second;
        }
        AndroidMapItem rootObject = new AndroidMapItem(full_value, "Team", Color.WHITE);
        model = new TreeModel(rootObject);
        int color_pos = 0;
        for(Pair<String, Float> val : values)
        {
            int rand_color = colors[color_pos];
            model.addChild(new TreeModel(new AndroidMapItem(val.second, val.first, rand_color)));
            color_pos ++;
            color_pos %= colors.length;
        }
    }

    public TreeModel getModel()
    {
        return model;
    }
}
