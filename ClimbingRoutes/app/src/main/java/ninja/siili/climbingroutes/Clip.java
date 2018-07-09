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

public class Clip {
    private Context mContext;
    private TransformationSystem mTransformationSystem;
    private RenderableHelper mRenderableHelper;

    private AnchorNode mAnchor;
    private TransformableNode mClip;

    private boolean isStartClip;


    public Clip(Context context, Scene scene, HitResult hit,
                TransformationSystem transformationSystem, RenderableHelper renderableHelper,
                boolean isStartClip, int color) {
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
            // TODO create line
        }
    }


    private TransformableNode getClipNode() {
        return mClip;
    }


    // TODO change clip color
}
