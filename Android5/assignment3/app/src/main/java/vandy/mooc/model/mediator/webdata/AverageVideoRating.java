package vandy.mooc.model.mediator.webdata;

/**
 * Created by Cecilia Urbina on 3/09/15.
 */
public class AverageVideoRating {

    private final double rating;

    private final long videoId;

    private final int totalRatings;

    public AverageVideoRating(double rating, long videoId, int totalRatings) {
        super();
        this.rating = rating;
        this.videoId = videoId;
        this.totalRatings = totalRatings;
    }

    public double getRating() {
        return rating;
    }

    public long getVideoId() {
        return videoId;
    }

    public int getTotalRatings() {
        return totalRatings;
    }

}
