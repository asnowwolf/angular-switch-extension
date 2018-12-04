package live.angular.utils;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

class ExtensionSwitcherTest {

    ExtensionSwitcher switcher = new ExtensionSwitcher(null);

    @Test
    void groupIndexOf() {
        assertThat(switcher.groupIndexOf("ts"), is(0));
        assertThat(switcher.groupIndexOf("css"), is(1));
        assertThat(switcher.groupIndexOf("scss"), is(1));
        assertThat(switcher.groupIndexOf("html"), is(2));
        assertThat(switcher.groupIndexOf("spec.ts"), is(3));
    }

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

    @Test
    void nextIndexOf() {
        assertThat(switcher.nextIndexOf(1, 1), is(2));
        assertThat(switcher.nextIndexOf(3, 1), is(0));
        assertThat(switcher.nextIndexOf(1, -1), is(0));
        assertThat(switcher.nextIndexOf(0, -1), is(3));
    }
}