package live.angular.utils;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;

class ExtensionSwitcherTest {

    final ExtensionSwitcher switcher = new ExtensionSwitcher(null);

    @Test
    void getExtension() {
        MatcherAssert.assertThat(switcher.getExtension("a.component.spec.ts"), is("spec.ts"));
        MatcherAssert.assertThat(switcher.getExtension("a.component.ts"), is("ts"));
        MatcherAssert.assertThat(switcher.getExtension("ats"), is(""));
    }

    @Test
    void getNameWithoutExtension() {
        MatcherAssert.assertThat(switcher.getNameWithoutExtension("/home/a.spec.ts"), is("/home/a"));
        MatcherAssert.assertThat(switcher.getNameWithoutExtension("a.ts"), is("a"));
        MatcherAssert.assertThat(switcher.getNameWithoutExtension("ats"), is("ats"));
    }
}
