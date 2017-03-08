package ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONObject;

import java.io.File;

import adapter.ImageGridAdapter;
import adapter.ImageGridFacAdapter;
import app.Constants;
import app.User;
import butterknife.BindView;
import butterknife.ButterKnife;
import dudic.accieo.R;

public class AddPlace extends AppCompatActivity implements AdapterView.OnItemClickListener {
    Intent intent;
    double lat, lang;
    String placeName, address;
    Context mContext;
    ImageGridFacAdapter imageGridFacAdapter;
//    CheckBoxAdapter checkBoxAdapter;


    @BindView(R.id.addressField)
    TextInputEditText addressField;

    @BindView(R.id.placeField)
    TextInputEditText placeField;

    @BindView(R.id.place_type_grid)
    GridView placeTypeGrid;

    @BindView(R.id.facilities_type_grid)
    GridView facilitiesTypeGrid;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    String accessLevel = "-1", TAG = AddPlace.class.getSimpleName();
    int place_type = 0;
    int[] placeTypes;
    String[] placeNames;
    String[] facilitiesTypes;
    String facilities = "";
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_place);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        initUI();
    }

    private void initUI() {
        mContext = this;
        intent = getIntent();
        ButterKnife.bind(this);

        user = new User(this);

        lat = intent.getDoubleExtra(Constants.LATITUDE, 0);
        lang = intent.getDoubleExtra(Constants.LONGITUDE, 0);

        placeName = intent.getStringExtra(Constants.PLACE_NAME);
        address = intent.getStringExtra(Constants.ADDRESS);

        addressField.setText(address);
        placeField.setText(placeName);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.add_info);

        placeTypes = Constants.PLACE_TYPE_IMAGES;
        placeNames = Constants.PLACE_TYPE_NAMES;


        placeTypeGrid.setNumColumns(placeTypes.length);
        gridViewSetting(placeTypeGrid);
        placeTypeGrid.setAdapter(new ImageGridAdapter(this, placeTypes, placeNames));

        final HorizontalScrollView h = (HorizontalScrollView) findViewById(R.id.grid_horizontal_scroll);
        h.post(new Runnable() {
            public void run() {
                h.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            }
        });
        h.postDelayed(new Runnable() {
            public void run() {
                h.smoothScrollTo(0, 0);
            }
        }, 1250L);
//        checkBoxAdapter = new CheckBoxAdapter(this);
        imageGridFacAdapter = new ImageGridFacAdapter(this, Constants.FACILITIES_TYPE_ICONS, Constants.FACILITIES_TYPE_NAMES);
        facilitiesTypes = Constants.FACILITIES_TYPE_NAMES;
        facilitiesTypeGrid.setAdapter(imageGridFacAdapter);

        placeTypeGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //showToast(position + " ");
                placeTypeGrid.getChildAt(place_type).setAlpha((float) 0.3);
                place_type = position;
                view.setAlpha((float) 1.0);
            }
        });

        facilitiesTypeGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                boolean state  = imageGridFacAdapter.toggle(position);
                if(state){
                    facilitiesTypeGrid.getChildAt(position).setAlpha((float) 1.0);
                    view.setAlpha((float) 1.0);
                } else{
                    facilitiesTypeGrid.getChildAt(position).setAlpha((float) 0.3);
                }
            }
        });

        RatingBar ratingBar = (RatingBar) findViewById(R.id.accessibility_rating);

        if (ratingBar != null) {
            Drawable progress = ratingBar.getProgressDrawable();
            DrawableCompat.setTint(progress, getResources().getColor(R.color.colorPrimary));
            ratingBar.setProgressDrawable(progress);
            ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    TextView ratingText = (TextView) findViewById(R.id.rating_indicator);
                    if (ratingText != null) {
                        if (rating >= 0.0 && rating < 1.5) {
                            accessLevel = "0";
                            ratingText.setText("Not Accessible");
                            ratingText.setTextColor(Color.RED);
                        } else if (rating >= 1.5 && rating < 3.5) {
                            accessLevel = "1";
                            ratingText.setText("Mildly Accessible");
                            ratingText.setTextColor(Color.BLUE);
                        } else {
                            accessLevel = "2";
                            ratingText.setText("Very Accessible");
                            ratingText.setTextColor(Color.GREEN);
                        }
                    } else {
                        Log.e(TAG, "Rating text error");
                    }

                }
            });
        } else
            Log.e(TAG, "Rating Bar error");

    }

    private void gridViewSetting(GridView gridview) {

        int size = placeTypes.length;
        // Calculated single Item Layout Width for each grid element ....
        int width = 60;

        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        float density = dm.density;

        int totalWidth = (int) (width * size * density);
        int singleItemWidth = (int) (width * density);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                totalWidth, LinearLayout.LayoutParams.MATCH_PARENT);

        gridview.setLayoutParams(params);
        gridview.setColumnWidth(singleItemWidth);
        gridview.setHorizontalSpacing(2);
        gridview.setStretchMode(GridView.STRETCH_SPACING);
        gridview.setNumColumns(size);
    }


    public void dismiss() {
        finish();
    }

    public void submitPlace() {
        facilities = "";

        if (!isInputValid()) {
            showToast(getString(R.string.invalid_input));
            return;
        }

        Log.e(TAG, user.getToken());


        // Log.e("Pro", facilities + " hi " + checkBoxAdapter.mCheckStates.length + " " + Constants.FACILITIES_TYPE_NAMES.length);
        for (int i = 0; i < imageGridFacAdapter.mCheckStates.length; i++) {
            // Log.e("ITR", Constants.FACILITIES_TYPE_NAMES[i] + " " + checkBoxAdapter.mCheckStates[i]);
            if (imageGridFacAdapter.mCheckStates[i])
                facilities += Constants.FACILITIES_TYPE_NAMES[i] + ", ";
        }
        Log.e("Pro", facilities + " hi");


        if (facilities.length() == 0)
            return;

        placeName = placeField.getText().toString();
        address = addressField.getText().toString();


        Log.d(TAG, "acc_level: " + accessLevel + "\nplace type: " + place_type + "\nPlace Fac: " + facilities.subSequence(0, facilities.length() - 2));

        Ion.with(mContext)
                .load(Constants.ADDPLACES)
                .setBodyParameter(Constants.ACTUAL_ADDRESS, address).setBodyParameter(Constants.PLACE_NAME, placeName)
                .setBodyParameter(Constants.TOKEN, user.getToken()).setBodyParameter(Constants.ACCESSIBLITY_LEVEL, accessLevel)
                .setBodyParameter(Constants.LATITUDE, lat + "").setBodyParameter(Constants.LONGITUDE, lang + "")
                .setBodyParameter(Constants.PLACE_TYPE, place_type + "").setBodyParameter(Constants.FACILITIES,
                facilities.subSequence(0, facilities.length() - 2) + "")
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        if (e != null) {
                            showToast(getString(R.string.server_problem));
                            Log.e(TAG, "e: " + e.toString());
                            return;
                        }

                        Log.e(TAG, result);
                        Log.e(TAG, "LEN " + result.length());

                        try {
                            JSONObject json = new JSONObject(Constants.formatResponse(result));
                            showToast(json.getString(Constants.MESSAGE));
                            if (json.getString(Constants.STATUS).equals("200")) {
                                startActivity(new Intent(mContext, Maps.class));
                            }
                        } catch (Exception e1) {
                            Log.e(TAG, e1.toString());
                        }
                    }
                });

    }


    boolean isInputValid() {
        return address.length() > 2 && placeName.length() > 2 && accessLevel != null && lat != 0 && lang != 0 && place_type != -1;
    }

