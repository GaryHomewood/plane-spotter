package garyhomewood.co.uk.planespotter.planes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import garyhomewood.co.uk.planespotter.PlaneSpotterApp;
import garyhomewood.co.uk.planespotter.R;
import garyhomewood.co.uk.planespotter.model.Favourite;
import garyhomewood.co.uk.planespotter.model.GalleryItem;
import garyhomewood.co.uk.planespotter.planesgallery.GalleryActivity;
import io.realm.Realm;

/**
 *
 */
public class FavouritesFragment extends Fragment implements FavouritesContract.View {

    private static final String KEY_ITEMS = "KEY_ITEMS";
    private static final String KEY_SELECTED_ITEM = "KEY_SELECTED_ITEM";

    @BindView(R.id.item_list) RecyclerView recyclerView;
    @BindView(R.id.refresh_layout) SwipeRefreshLayout swipeRefreshLayout;

    private FavouritesContract.UserActionsListener presenter;
    private FavouritesFragment.FavouritesAdapter adapter;
    private List<Favourite> favourites;
    private FavouritesFragment.ItemClickListener itemClickListener = new FavouritesFragment.ItemClickListener() {
        @Override
        public void onItemClick(int selectedItem) {
            presenter.openGallery(favourites, selectedItem);
        }
    };

    public static FavouritesFragment newInstance() {
        Bundle args = new Bundle();
        FavouritesFragment fragment = new FavouritesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Realm realm = ((PlaneSpotterApp) getActivity().getApplication()).getRealm();
        presenter = new FavouritesPresenter(this, realm);
        adapter = new FavouritesAdapter(new ArrayList<Favourite>(), itemClickListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.loadFavourites();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_planes, container, false);
        ButterKnife.bind(this, root);

        int numberOfColumns = (getResources().getConfiguration().orientation == OrientationHelper.VERTICAL) ? 2 : 3;
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), numberOfColumns));

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.loadFavourites();
            }
        });

        return root;
    }

    @Override
    public void showFavourites(List<Favourite> favourites) {
        this.favourites = favourites;
        adapter.replaceData(favourites);
    }

    @Override
    public void showProgressIndicator(final boolean active) {
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(active);
            }
        });
    }

    @Override
    public void showError(String message) {
        Snackbar.make(swipeRefreshLayout, message, BaseTransientBottomBar.LENGTH_SHORT).show();
    }

    @Override
    public void showGallery(List<GalleryItem> items, int selectedItem) {
        Intent intent = new Intent(getContext(), GalleryActivity.class);
        intent.putExtra(KEY_ITEMS, Parcels.wrap(items));
        intent.putExtra(KEY_SELECTED_ITEM, selectedItem);
        startActivity(intent);
    }

    public static class FavouritesAdapter extends RecyclerView.Adapter<FavouritesAdapter.ViewHolder> {
        private List<Favourite> favourites;
        private FavouritesFragment.ItemClickListener itemClickListener;
        private Context context;

        FavouritesAdapter(List<Favourite> favourites, FavouritesFragment.ItemClickListener itemClickListener) {
            this.favourites = favourites;
            this.itemClickListener = itemClickListener;
        }

        @Override
        public FavouritesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            context = parent.getContext();
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_plane, parent, false);
            return new FavouritesAdapter.ViewHolder(v, itemClickListener, favourites);
        }

        @Override
        public void onBindViewHolder(FavouritesAdapter.ViewHolder holder, int position) {
            Favourite plane = favourites.get(position);
            holder.title.setText(plane.getTitle());
            holder.description.setText(plane.getDescription());
            Glide.with(context)
                    .load(plane.getThumbnail())
                    .fitCenter()
                    .into(holder.thumbnail);
        }

        @Override
        public int getItemCount() {
            return favourites.size();
        }

        void replaceData(List<Favourite> favourites) {
            this.favourites = favourites;
            notifyDataSetChanged();
        }

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            @BindView(R.id.item_title) TextView title;
            @BindView(R.id.item_description) TextView description;
            @BindView(R.id.item_thumbnail) ImageView thumbnail;
            FavouritesFragment.ItemClickListener itemClickListener;
            List<Favourite> favourites;

            ViewHolder(View itemView, FavouritesFragment.ItemClickListener itemClickListener, List<Favourite> favourites) {
                super(itemView);
                ButterKnife.bind(this, itemView);
                this.itemClickListener = itemClickListener;
                this.favourites = favourites;
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                itemClickListener.onItemClick(getAdapterPosition());
            }
        }
    }

    public interface ItemClickListener {
        void onItemClick(int selectedItem);
    }
}
