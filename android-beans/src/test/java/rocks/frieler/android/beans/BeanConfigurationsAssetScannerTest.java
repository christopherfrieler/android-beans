package rocks.frieler.android.beans;

import android.content.Context;
import android.content.res.AssetManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BeanConfigurationsAssetScannerTest {
    private Context context = mock(Context.class);
    private BeanConfigurationsAssetScanner beanConfigurationsAssetScanner = new BeanConfigurationsAssetScanner(context);

    @Mock
    private AssetManager assets;

    @Test
    public void testScanReturnsListOfBeanConfigurations() throws IOException {
        when(assets.list(BeanConfigurationsAssetScanner.BEAN_CONFIGURATIONS_ASSET_PATH)).thenReturn(new String[] { "beans1.txt", "beans2.txt" });
        when(assets.open(BeanConfigurationsAssetScanner.BEAN_CONFIGURATIONS_ASSET_PATH + "/beans1.txt")).thenReturn(new ByteArrayInputStream(
                ("rocks.frieler.android.beans.BeanConfigurationsAssetScannerTest$ABeanConfiguration\n" +
                 "rocks.frieler.android.beans.BeanConfigurationsAssetScannerTest$ABeanConfigurationNeedingTheContext\n").getBytes()));
        when(assets.open(BeanConfigurationsAssetScanner.BEAN_CONFIGURATIONS_ASSET_PATH + "/beans2.txt")).thenReturn(new ByteArrayInputStream(
                ("rocks.frieler.android.beans.BeanConfigurationsAssetScannerTest$AnotherBeanConfiguration\n").getBytes()));

        List<? extends BeanConfiguration> beanConfigurations = beanConfigurationsAssetScanner.scan(assets);

        assertThat(beanConfigurations.size(), is(3));
        assertThat(beanConfigurations.get(0), is(instanceOf(ABeanConfiguration.class)));
        assertThat(beanConfigurations.get(1), is(instanceOf(ABeanConfigurationNeedingTheContext.class)));
        assertThat(beanConfigurations.get(2), is(instanceOf(AnotherBeanConfiguration.class)));
    }

    @Test(expected = BeanInstantiationException.class)
    public void testScanThrowsBeanInstantiationExceptionWhenListingTheAssetsFails() throws IOException {
        when(assets.list(BeanConfigurationsAssetScanner.BEAN_CONFIGURATIONS_ASSET_PATH)).thenThrow(IOException.class);

        beanConfigurationsAssetScanner.scan(assets);
    }

    @Test(expected = BeanInstantiationException.class)
    public void testScanThrowsBeanInstantiationExceptionWhenReadingTheAssetFails() throws IOException {
        when(assets.list(BeanConfigurationsAssetScanner.BEAN_CONFIGURATIONS_ASSET_PATH)).thenReturn(new String[] { "beans.txt" });
        when(assets.open(BeanConfigurationsAssetScanner.BEAN_CONFIGURATIONS_ASSET_PATH + "/beans.txt")).thenThrow(IOException.class);

        beanConfigurationsAssetScanner.scan(assets);
    }

    @Test(expected = BeanInstantiationException.class)
    public void testScanThrowsBeanInstantiationExceptionWhenSpecifiedClassIsNotFound() throws IOException {
        when(assets.list(BeanConfigurationsAssetScanner.BEAN_CONFIGURATIONS_ASSET_PATH)).thenReturn(new String[] { "beans.txt" });
        when(assets.open(BeanConfigurationsAssetScanner.BEAN_CONFIGURATIONS_ASSET_PATH + "/beans.txt"))
                .thenReturn(new ByteArrayInputStream("something.ThatIsNotAClass\n".getBytes()));

        beanConfigurationsAssetScanner.scan(assets);
    }

    @Test(expected = BeanInstantiationException.class)
    public void testScanThrowsBeanInstantiationExceptionWhenSpecifiedClassIsNotABeanConfiguration() throws IOException {
        when(assets.list(BeanConfigurationsAssetScanner.BEAN_CONFIGURATIONS_ASSET_PATH)).thenReturn(new String[] { "beans.txt" });
        when(assets.open(BeanConfigurationsAssetScanner.BEAN_CONFIGURATIONS_ASSET_PATH + "/beans.txt"))
                .thenReturn(new ByteArrayInputStream("rocks.frieler.android.beans.BeanConfigurationsAssetScannerTest\n".getBytes()));

        beanConfigurationsAssetScanner.scan(assets);
    }

    public static class ABeanConfiguration extends BeanConfiguration {
        @Override
        public void defineBeans(BeansCollector beansCollector) {}
    }

    static class ABeanConfigurationNeedingTheContext extends BeanConfiguration {
        public ABeanConfigurationNeedingTheContext(Context context) {}

        @Override
        public void defineBeans(BeansCollector beansCollector) {}
    }

    public static class AnotherBeanConfiguration extends BeanConfiguration {
        @Override
        public void defineBeans(BeansCollector beansCollector) {}
    }
}
