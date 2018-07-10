package ninja.siili.climbingroutes;

import android.content.Context;

import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.rendering.ModelRenderable;

/** Stores Renderables for clips and lines. */
public class RenderableHelper {

    private Context mContext;
    private Scene mScene;

    private ModelRenderable mClipRenderableGreen;
    private ModelRenderable mClipRenderableYellow;
    private ModelRenderable mClipRenderableOrange;
    private ModelRenderable mClipRenderableRed;
    private ModelRenderable mLineRenderableGreen;
    private ModelRenderable mLineRenderableYellow;
    private ModelRenderable mLineRenderableOrange;
    private ModelRenderable mLineRenderableRed;


    public RenderableHelper(Context context, Scene scene,
                            ModelRenderable greenclip, ModelRenderable yellowclip,
                            ModelRenderable orangeclip, ModelRenderable redclip,
                            ModelRenderable greenline, ModelRenderable yellowline,
                            ModelRenderable orangeline, ModelRenderable redline) {
        mContext = context;
        mScene = scene;
        mClipRenderableGreen = greenclip;
        mClipRenderableYellow = yellowclip;
        mClipRenderableOrange = orangeclip;
        mClipRenderableRed = redclip;
        mLineRenderableGreen = greenline;
        mLineRenderableYellow = yellowline;
        mLineRenderableOrange = orangeline;
        mLineRenderableRed = redline;
    }


    /**
     * Pass Context to Clip.
     * @return Context of the app:
     */
    public Context getContext() {
        return mContext;
    }


    /**
     * Pass Scene to Clip.
     * @return Scene.
     */
    public Scene getScene() {
        return mScene;
    }


    /**
     * Get correctrly colored clip renderable for Route.
     * @param color The color needed.
     * @return ModelRenderable of a colored clip.
     */
    public ModelRenderable getColoredClipRenderable(int color) {
        if (color == mContext.getColor(R.color.green)) {
            return mClipRenderableGreen;
        } else if (color == mContext.getColor(R.color.yellow)) {
            return mClipRenderableYellow;
        } else if (color == mContext.getColor(R.color.orange)) {
            return mClipRenderableOrange;
        } else {
            return mClipRenderableRed;
        }
    }


    /**
     * Get correctly colored line renderable for Route.
     * @param color The color needed.
     * @return ModelRenderable of a colored line.
     */
    public ModelRenderable getColoredLineRenderable(int color) {
        if (color == mContext.getColor(R.color.green)) {
            return mLineRenderableGreen;
        } else if (color == mContext.getColor(R.color.yellow)) {
            return mLineRenderableYellow;
        } else if (color == mContext.getColor(R.color.orange)) {
            return mLineRenderableOrange;
        } else {
            return mLineRenderableRed;
        }
    }
}

