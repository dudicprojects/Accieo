package adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import dudic.accieo.R;

public class ImageGridAdapter extends BaseAdapter {


    Context mContext;
    int[] placeTypes;
    String[] placeNames;
    private static LayoutInflater inflater = null;

    public ImageGridAdapter(Context context, int[] placeTypes, String[] placeNames) {
        mContext = context;
        this.placeTypes = placeTypes;
        this.placeNames = placeNames;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return placeTypes.length;
    }


    @Override
    public Object getItem(int position) {
        return placeTypes[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public class Holder {
        ImageView imageView;
        TextView textView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = new Holder();
        View rootView = inflater.inflate(R.layout.grid_item, null);
        holder.imageView = (ImageView) rootView.findViewById(R.id.grid_item_icon);
        holder.textView = (TextView) rootView.findViewById(R.id.grid_item_text);
        rootView.setAlpha((float) 0.3);
        holder.imageView.setAlpha((float) 1.0);
//        holder.imageView.setBackgroundColor(Color.BLUE);
        holder.imageView.setImageResource(placeTypes[position]);
        holder.textView.setText(placeNames[position]);
        return rootView;
    }
}