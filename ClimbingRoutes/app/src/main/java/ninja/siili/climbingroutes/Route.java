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
    TransformationSystem mTransformationSystem;
    RenderableHelper mRenderableHelper;

    private ArrayList<Clip> mClips = new ArrayList<>();
    private int mSelectedClipPosition = 0;

    private RouteInfo mRouteInfo;


    /**
     * Constructor for Route.
     * @param context App's context.
     * @param transformationSystem TransformationSystem for TransformableNodes.
     * @param renderableHelper RenderableHelper to provide correct Renderables.
     */
    public Route(Context context,
                 TransformationSystem transformationSystem, RenderableHelper renderableHelper) {
        mContext = context;
        mTransformationSystem = transformationSystem;
        mRenderableHelper = renderableHelper;

        mRouteInfo = new RouteInfo(context);

        // TODO create object for storing route info
    }


    /**
     * Add new Clip to the route.
     * @param hit HitResult for the spot the user tapped.
     */
    public void addClip(HitResult hit) {

        // If no previous clip, pass null.
        Clip previousClip = null;
        if (mClips.size() > 0) {
            previousClip = mClips.get(mClips.size()-1);
        }

        mClips.add(new Clip(mTransformationSystem, mRenderableHelper, hit,
                mRouteInfo.getDifficultyColor(), previousClip));
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
            clip.changeColor(mRouteInfo.getDifficultyColor());
        }
    }


    /**
     * Update Route info.
     * @param name String of new name.
     * @param difficulty Integer of new difficulty.
     * @param isBoulder True if Route's type is boulder.
     * @param isSport True if Route's type is sport.
     * @param isTrad True if Route's type is trad.
     * @param isSitstart True if Route has a sit start.
     * @param isTopOut True if Route has a top out.
     * @param startHoldCount Integer of Route's start hold count, from 0 to 2.
     * @param notes String of additional notes about the Route.
     */
    public void updateRouteInfo(String name, int difficulty,
                                boolean isBoulder, boolean isSport, boolean isTrad,
                                boolean isSitstart, boolean isTopOut, int startHoldCount,
                                String notes) {
        mRouteInfo.setName(name);
        mRouteInfo.setDifficulty(difficulty);
        mRouteInfo.setIsBoulder(isBoulder);
        mRouteInfo.setIsSport(isSport);
        mRouteInfo.setIsTrad(isTrad);
        mRouteInfo.setIsSitstart(isSitstart);
        mRouteInfo.setIsTopout(isTopOut);
        mRouteInfo.setStartHoldCount(startHoldCount);
        mRouteInfo.setNotes(notes);

        // TODO check this
        boolean valuesFine = mRouteInfo.checkValuesAreAllRight();

        // TODO listener for this
        changeRouteColor();
    }
}
