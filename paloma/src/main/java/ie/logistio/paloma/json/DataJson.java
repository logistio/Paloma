package ie.logistio.paloma.json;

import com.google.gson.annotations.SerializedName;

/**
 * A JSON that only contains "data", which is how many APIs return their payload.
 */
public class DataJson<PayloadType> {

    @SerializedName("data")
    public PayloadType data;

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "data=" + data +
                '}';
    }

    public static <T> DataJson<T> create(T payload) {
        DataJson<T> dataJson = new DataJson<>();
        dataJson.data = payload;
        return dataJson;
    }
}
