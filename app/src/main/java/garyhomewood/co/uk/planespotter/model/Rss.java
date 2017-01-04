package garyhomewood.co.uk.planespotter.model;


import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.NamespaceList;
import org.simpleframework.xml.Root;

/**
 *
 */
@Root(name = "rss", strict = false)
@NamespaceList({
        @Namespace(reference = "http://www.w3.org/2005/Atom", prefix = "atom"),
        @Namespace(reference = "http://purl.org/dc/elements/1.1/", prefix = "dc"),
        @Namespace(reference = "http://search.yahoo.com/mrss/", prefix = "media")
})
public class Rss {
    @Element(name = "channel")
    private Channel channel;

    public Channel getChannel() {
        return channel;
    }
}
