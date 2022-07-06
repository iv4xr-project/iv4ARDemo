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

    List<WrappedAnchor> seenAnchors = new LinkedList<>();

    List<String> lastSeen = new LinkedList<>() ;

    int freshId = 0 ;

    int timeStamp = 0 ;


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


    public WorldModel observe(String agentId) {

        WorldModel wom = new WorldModel() ;
        wom.agentId = agentId ;
        wom.timestamp = timeStamp ;

        List<WrappedAnchor> anchorsList = mainActivity.getAnchors();
        lastSeen.clear();
        for(WrappedAnchor a : anchorsList) {

            int id_ = getId(a) ;
            if(id_ < 0)  {
                id_ = freshId ;
                freshId++ ;
            }

            Anchor anchor = a.getAnchor() ;

            String id = "A" + id_ ;
            lastSeen.add(id) ;
            WorldEntity e = new WorldEntity(id,"3DObj", true) ;
            e.timestamp = wom.timestamp ;
            e.properties.put("qx", anchor.getPose().qx()) ;
            e.properties.put("qy", anchor.getPose().qy()) ;

            e.properties.put("tx", anchor.getPose().tx()) ;
            // complete this :)
            wom.elements.put(e.id,e) ;
        }
        this.timeStamp++ ;
        return wom ;
    }

    public WorldModel tapScreen(String agentId, int x, int y, int sleep) throws InterruptedException {
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).click(x, y);
        Thread.sleep(sleep);
        return observe(agentId);
    }

    // add more actions
}
