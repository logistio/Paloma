package ie.logistio.paloma.mock;

import android.support.annotation.NonNull;

import java.util.concurrent.Executor;

/**
 *
 */
public interface ApiMocker<ApiType> {
    ApiType createMockApi();

    Class<ApiType> getType();

    void setEnqueueExecutor(@NonNull Executor executor);

    void clearRequestHistory();
}
