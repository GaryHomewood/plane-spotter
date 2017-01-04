package garyhomewood.co.uk.planespotter.planes;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import garyhomewood.co.uk.planespotter.R;

/**
 *
 */
public class PlanesActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.toolbar_title) TextView toolbarTitle;
    @BindView(R.id.contentFrame) FrameLayout contentFrame;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planes);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbarTitle.setText(R.string.plane_list_title);

        if (savedInstanceState == null) {
            initFragment(PlanesFragment.newInstance());
        }
    }

    private void initFragment(Fragment planesFragment) {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.contentFrame, planesFragment)
                .commit();
    }
}
