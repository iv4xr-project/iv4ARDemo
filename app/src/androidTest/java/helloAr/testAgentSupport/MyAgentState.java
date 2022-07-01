package helloAr.testAgentSupport;

import eu.iv4xr.framework.extensions.pathfinding.Navigatable;
import eu.iv4xr.framework.mainConcepts.Iv4xrAgentState;

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
}
