package garyhomewood.co.uk.planespotter.api;

import garyhomewood.co.uk.planespotter.model.Rss;
import retrofit2.http.GET;
import rx.Observable;

/**
 *
 */
public interface PlanesService {
    @GET("search.php?output=rss")
    Observable<Rss> getPlanes();
}
