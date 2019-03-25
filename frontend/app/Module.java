import com.google.inject.AbstractModule;
import datadog.examples.*;

public class Module extends AbstractModule {

    @Override
    public void configure() {
        bind(BackendClient.class).toInstance(new BackendClient());
    }
}
