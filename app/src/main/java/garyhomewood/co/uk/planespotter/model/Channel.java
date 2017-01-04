package garyhomewood.co.uk.planespotter.model;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 *
 */
@Root(name = "channel", strict = false)
public class Channel {
    @ElementList(name = "item", inline = true)
    private List<Item> items;

    public List<Item> getItems() {
        return items;
    }
}
