package dudic.accieo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import adapter.ImageGridAdapter;
import app.Constants;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PlaceTypeFilter extends AppCompatActivity {

    @BindView(R.id.place_type_grid)
    GridView placeTypeGrid;

    int place_type = 0;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_type_filter);

        ButterKnife.bind(this);
        intent = new Intent();

        placeTypeGrid.setAdapter(new ImageGridAdapter(this, Constants.PLACE_TYPE_IMAGES, Constants.PLACE_TYPE_NAMES));
        placeTypeGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //showToast(position + " ");
                placeTypeGrid.getChildAt(place_type).setAlpha((float) 0.3);
                place_type = position;
                view.setAlpha((float) 1.0);
                intent.putExtra(Constants.PLACE_TYPE, place_type);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

}
