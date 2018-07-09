package ninja.siili.climbingroutes;

import android.content.Context;

import com.google.ar.core.HitResult;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.ar.sceneform.ux.TransformationSystem;

/** Clip represents a point in Route, visualised by a sphere.
 *  Route's first clip is always a start clip and has an info card attached to it,
 *  rest of the clips have a single line attached to them.
 */
public class Clip {
    private Context mContext;
    private TransformationSystem mTransformationSystem;
    private RenderableHelper mRenderableHelper;

    private AnchorNode mAnchor;
    private TransformableNode mClip;
    private Node mLine;

    private boolean isStartClip;


    /**
     * Constructor for Clip.
     * @param context App's context.
     * @param scene ArFragment's Scene.
     * @param hit HitResult for the spot the user tapped.
     * @param transformationSystem TransformationSystem for TransformableNodes.
     * @param renderableHelper RenderableHelper to provide correct Renderables.
     * @param isStartClip If Clip is the first in on Route.
     * @param previousClip Previous Clip in Route.
     * @param color Integer of the Route's color.
     */
    public Clip(Context context, Scene scene, HitResult hit,
                TransformationSystem transformationSystem, RenderableHelper renderableHelper,
                boolean isStartClip, Clip previousClip, int color) {
        mContext = context;
        mTransformationSystem = transformationSystem;
        mRenderableHelper = renderableHelper;
        this.isStartClip = isStartClip;

        // Create anchor node.
        mAnchor = new AnchorNode(hit.createAnchor());
        mAnchor.setParent(scene);

        // Create a transformable node and add it to the anchor.
        mClip = new TransformableNode(mTransformationSystem);
        mClip.setParent(mAnchor);
        mClip.setRenderable(mRenderableHelper.getColoredClipRenderable(color));
        mClip.select();

        mClip.getScaleController().setMinScale(0.1f);
        mClip.getScaleController().setMaxScale(0.3f);

        if (isStartClip) {
            // TODO create info card
        } else {
            createLine(previousClip, color);
        }
    }


    /**
     * Get Clip's Node.
     * @return TransformableNode of the clip.
     */
    private TransformableNode getClipNode() {
        return mClip;
    }


    /**
     * Create a line from this Clip to previous in Route.
     * @param previousClip Previous Clip in Route.
     * @param color Color for the line.
     */
    private void createLine(Clip previousClip, int color) {
        if (previousClip != null) {
            Vector3 up = new Vector3(0.0f, 1.0f, 0.0f);
            Vector3 directionVector = getDirectionVector(previousClip.getClipNode(), mClip);

            float distance = directionVector.length();
            Quaternion rotation = Quaternion.lookRotation(directionVector, up);

            mLine = new Node();
            mLine.setParent(mAnchor);
            mLine.setRenderable(mRenderableHelper.getColoredLineRenderable(color));
            mLine.setWorldScale(new Vector3(0.03f, 0.03f, distance));
            mLine.setWorldPosition(mClip.getWorldPosition());
            mLine.setWorldRotation(rotation);
        }
    }

    // TODO move line


    /**
     * Get a direction vector from previous Node to this Node.
     * @param prevNode Previous Clip's Node.
     * @param thisNode This Clip's Node.
     * @return Vector3 from Node to Node.
     */
    private Vector3 getDirectionVector(Node prevNode, Node thisNode) {
        Vector3 worldPosPrev = new Vector3(prevNode.getWorldPosition());
        Vector3 worldPosThis = new Vector3(thisNode.getWorldPosition());

        return new Vector3(
                worldPosThis.x - worldPosPrev.x,
                worldPosThis.y - worldPosPrev.y,
                worldPosThis.z - worldPosPrev.z);
    }


    // TODO change clip color
}
