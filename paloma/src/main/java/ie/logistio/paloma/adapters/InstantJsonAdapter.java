package ie.logistio.paloma.adapters;

import android.support.annotation.NonNull;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import org.threeten.bp.Instant;

import java.io.IOException;

/**
 * Converts {@link Instant} to the required String representation for
 * API requests.
 * Currently, this format is "yyyy-MM-dd HH:mm:ss" [UTC], but as these details are always liable
 * to change, conversion should always be performed via this adapter if making Xpreso-API requests.
 * <p>
 * Note that DateTimes created by this adapter will have the driver's TimeZone by default, but
 * the String representaion in the JSON will always be in UTC, and will not have any TimeZone
 * information associated with it (since the API assumes all times to be UTC).
 * </p>
 */
public class InstantJsonAdapter extends TypeAdapter<Instant> {

    private final InstantConverter converter;

    public InstantJsonAdapter(InstantConverter converter) {
        this.converter = converter;
    }

    @Override
    public void write(JsonWriter out, Instant value) throws IOException {
        String apiDateTime = converter.convertFromInstantToTimestamp(value);
        out.value(apiDateTime);
    }

    @Override
    public Instant read(JsonReader in) throws IOException {
        String timestamp = in.nextString();

        if (timestamp == null) {
            // Returning null here will cause Gson to throw an
            // IllegalStateException if the field being converted
            // is marked with @NonNull.
            return null;
        }

        return converter.convertFromTimestampToInstant(timestamp);
    }

    public interface InstantConverter {
        @NonNull
        String convertFromInstantToTimestamp(@NonNull Instant instant);

        @NonNull
        Instant convertFromTimestampToInstant(@NonNull String timestamp);
    }
}