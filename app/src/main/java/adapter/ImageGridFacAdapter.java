package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import dudic.accieo.R;

public class ImageGridFacAdapter extends BaseAdapter {
    public Boolean[] mCheckStates;
    Context mContext;
    int[] placeTypes;
    String[] placeNames;
    private static LayoutInflater inflater = null;

    public ImageGridFacAdapter(Context context, int[] placeFacTypes, String[] placeFacNames) {
        mContext = context;
        this.placeTypes = placeFacTypes;
        this.placeNames = placeFacNames;
        mCheckStates = new Boolean[]{false, false, false, false, false, false};
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

    public boolean toggle(int pos) {
        mCheckStates[pos] = !mCheckStates[pos];
        return mCheckStates[pos];
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
