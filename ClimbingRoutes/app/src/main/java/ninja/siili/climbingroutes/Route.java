package ninja.siili.climbingroutes;

import android.content.Context;
import android.widget.Toast;

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
    private int mSelectedClipPosition = 0;


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


    /**
     * Find selected Clip and move the lines adjacent to it.
     * Recursion <3
     */
    public void moveLinesIfNeeded() {
        if (mClips.get(mSelectedClipPosition).isClipSelected()) {
            // Selected Clip hasn't changed, move lines adjacent to it if the Clip is transforming.
            if (mClips.get(mSelectedClipPosition).isClipTransforming()) {
                // Move line below Clip.
                if (mSelectedClipPosition != 0) {
                    mClips.get(mSelectedClipPosition).moveLine(mClips.get(mSelectedClipPosition - 1));
                }
                // Move line above Clip.
                if (mSelectedClipPosition != mClips.size()-1) {
                    mClips.get(mSelectedClipPosition + 1).moveLine(mClips.get(mSelectedClipPosition));
                }
            }
        } else {
            // Selected Clip has changed, find the new one and do recursion.
            for (int i = 0; i < mClips.size(); i++) {
                if (mClips.get(i).isClipSelected()) {
                    mSelectedClipPosition = i;
                    moveLinesIfNeeded();
                    break;
                }
            }
        }
    }


    /**
     * Change the route's color.
     */
    public void changeRouteColor() {
        for (Clip clip : mClips) {
            // TODO get color from route info
            clip.changeColor(mContext.getColor(R.color.red));
        }
    }
}
