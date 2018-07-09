package ninja.siili.climbingroutes;

import android.content.Context;

import com.google.ar.core.HitResult;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.ux.TransformationSystem;

import java.util.ArrayList;

/**
 * Route is an entity that consists of multiple Clips and a RouteInfo.
 */
public class Route {

    private Context mContext;
    private Scene mScene;
    TransformationSystem mTransformationSystem;
    RenderableHelper mRenderableHelper;

    private ArrayList<Clip> mClips = new ArrayList<>();


    /**
     * Constructor for Route.
     * @param context App's context.
     * @param scene ArFragment's Scene.
     * @param transformationSystem TransformationSystem for TransformableNodes.
     * @param renderableHelper RenderableHelper to provide correct Renderables.
     */
    public Route(Context context, Scene scene,
                 TransformationSystem transformationSystem, RenderableHelper renderableHelper) {
        mContext = context;
        mScene = scene;
        mTransformationSystem = transformationSystem;
        mRenderableHelper = renderableHelper;

        // TODO create object for storing route info
    }



    /**
     * Add a starting point for the route. Has the Node for RouteInfo as a child.
     * @param hit HitResult for the spot the user tapped.
     */
    public void addStartingpoint(HitResult hit) {
        // TODO get color from route info
        mClips.add(new Clip(mContext, mScene, hit, mTransformationSystem, mRenderableHelper,
                true, null, mContext.getColor(R.color.green)));
    }


    /**
     * Add new Clip to the route.
     * @param hit HitResult for the spot the user tapped.
     */
    public void addClip(HitResult hit) {
        // TODO get color from route info
        mClips.add(new Clip(mContext, mScene, hit, mTransformationSystem, mRenderableHelper,
                false, mClips.get(mClips.size()-1), mContext.getColor(R.color.green)));
    }


    // TODO clip moved

    // TODO change route color
}
