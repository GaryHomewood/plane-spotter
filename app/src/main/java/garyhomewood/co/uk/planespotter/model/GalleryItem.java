package garyhomewood.co.uk.planespotter.model;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

/**
 * View model for gallery
 */
@Parcel
public class GalleryItem {
    public String title;
    public String description;
    public String subject;
    public String thumbnail;

    @ParcelConstructor
    public GalleryItem(String title, String description, String subject, String thumbnail) {
        this.title = title;
        this.description = description;
        this.subject = subject;
        this.thumbnail = thumbnail;
    }

    public String getOriginalUrl() {
        return thumbnail.replace("thumbnail", "original");
    }
}
