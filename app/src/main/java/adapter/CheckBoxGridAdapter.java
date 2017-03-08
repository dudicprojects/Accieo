package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import dudic.accieo.R;

/**
 * Created by dic on 13/05/2016.
 */
public class CheckBoxGridAdapter extends BaseAdapter {

    Context mContext;
    String[] facilitiesType;

    public CheckBoxGridAdapter(Context context, String[] facilitiesType) {
        mContext = context;
        this.facilitiesType = facilitiesType;
    }

    private class ViewHolder {
        CheckBox facilityCheckBox;
    }


    @Override
    public int getCount() {
        return facilitiesType.length;
    }

    @Override
    public Object getItem(int position) {
        return facilitiesType[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        LayoutInflater layoutInflater = (LayoutInflater)
                mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            holder = new ViewHolder();

            convertView = layoutInflater.inflate(
                    R.layout.checkbox_grid, null);
            holder.facilityCheckBox = (CheckBox) convertView.findViewById(R.id.facilityCheckBox);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.facilityCheckBox.setId(position);
        holder.facilityCheckBox.setText(facilitiesType[position]);

        holder.facilityCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    holder.facilityCheckBox.setTag("Checked");
            }
        });

        return convertView;
        /*CheckBox checkBox;

        if (convertView == null) {
            checkBox = new CheckBox(mContext);
            checkBox.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            checkBox.setPadding(8, 8, 8, 8);
        } else
            checkBox = (CheckBox) convertView;

        checkBox.setText(facilitiesType[position]);

        return checkBox;
*/

    }
}

