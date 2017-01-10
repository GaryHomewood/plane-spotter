package garyhomewood.co.uk.planespotter.planes;

import java.util.ArrayList;
import java.util.List;

import garyhomewood.co.uk.planespotter.model.Favourite;
import garyhomewood.co.uk.planespotter.model.GalleryItem;
import io.realm.Realm;
import io.realm.Sort;

/**
 *
 */
class FavouritesPresenter implements FavouritesContract.UserActionsListener{

    private final FavouritesContract.View favouritesView;
    private final Realm realm;

    FavouritesPresenter(FavouritesContract.View planesView, Realm realm) {
        this.favouritesView = planesView;
        this.realm = realm;
    }

    @Override
    public void loadFavourites() {
        favouritesView.showProgressIndicator(true);

        List<Favourite> favourites = new ArrayList<>();
        favourites.addAll(realm.where(Favourite.class).findAllSorted("createdDate", Sort.DESCENDING));

        favouritesView.showFavourites(favourites);
        favouritesView.showProgressIndicator(false);
    }

    @Override
    public void openGallery(List<Favourite> items, int selectedItem) {
        favouritesView.showGallery(getItems(items), selectedItem);
    }

    private List<GalleryItem> getItems(List<Favourite> favourites) {
        List<GalleryItem> galleryItems = new ArrayList<>();
        for (Favourite favourite : favourites) {
            galleryItems.add(new GalleryItem(
                    favourite.getTitle(),
                    favourite.getDescription(),
                    favourite.getSubject(),
                    favourite.getThumbnail()));
        }
        return galleryItems;
    }
}
