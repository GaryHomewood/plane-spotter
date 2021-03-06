package garyhomewood.co.uk.planespotter.planes;

import java.util.ArrayList;
import java.util.List;

import garyhomewood.co.uk.planespotter.api.PlanesService;
import garyhomewood.co.uk.planespotter.model.GalleryItem;
import garyhomewood.co.uk.planespotter.model.Item;
import garyhomewood.co.uk.planespotter.model.Rss;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 *
 */
class PlanesPresenter implements PlanesContract.UserActionsListener {

    private final PlanesContract.View planesView;
    private final PlanesService service;

    PlanesPresenter(PlanesContract.View planesView, PlanesService service) {
        this.planesView = planesView;
        this.service = service;
    }

    @Override
    public void loadPlanes() {
        planesView.showProgressIndicator(true);

        Observable<Rss> call = service.getPlanes();
        call.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Rss>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        planesView.showProgressIndicator(false);
                        planesView.showError(e.getMessage());
                    }

                    @Override
                    public void onNext(Rss rss) {
                        planesView.showProgressIndicator(false);
                        planesView.showPlanes(rss.getChannel().getItems());
                    }
                });
    }

    @Override
    public void openGallery(List<Item> items, int selectedItem) {
        planesView.showGallery(getItems(items), selectedItem);
    }

    private List<GalleryItem> getItems(List<Item> items) {
        List<GalleryItem> galleryItems = new ArrayList<>();
        for (Item item : items) {
            galleryItems.add(new GalleryItem(
                    item.title,
                    item.description,
                    item.subject,
                    item.thumbnail.getUrl()));
        }
        return galleryItems;
    }
}