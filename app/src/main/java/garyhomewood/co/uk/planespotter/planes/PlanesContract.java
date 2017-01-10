package garyhomewood.co.uk.planespotter.planes;

import java.util.List;

import garyhomewood.co.uk.planespotter.model.GalleryItem;
import garyhomewood.co.uk.planespotter.model.Item;

/**
 *
 */
interface PlanesContract {
    interface View {
        void showPlanes(List<Item> items);
        void showProgressIndicator(final boolean active);
        void showError(String message);
        void showGallery(List<GalleryItem> items, int selectedItem);
    }

    interface UserActionsListener {
        void loadPlanes();
        void openGallery(List<Item> items, int selectedItem);
    }
}
