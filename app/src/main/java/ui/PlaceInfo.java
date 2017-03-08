
package ui;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import org.parceler.Parcels;

import app.Constants;
import app.Place;
import butterknife.BindView;
import butterknife.ButterKnife;
import dudic.accieo.R;

public class PlaceInfo extends AppCompatActivity {

    @BindView(R.id.pop_place_name)
    TextView placeName;

    @BindView(R.id.pop_place_loc)
    TextView placeLoc;

    @BindView(R.id.pop_rating)
    RatingBar placeRating;

    @BindView(R.id.pop_place_fac)
    TextView placeFac;

    @BindView(R.id.pop_place_icon)
    ImageView placeIcon;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    Intent intent;
    String TAG = PlaceInfo.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_info_new);
        initUI();
    }

    private void initUI() {
        ButterKnife.bind(this);

        intent = getIntent();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.more_info);

        Place clickedPlace = Parcels.unwrap(intent.getParcelableExtra(Constants.PLACE_OBJECT));

        if (clickedPlace == null)
            return;

//        String info = clickedPlace.name + "\n" + clickedPlace.address + "\nFacilities available: " + clickedPlace.facilties
//                + "\nAccessibility Score: " + Double.parseDouble(clickedPlace.rate);

        placeName.setText(clickedPlace.name);
        placeLoc.setText(clickedPlace.address);
        placeIcon.setImageResource(Constants.PLACE_TYPE_IMAGES[Integer.parseInt(clickedPlace.type)]);
        int rating = Integer.parseInt(clickedPlace.rate);
        if(rating > 2)
            rating = 2;
        if(rating < 0)
            rating = 0;

        switch (rating){
            case 0:
                placeRating.setRating(1.0f);
                break;
            case 1:
                placeRating.setRating(2.5f);
                break;
            case 2:
                placeRating.setRating(5.0f);
                break;
            default:
                placeRating.setRating(0.0f);
        }

        if(!clickedPlace.facilties.trim().equals(""))
            placeFac.setText(clickedPlace.facilties);
        else
            placeFac.setText("None");
    }

    public void dismiss(View v) {
        finish();
    }
}
