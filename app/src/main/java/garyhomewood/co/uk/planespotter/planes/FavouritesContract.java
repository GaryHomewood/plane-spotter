package garyhomewood.co.uk.planespotter.planes;

import java.util.List;

import garyhomewood.co.uk.planespotter.model.Favourite;
import garyhomewood.co.uk.planespotter.model.GalleryItem;

/**
 *
 */
interface FavouritesContract {
    interface View {
        void showFavourites(List<Favourite> favourites);
        void showProgressIndicator(final boolean active);
        void showError(String message);
        void showGallery(List<GalleryItem> galleryItems, int selectedItem);
    }

    interface UserActionsListener {
        void loadFavourites();
        void openGallery(List<Favourite> items, int selectedItem);
    }
}
