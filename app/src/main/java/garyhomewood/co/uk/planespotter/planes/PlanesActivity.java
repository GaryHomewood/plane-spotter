package garyhomewood.co.uk.planespotter.planes;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
    @BindView(R.id.tabs) TabLayout tabLayout;
    @BindView(R.id.content_pager) ViewPager contentPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planes);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbarTitle.setText(R.string.plane_list_title);

        PlaneGridPagerAdapter adapter = new PlaneGridPagerAdapter(getSupportFragmentManager());
        contentPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(contentPager);
    }

    public class PlaneGridPagerAdapter extends FragmentPagerAdapter {

        PlaneGridPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                default:
                    return PlanesFragment.newInstance();
                case 1:
                    return FavouritesFragment.newInstance();
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                default:
                    return "Latest";
                case 1:
                    return "Favourites";
            }
        }
    }
}
