package helloAr.testAgentSupport;

import android.app.Activity;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;

import com.google.ar.core.examples.java.helloar.WrappedAnchor;

import java.util.List;

import eu.iv4xr.framework.mainConcepts.Iv4xrEnvironment;
import eu.iv4xr.framework.mainConcepts.WorldModel;

public class MyAgentEnv extends Iv4xrEnvironment {

    public Activity activity;
    String agentId = "agent Smith";

    public MyAgentEnv(Activity activity) {
        this.activity = activity;
    }

    @Override
    public WorldModel observe(String agentId) {
        List<WrappedAnchor> anchorsList = activity.getAnchors();
        return null;
    }

    public WorldModel tapping(int x, int y, int sleep) throws InterruptedException {
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).click(x, y);
        Thread.sleep(sleep);
        return observe(agentId);
    }
}
