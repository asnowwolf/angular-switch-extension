package live.angular.utils;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

class ExtensionSwitcherTest {

    ExtensionSwitcher switcher = new ExtensionSwitcher(null);

    @Test
    void getExtension() {
        assertThat(switcher.getExtension("a.component.spec.ts"), is("spec.ts"));
        assertThat(switcher.getExtension("a.component.ts"), is("ts"));
        assertThat(switcher.getExtension("ats"), is(""));
    }

    @Test
    void getNameWithoutExtension() {
        assertThat(switcher.getNameWithoutExtension("/home/a.spec.ts"), is("/home/a"));
        assertThat(switcher.getNameWithoutExtension("a.ts"), is("a"));
        assertThat(switcher.getNameWithoutExtension("ats"), is("ats"));
    }
}