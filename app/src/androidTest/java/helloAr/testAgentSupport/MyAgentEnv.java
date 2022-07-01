package helloAr.testAgentSupport;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;

import com.google.ar.core.Anchor;
import com.google.ar.core.examples.java.helloar.HelloArActivity;
import com.google.ar.core.examples.java.helloar.WrappedAnchor;

import java.util.LinkedList;
import java.util.List;

import eu.iv4xr.framework.mainConcepts.Iv4xrEnvironment;
import eu.iv4xr.framework.mainConcepts.WorldEntity;
import eu.iv4xr.framework.mainConcepts.WorldModel;

public class MyAgentEnv extends Iv4xrEnvironment {

    public HelloArActivity mainActivity;
    String agentId = "agent Smith";

    List<WrappedAnchor> seenAnchors = new LinkedList<>();

    int freshId = 0 ;


    public MyAgentEnv(HelloArActivity activity) {
        this.mainActivity = activity;
    }

    int getId(WrappedAnchor a) {
        for(int k = 0 ; k<seenAnchors.size(); k++) {
            if (a == seenAnchors.get(k))
                return k ;
        }
        return -1 ;
    }

    @Override
    public WorldModel observe(String agentId) {
        WorldModel wom = new WorldModel() ;
        wom.agentId = agentId ;

        List<WrappedAnchor> anchorsList = mainActivity.getAnchors();
        for(WrappedAnchor a : anchorsList) {

            int id_ = getId(a) ;
            if(id_ < 0)  {
                id_ = freshId ;
                freshId++ ;
            }

            Anchor anchor = a.getAnchor() ;

            String id = "A" + id_ ;
            WorldEntity e = new WorldEntity(id,"3DObj", true) ;
            e.properties.put("qx", anchor.getPose().qx()) ;
            e.properties.put("qy", anchor.getPose().qy()) ;

            e.properties.put("tx", anchor.getPose().tx()) ;
        }
        return wom ;
    }

    public WorldModel tapping(int x, int y, int sleep) throws InterruptedException {
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).click(x, y);
        Thread.sleep(sleep);
        return observe(agentId);
    }
}
