package garyhomewood.co.uk.planespotter.planesgallery;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.parceler.Parcels;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import garyhomewood.co.uk.planespotter.PlaneSpotterApp;
import garyhomewood.co.uk.planespotter.R;
import garyhomewood.co.uk.planespotter.model.Favourite;
import garyhomewood.co.uk.planespotter.model.GalleryItem;
import io.realm.Realm;
import io.realm.RealmResults;
import timber.log.Timber;

/**
 *
 */
public class GalleryActivity extends AppCompatActivity {

    private static final String KEY_ITEMS = "KEY_ITEMS";
    private static final String KEY_SELECTED_ITEM = "KEY_SELECTED_ITEM";
    private static final int UI_ANIMATION_DELAY = 100;

    protected int selectedItem;
    private List<GalleryItem> items;
    private Map<Integer, View> captions = new HashMap<>();
    private int captionOffscreenTranslation;
    private Menu menu;
    private boolean isFavourite;
    private GalleryPagerAdapter adapter;

    private boolean immersiveFullscreen;
    private final Handler immersiveFullscreenHandler = new Handler();
    private final Runnable hideUI = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar
            viewPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private final Runnable showUI = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            viewPager.setVisibility(View.VISIBLE);
        }
    };

    @BindView(R.id.main_content) FrameLayout mainContent;
    @BindView(R.id.appbar) AppBarLayout appBarLayout;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.container) ViewPager viewPager;

    private class PageListener extends ViewPager.SimpleOnPageChangeListener {
        @Override
        public void onPageSelected(int position) {
            View captionView = captions.get(position);
            if (captionView != null) {
                if (immersiveFullscreen) {
                    captionView.setTranslationY(captionOffscreenTranslation);
                } else {
                    captionView.setTranslationY(0);
                }
            }

            isFavourite = checkIfFavourite(position);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        ButterKnife.bind(this);

        items = Parcels.unwrap(getIntent().getParcelableExtra(KEY_ITEMS));
        selectedItem = getIntent().getIntExtra(KEY_SELECTED_ITEM, 0);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        adapter = new GalleryPagerAdapter(this);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(selectedItem);
        viewPager.addOnPageChangeListener(new PageListener());

        immersiveFullscreen = false;
        captionOffscreenTranslation = (int) (100 * Resources.getSystem().getDisplayMetrics().density);

        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) appBarLayout.getLayoutParams();
        layoutParams.setMargins(0, getStatusBarHeight(), 0, 0);

        if (Build.VERSION.SDK_INT >= 21) {
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mainContent.getLayoutParams();
            lp.setMargins(0, -getStatusBarHeight(), 0, 0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_gallery, menu);
        this.menu = menu;

        isFavourite = checkIfFavourite(selectedItem);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_favourite:
                if (isFavourite) {
                    removeFavourite();
                } else {
                    addFavourite();
                }
                return true;

            case R.id.action_settings:
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean checkIfFavourite(int position) {
        // indicate if this item is a favourite
        GalleryItem item = items.get(position);
        Realm realm  = ((PlaneSpotterApp) getApplication()).getRealm();
        final RealmResults<Favourite> results = realm.where(Favourite.class).equalTo("thumbnail", item.thumbnail).findAll();
        if (results.size() == 1) {
            menu.getItem(0).setIcon(ContextCompat.getDrawable(getBaseContext(), R.drawable.ic_bookmark));
            return true;
        } else {
            menu.getItem(0).setIcon(ContextCompat.getDrawable(getBaseContext(), R.drawable.ic_bookmark_border));
            return false;
        }
    }

    private void removeFavourite() {
        Realm realm  = ((PlaneSpotterApp) getApplication()).getRealm();
        final GalleryItem selectedItem = items.get(viewPager.getCurrentItem());

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<Favourite> results = realm.where(Favourite.class).equalTo("thumbnail", selectedItem.thumbnail).findAll();
                results.deleteAllFromRealm();
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Timber.d("Success");
                menu.getItem(0).setIcon(ContextCompat.getDrawable(getBaseContext(), R.drawable.ic_bookmark_border));
                isFavourite = false;
                adapter.notifyDataSetChanged();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Timber.d("Error", error);
            }
        });
    }

    private void addFavourite() {
        Realm realm  = ((PlaneSpotterApp) getApplication()).getRealm();
        final GalleryItem selectedItem = items.get(viewPager.getCurrentItem());

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realmBackground) {
                Favourite favourite = realmBackground.createObject(Favourite.class);
                favourite.setTitle(selectedItem.title);
                favourite.setDescription(selectedItem.description);
                favourite.setSubject(selectedItem.subject);
                favourite.setThumbnail(selectedItem.thumbnail);
                favourite.setCreatedDate(new Date(System.currentTimeMillis()));
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Timber.d("Success");
                menu.getItem(0).setIcon(ContextCompat.getDrawable(getBaseContext(), R.drawable.ic_bookmark));
                isFavourite = true;
                adapter.notifyDataSetChanged();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Timber.d("Error", error);
            }
        });
    }

    class GalleryPagerAdapter extends PagerAdapter {
        Context context;
        LayoutInflater layoutInflater;

        GalleryPagerAdapter(Context context) {
            this.context = context;
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = layoutInflater.inflate(R.layout.gallery_item, container, false);

            ImageView imageView = (ImageView) view.findViewById(R.id.item_image);
            Glide.with(context)
                    .load(items.get(position).getOriginalUrl())
                    .into(imageView);

            final TextView caption = (TextView) view.findViewById(R.id.caption);
            caption.setText(items.get(position).title);
            if (immersiveFullscreen) {
                caption.setTranslationY(captionOffscreenTranslation);
            }

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (immersiveFullscreen) {
                        caption.animate().translationY(0);
                    } else {
                        caption.animate().translationY(captionOffscreenTranslation);
                    }
                    toggle();
                }
            });

            container.addView(view);
            captions.put(position, caption);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
            captions.remove(position);
        }
    }

    private void toggle() {
        if (immersiveFullscreen) {
            show();
        } else {
            hide();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        immersiveFullscreen = true;

        // Schedule a runnable to remove the status and navigation bar after a delay
        immersiveFullscreenHandler.removeCallbacks(showUI);
        immersiveFullscreenHandler.postDelayed(hideUI, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        viewPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        immersiveFullscreen = false;

        // Schedule a runnable to display UI elements after a delay
        immersiveFullscreenHandler.removeCallbacks(hideUI);
        immersiveFullscreenHandler.postDelayed(showUI, UI_ANIMATION_DELAY);
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
