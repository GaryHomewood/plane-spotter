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
import garyhomewood.co.uk.planespotter.R;
import garyhomewood.co.uk.planespotter.di.Injector;
import garyhomewood.co.uk.planespotter.model.Item;
import garyhomewood.co.uk.planespotter.planesgallery.GalleryActivity;

/**
 *
 */
public class PlanesFragment extends Fragment implements PlanesContract.View {

    private static final String KEY_ITEMS = "KEY_ITEMS";
    private static final String KEY_SELECTED_ITEM = "KEY_SELECTED_ITEM";

    @BindView(R.id.refresh_layout) SwipeRefreshLayout refreshLayout;

    private PlanesContract.UserActionsListener actionsListener;
    private PlanesAdapter adapter;
    ItemClickListener itemClickListener = new ItemClickListener() {
        @Override
        public void onItemClick(List<Item> items, int selectedItem) {
            actionsListener.openGallery(items, selectedItem);
        }
    };

    public static PlanesFragment newInstance() {
        Bundle args = new Bundle();
        PlanesFragment fragment = new PlanesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionsListener = new PlanesPresenter(this, Injector.providePlanesService());
        adapter = new PlanesAdapter(new ArrayList<Item>(), itemClickListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        actionsListener.loadPlanes();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_planes, container, false);
        ButterKnife.bind(this, root);

        int numberOfColumns = (getResources().getConfiguration().orientation == OrientationHelper.VERTICAL) ? 2 : 3;

        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.item_list);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), numberOfColumns));

        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                actionsListener.loadPlanes();
            }
        });

        return root;
    }

    @Override
    public void showPlanes(List<Item> items) {
        adapter.replaceData(items);
    }

    @Override
    public void showProgressIndicator(final boolean active) {
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.refresh_layout);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(active);
            }
        });
    }

    @Override
    public void showError(String message) {
        Snackbar.make(refreshLayout, message, BaseTransientBottomBar.LENGTH_SHORT).show();
    }

    @Override
    public void showGallery(List<Item> items, int selectedItem) {
        Intent intent = new Intent(getContext(), GalleryActivity.class);
        intent.putExtra(KEY_ITEMS, Parcels.wrap(items));
        intent.putExtra(KEY_SELECTED_ITEM, selectedItem);
        startActivity(intent);
    }

    public static class PlanesAdapter extends RecyclerView.Adapter<PlanesAdapter.ViewHolder> {
        private List<Item> items;
        private ItemClickListener itemClickListener;
        private Context context;

        PlanesAdapter(List<Item> planes, ItemClickListener itemClickListener) {
            this.items = planes;
            this.itemClickListener = itemClickListener;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            context = parent.getContext();
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_plane, parent, false);
            return new ViewHolder(v, itemClickListener, items);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Item plane = items.get(position);
            holder.title.setText(plane.getTitle());
            holder.description.setText(plane.getDescriptionText());
            Glide.with(context)
                    .load(plane.getOriginalUrl())
                    .fitCenter()
                    .into(holder.thumbnail);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        void replaceData(List<Item> planes) {
            this.items = planes;
            notifyDataSetChanged();
        }

        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            @BindView(R.id.item_title) TextView title;
            @BindView(R.id.item_description) TextView description;
            @BindView(R.id.item_thumbnail) ImageView thumbnail;
            ItemClickListener itemClickListener;
            List<Item> items;

            ViewHolder(View itemView, ItemClickListener itemClickListener, List<Item> items) {
                super(itemView);
                ButterKnife.bind(this, itemView);
                this.itemClickListener = itemClickListener;
                this.items = items;
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                itemClickListener.onItemClick(items, getAdapterPosition());
            }
        }
    }

    public interface ItemClickListener {
        void onItemClick(List<Item> items, int selectedItem);
    }
}
