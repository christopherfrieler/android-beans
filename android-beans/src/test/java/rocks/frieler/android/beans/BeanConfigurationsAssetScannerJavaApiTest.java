package rocks.frieler.android.beans;

import android.content.Context;
import android.content.res.AssetManager;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BeanConfigurationsAssetScannerJavaApiTest {
	@Mock
	private Context context;
	@InjectMocks
	private BeanConfigurationsAssetScanner beanConfigurationsAssetScanner;

	@Mock
	private AssetManager assetManager;

	@Test
	public void canInstantiateAJavaBeanConfiguration() throws IOException {
		when(assetManager.list(BeanConfigurationsAssetScanner.BEAN_CONFIGURATIONS_ASSET_PATH)).thenReturn(new String[] { "beans.txt" });
		when(assetManager.open(BeanConfigurationsAssetScanner.BEAN_CONFIGURATIONS_ASSET_PATH + "/beans.txt"))
				.thenReturn(new ByteArrayInputStream(("rocks.frieler.android.beans.BeanConfigurationsAssetScannerJavaApiTest$AJavaBeanConfiguration\n").getBytes()));

		List<BeanConfiguration> beanConfigurations = new BeanConfigurationsAssetScanner(context).scan(assetManager);

		assertThat(beanConfigurations, hasItem(instanceOf(AJavaBeanConfiguration.class)));
	}

	public static class AJavaBeanConfiguration extends BeanConfiguration {
		@NotNull
		@Override
		public List<BeanDefinition<?>> getBeanDefinitions() {
			return Collections.emptyList();
		}
	}

	@Test
	public void canInstantiateAJavaBeanConfigurationNeedingTheContext() throws IOException {
		when(assetManager.list(BeanConfigurationsAssetScanner.BEAN_CONFIGURATIONS_ASSET_PATH)).thenReturn(new String[] { "beans.txt" });
		when(assetManager.open(BeanConfigurationsAssetScanner.BEAN_CONFIGURATIONS_ASSET_PATH + "/beans.txt"))
				.thenReturn(new ByteArrayInputStream(("rocks.frieler.android.beans.BeanConfigurationsAssetScannerJavaApiTest$AJavaBeanConfigurationNeedingTheContext\n").getBytes()));

		List<BeanConfiguration> beanConfigurations = new BeanConfigurationsAssetScanner(context).scan(assetManager);

		assertThat(beanConfigurations, hasItem(instanceOf(AJavaBeanConfigurationNeedingTheContext.class)));
	}

	public static class AJavaBeanConfigurationNeedingTheContext extends BeanConfiguration {
		public AJavaBeanConfigurationNeedingTheContext(Context context) {}

		@NotNull
		@Override
		public List<BeanDefinition<?>> getBeanDefinitions() {
			return Collections.emptyList();
		}
	}
}