//    public void addLevel(View v) {
//        switch (v.getId()) {
//            case R.id.na:
//                accessLevel = "0";
//                break;
//            case R.id.ma:
//                accessLevel = "1";
//                break;
//            case R.id.va:
//                accessLevel = "2";
//                break;
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add_place, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                submitPlace();
                break;
            case R.id.action_dismiss:
                dismiss();
                break;
        }
        return true;
    }


    void showToast(final String s) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, s, Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        boolean state = imageGridFacAdapter.toggle(position);
        if(state){
            view.setAlpha(1.0f);
        } else {
            view.setAlpha(0.3f);
        }
        //Log.e("Toogled", checkBoxAdapter.mCheckStates[position] + " ");
    }
}

//class CheckBoxAdapter extends ArrayAdapter implements CompoundButton.OnCheckedChangeListener {
//    public Boolean mCheckStates[];
//    LayoutInflater mInflater;
//    CheckBox cb;
//    String[] fac;
//
//    CheckBoxAdapter(AddPlace context) {
//        super(context, 0);
//        mCheckStates = new Boolean[]{false, false, false, false, false, false};
//        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        fac = Constants.FACILITIES_TYPE_NAMES;
//        //   Log.e("Len ", " " + mCheckStates.length);
//    }
//
//    @Override
//    public int getCount() {
//        return Constants.FACILITIES_TYPE_NAMES.length;
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return position;
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return 0;
//    }
//
//    public void toggle(int position) {
//        setChecked(position, !isChecked(position));
//    }
//
//    @Override
//    public View getView(final int position, View convertView, ViewGroup parent) {
//        View vi = convertView;
//
//        if (convertView == null)
//            vi = mInflater.inflate(R.layout.checkbox_grid, null);
//
//        cb = (CheckBox) vi.findViewById(R.id.facilityCheckBox);
//
//        cb.setButtonDrawable(Constants.FACILITIES_TYPE_SELECTORS[position]);
//        cb.setHeight(250);
//        cb.setWidth(250);
//        cb.setText(fac[position]);
//        cb.setTag(position);
//        //cb.setChecked(mCheckStates[position]);
//        cb.setOnCheckedChangeListener(this);
//        return vi;
//    }
//
//    public boolean isChecked(int position) {
//        return mCheckStates[position];
//    }
//
//    public void setChecked(int position, boolean isChecked) {
//        mCheckStates[position] = isChecked;
//    }
//
//    @Override
//    public void onCheckedChanged(CompoundButton buttonView,
//                                 boolean isChecked) {
//        setChecked((int) buttonView.getTag(), isChecked);
//        //Log.e("On check changed", isChecked + " ");
//
//        notifyDataSetChanged();
//    }
//}