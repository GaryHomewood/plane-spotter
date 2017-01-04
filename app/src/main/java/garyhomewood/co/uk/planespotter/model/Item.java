package garyhomewood.co.uk.planespotter.model;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 *
 */
@Parcel
@Root(name = "item", strict = false)
public class Item {
    @Element(name = "title")
    private String title;
    @Element(name = "description")
    private String description;
    @Element(name = "subject")
    private String subject;
    @Element(name = "thumbnail", required = false)
    private Thumbnail thumbnail;

    @Parcel
    public static class Thumbnail {
        @Attribute(name = "url")
        String url;

        public String getUrl() {
            return url;
        }
    }

    @ParcelConstructor
    public Item(
            @Element(name = "title") String title,
            @Element(name = "description") String description,
            @Element(name = "thumbnail", required = false) Thumbnail thumbnail,
            @Element(name = "subject") String subject) {
        this.title = title;
        this.description = description;
        this.thumbnail = thumbnail;
        this.subject = subject;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getSubject() {
        return subject;
    }

    public Thumbnail getThumbnail() {
        return thumbnail;
    }

    public String getDescriptionText() {
        return description.substring(description.indexOf("<br/>") + 5).trim()
                .replaceAll("\\n", " ")
                .replaceAll("[^a-zA-Z0-9 ]", "")
                .replaceAll("  ", " ");
    }

    public String getOriginalUrl() {
        return thumbnail.getUrl().replace("thumbnail", "original");
    }
}
