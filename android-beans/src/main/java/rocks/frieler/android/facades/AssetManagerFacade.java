package rocks.frieler.android.facades;

import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;

/**
 * Facade that wraps the {@link AssetManager} to ease working with assets and mocking them in unit-tests.
 *
 * @see AssetManager
 *
 * @author Christopher Frieler
 */
public class AssetManagerFacade {
    private final AssetManager assetManager;

    public AssetManagerFacade(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    public String[] list(String path) throws IOException {
        return assetManager.list(path);
    }

    public InputStream open(String filename) throws IOException {
        return assetManager.open(filename);
    }
}
