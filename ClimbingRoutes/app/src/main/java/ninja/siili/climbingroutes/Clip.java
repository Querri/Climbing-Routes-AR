package ninja.siili.climbingroutes;

import android.view.View;
import android.widget.Toast;

import com.google.ar.core.HitResult;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.ar.sceneform.ux.TransformationSystem;

/** Clip represents a point in Route, visualised by a sphere.
 *  Route's first clip is always a start clip and has an info card attached to it,
 *  rest of the clips have a single line attached to them.
 */
public class Clip {
    private RenderableHelper mRenderableHelper;

    private AnchorNode mAnchor;
    private TransformableNode mTransformableNode;
    private Node mStaticNode;
    private Node mLine;
    private Node mInfoCard;
    private View mInfoCardView;


    /**
     * Constructor for the Clip.
     * @param transformationSystem TransformationSystem for Trasformable Nodes.
     * @param renderableHelper RenderableHelper class to help with Renderables.
     * @param hit HitResult for the spot the user tapped.
     * @param color Integer of the Route's color.
     * @param previousClip Route's previous Clip, null if this is the first one.
     */
    public Clip(TransformationSystem transformationSystem, RenderableHelper renderableHelper,
                HitResult hit, int color, Clip previousClip) {
        mRenderableHelper = renderableHelper;

        // Create anchor node.
        mAnchor = new AnchorNode(hit.createAnchor());
        mAnchor.setParent(mRenderableHelper.getScene());

        // Create a transformable node and add it to the anchor.
        mTransformableNode = new TransformableNode(transformationSystem);
        mTransformableNode.setParent(mAnchor);
        mTransformableNode.setRenderable(mRenderableHelper.getColoredClipRenderable(color));
        mTransformableNode.select();
        mTransformableNode.getScaleController().setMinScale(0.1f);
        mTransformableNode.getScaleController().setMaxScale(0.3f);

        // Create a static node and add it to the anchor.
        mStaticNode = new Node();
        mStaticNode.setParent(mAnchor);

        // If no previous clip, create info card instead of a line.
        if (previousClip == null) {
            createInfoCard();
        } else {
            createLine(previousClip, color);
        }
    }


    /**
     * Create info card ViewRenderable for the first Clip on Route.
     */
    private void createInfoCard() {
        if (mInfoCard == null && mTransformableNode != null) {
            mInfoCard = new Node();
            mInfoCard.setParent(mTransformableNode);
            mInfoCard.setLocalPosition(new Vector3(0.0f, 1.5f, 0.0f));
            mInfoCard.setLocalScale(new Vector3(4.0f, 4.0f, 4.0f));
            mInfoCard.setWorldRotation(mRenderableHelper.getScene().getCamera().getWorldRotation());

            // Build ViewRenderable.
            ViewRenderable.builder()
                    .setView(mRenderableHelper.getContext(), R.layout.route_info_card_view)
                    .build()
                    .thenAccept(
                            (renderable) -> {
                                mInfoCard.setRenderable(renderable);
                                mInfoCardView = renderable.getView();
                            })
                    .exceptionally(
                            (throwable) -> {
                                throw new AssertionError("Could not load card view.", throwable);
                            });
        }
    }


    /**
     * Create a line from this Clip to previous in Route.
     * @param previousClip Previous Clip in Route.
     * @param color Color for the line.
     */
    private void createLine(Clip previousClip, int color) {
        mLine = new Node();
        mLine.setParent(mAnchor);
        mLine.setRenderable(mRenderableHelper.getColoredLineRenderable(color));
        moveLine(previousClip);
    }


    /**
     * Move line alogside Clip.
     * @param previousClip Previous Clip in Route.
     */
    public void moveLine(Clip previousClip) {
        if (mLine != null && mTransformableNode != null && previousClip != null ) {
            Vector3 up = new Vector3(0.0f, 1.0f, 0.0f);
            Vector3 worldPosPrev = new Vector3(previousClip.getClipNode().getWorldPosition());
            Vector3 worldPosThis = new Vector3(mTransformableNode.getWorldPosition());

            Vector3 directionVector =  new Vector3(
                    worldPosThis.x - worldPosPrev.x,
                    worldPosThis.y - worldPosPrev.y,
                    worldPosThis.z - worldPosPrev.z);

            float distance = directionVector.length();
            Quaternion rotation = Quaternion.lookRotation(directionVector, up);

            mLine.setWorldScale(new Vector3(0.03f, 0.03f, distance));
            mLine.setWorldPosition(mTransformableNode.getWorldPosition());
            mLine.setWorldRotation(rotation);
        }
    }


    /**
     * Enable or disable clip's transforming.
     * @param enable True if enable, false if disable.
     */
    public void enableTransforming(boolean enable) {
        if (mTransformableNode != null && mStaticNode != null) {
            mTransformableNode.setEnabled(enable);

            if (enable) {
                // Hide the static node.
                mStaticNode.setRenderable(null);
                if (mInfoCard != null) {
                    // Change info card's parent, so that it moves with the transformable node.
                    mInfoCard.setParent(mTransformableNode);
                }

            } else {
                // Update and show the static node.
                mStaticNode.setRenderable(mTransformableNode.getRenderable());
                mStaticNode.setLocalScale(mTransformableNode.getLocalScale());
                mStaticNode.setLocalRotation(mTransformableNode.getLocalRotation());
                mStaticNode.setLocalPosition(mTransformableNode.getLocalPosition());
                if (mInfoCard != null) {
                    // Change info card's parent, so that it doesn't disable with trasformable node.
                    mInfoCard.setParent(mStaticNode);
                }
            }
        }
    }


    /**
     * Change clip's and line's rederable's color.
     * @param newColor Integer of the new color.
     */
    public void changeColor(int newColor) {
        if (mTransformableNode != null) {
            mTransformableNode.setRenderable(mRenderableHelper.getColoredClipRenderable(newColor));
        }
        if (mLine != null) {
            mLine.setRenderable(mRenderableHelper.getColoredLineRenderable(newColor));
        }
    }


    /**
     * Check if Clip is currently selected.
     * @return True if selected.
     */
    public boolean isClipSelected() {
        return mTransformableNode.isSelected();
    }


    /**
     * Check if Clip is currently transforming.
     * @return True if transforming.
     */
    public boolean isClipTransforming() {
        return mTransformableNode.isTransforming();
    }


    /**
     * Get Clip's Node. Used to get a vector from a Clip to another.
     * @return TransformableNode of the Clip.
     */
    private TransformableNode getClipNode() {
        return mTransformableNode;
    }


    /**
     * Get info card for updating.
     * @return View of the info card.
     */
    public View getInfoCardView() {
        return mInfoCardView;
    }
}
