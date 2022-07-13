package helloAr.testAgentSupport;

import java.util.Map;

import eu.iv4xr.framework.extensions.pathfinding.Navigatable;
import eu.iv4xr.framework.mainConcepts.Iv4xrAgentState;
import nl.uu.cs.aplib.mainConcepts.Environment;

public class MyAgentState extends Iv4xrAgentState<Void> {

    @Override
    public MyAgentEnv env() {
        return (MyAgentEnv) super.env() ;
    }

    /**
     * We are not going to keep an Nav-graph, but will instead keep a layered-nav-graphs.
     */
    @Override
    public Navigatable<Void> worldNavigation() {
        throw new UnsupportedOperationException() ;
    }

    /**
     * We are not going to keep an Nav-graph, but will instead keep a layered-nav-graphs.
     */
    @Override
    public MyAgentState setWorldNavigation(Navigatable<Void> nav) {
        throw new UnsupportedOperationException() ;
    }

    @Override
    public MyAgentState setEnvironment(Environment env) {
        super.setEnvironment(env) ;
        return this ;
    }

    @Override
    public void updateState(String agentId) {
        super.updateState(agentId);
        // remove anchors that are no longer there:
        // wrong logic!

        //New: Removed anchors whose id is NOT in lastseen
        /*for(String id : env().lastSeen) {
            this.worldmodel.elements.remove(id) ;
        }*/
        Map elements = this.worldmodel.elements;
        boolean isInLastSeen = false;

        for (int i = 0; i < elements.size(); i++) {
            for (String id : env().lastSeen) {
                if(elements.get(i) == id) {
                    isInLastSeen = true;
                    break;
                }
            }
            if(isInLastSeen == false) {
                this.worldmodel.elements.remove(i);
            }
            isInLastSeen = false;
        }
    }

}
