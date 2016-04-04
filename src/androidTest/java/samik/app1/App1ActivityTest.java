package samik.app1;

import android.test.ActivityInstrumentationTestCase2;

/**
 * This is a simple framework for a test of an Application.  See
 * {@link android.test.ApplicationTestCase ApplicationTestCase} for more information on
 * how to write and extend Application tests.
 * <p/>
 * To run this test, you can type:
 * adb shell am instrument -w \
 * -e class samik.app1.App1ActivityTest \
 * samik.app1.tests/android.test.InstrumentationTestRunner
 */
public class App1ActivityTest extends ActivityInstrumentationTestCase2<App1Activity> {

    public App1ActivityTest() {
        super("samik.app1", App1Activity.class);
    }

}
