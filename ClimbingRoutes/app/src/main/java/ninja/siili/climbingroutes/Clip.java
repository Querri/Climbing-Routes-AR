package ninja.siili.climbingroutes;

import android.view.View;
import android.widget.Toast;

import com.google.ar.core.HitResult;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
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
    private RenderableHelper mRenderableHelper;

    private AnchorNode mAnchor;
    private TransformableNode mClip;
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
        mClip = new TransformableNode(transformationSystem);
        mClip.setParent(mAnchor);
        mClip.setRenderable(mRenderableHelper.getColoredClipRenderable(color));
        mClip.select();

        mClip.getScaleController().setMinScale(0.1f);
        mClip.getScaleController().setMaxScale(0.3f);

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
        Toast.makeText(mRenderableHelper.getContext(), "make info card", Toast.LENGTH_SHORT).show();
        if (mInfoCard == null && mClip != null) {
            mInfoCard = new Node();
            mInfoCard.setParent(mClip);
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


    /*
     * Update the Info card.
     * @param name String of the new name.
     * @param diffText Integer of the new difficulty.
     * @param diffColor Integer of the new difficulty color.
     * @param type String of the new route type.
     * @param isSitstart True if route has a sitstart.
     * @param startHoldCount Integer of the number of start holds, between 0 and 2.
     * @param isTopout True if route has a topout.
     *
    public boolean updateInfoCard(String name, String diffText, int diffColor, String type,
                               boolean isSitstart, int startHoldCount, boolean isTopout,
                               String notes) {
        if (mInfoCardView != null) {
            TextView nameTV = mInfoCardView.findViewById(R.id.name);
            TextView diffTV = mInfoCardView.findViewById(R.id.diff_number);
            TextView typeTV = mInfoCardView.findViewById(R.id.type);
            TextView sitstartIC = mInfoCardView.findViewById(R.id.sitstart);
            TextView holdcountIC = mInfoCardView.findViewById(R.id.start_hold_count);
            TextView topoutIC = mInfoCardView.findViewById(R.id.topout);
            TextView notesTV = mInfoCardView.findViewById(R.id.notes);

            if (nameTV == null || diffTV == null || typeTV == null || sitstartIC == null
                    || holdcountIC == null || topoutIC == null || notesTV == null)  {
                return false;
            }

            nameTV.setText(name);
            diffTV.setText(diffText);
            diffTV.setTextColor(diffColor);
            typeTV.setText(type);

            if (isSitstart) sitstartIC.setVisibility(View.VISIBLE);
            else sitstartIC.setVisibility(View.INVISIBLE);

            if (startHoldCount == 2) holdcountIC.setVisibility(View.VISIBLE);
            if (startHoldCount == 1) holdcountIC.setVisibility(View.VISIBLE);
            else holdcountIC.setVisibility(View.INVISIBLE);

            if (isTopout) topoutIC.setVisibility(View.VISIBLE);
            else topoutIC.setVisibility(View.INVISIBLE);

            notesTV.setText(notes);

            return true;
        }
        return false;
    }*/


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
        if (mLine != null && mClip != null && previousClip != null ) {
            Vector3 up = new Vector3(0.0f, 1.0f, 0.0f);
            Vector3 directionVector = getDirectionVector(previousClip.getClipNode(), mClip);

            float distance = directionVector.length();
            Quaternion rotation = Quaternion.lookRotation(directionVector, up);

            mLine.setWorldScale(new Vector3(0.03f, 0.03f, distance));
            mLine.setWorldPosition(mClip.getWorldPosition());
            mLine.setWorldRotation(rotation);
        }
    }


    public View getInfoCardView() {
        return mInfoCardView;
    }


    /**
     * Get Clip's Node.
     * @return TransformableNode of the clip.
     */
    private TransformableNode getClipNode() {
        return mClip;
    }


    /**
     * Check if Clip is currently selected.
     * @return True if selected.
     */
    public boolean isClipSelected() {
        return mClip.isSelected();
    }


    /**
     * Check if Clip is currently transforming.
     * @return True if transforming.
     */
    public boolean isClipTransforming() {
        return mClip.isTransforming();
    }


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


    /**
     * Change clip's and line's rederable's color.
     * @param newColor Integer of the new color.
     */
    public void changeColor(int newColor) {
        if (mClip != null) {
            mClip.setRenderable(mRenderableHelper.getColoredClipRenderable(newColor));
        }
        if (mLine != null) {
            mLine.setRenderable(mRenderableHelper.getColoredLineRenderable(newColor));
        }
    }
}
