package ninja.siili.climbingroutes;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ArActivity extends AppCompatActivity {

    private static final String TAG = ArActivity.class.getSimpleName();

    private ArFragment arFragment;
    private Scene mScene;

    private RenderableHelper mRenderableHelper;
    private GestureDetector gestureDetector;

    private Route mActiveRoute;
    private boolean addRouteMode = false;
    private boolean editRouteMode = false;

    private boolean hasFinishedLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);

        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);
        mScene = arFragment.getArSceneView().getScene();

        // Build all the models.
        CompletableFuture<ModelRenderable> clipStageGreen =
                ModelRenderable.builder().setSource(this, Uri.parse("sphere_green.sfb")).build();
        CompletableFuture<ModelRenderable> clipStageYellow =
                ModelRenderable.builder().setSource(this, Uri.parse("sphere_yellow.sfb")).build();
        CompletableFuture<ModelRenderable> clipStageOrange =
                ModelRenderable.builder().setSource(this, Uri.parse("sphere_orange.sfb")).build();
        CompletableFuture<ModelRenderable> clipStageRed =
                ModelRenderable.builder().setSource(this, Uri.parse("sphere_red.sfb")).build();
        CompletableFuture<ModelRenderable> lineStageGreen =
                ModelRenderable.builder().setSource(this, Uri.parse("cylinder_green.sfb")).build();
        CompletableFuture<ModelRenderable> lineStageYellow =
                ModelRenderable.builder().setSource(this, Uri.parse("cylinder_yellow.sfb")).build();
        CompletableFuture<ModelRenderable> lineStageOrange =
                ModelRenderable.builder().setSource(this, Uri.parse("cylinder_orange.sfb")).build();
        CompletableFuture<ModelRenderable> lineStageRed =
                ModelRenderable.builder().setSource(this, Uri.parse("cylinder_red.sfb")).build();

        CompletableFuture.allOf(
                clipStageGreen,
                clipStageYellow,
                clipStageOrange,
                clipStageRed,
                lineStageGreen,
                lineStageYellow,
                lineStageOrange,
                lineStageRed)
                .handle((notUsed, throwable) -> {

                    if (throwable != null) {
                        Toast.makeText(this, "Failed to load renderables", Toast.LENGTH_LONG).show();
                        return null;
                    }

                    try {
                        // Helper handles renderables from now on.
                        mRenderableHelper = new RenderableHelper(this,
                                clipStageGreen.get(), clipStageYellow.get(), clipStageOrange.get(), clipStageRed.get(),
                                lineStageGreen.get(), lineStageYellow.get(), lineStageOrange.get(), lineStageRed.get());
                        hasFinishedLoading = true;

                    } catch (InterruptedException | ExecutionException ex) {
                        Toast.makeText(this, "Failed to load renderables", Toast.LENGTH_LONG).show();
                    }

                    return null;
                });

        // Listener for taps on AR Planes.
        // TODO change to Points
        arFragment.setOnTapArPlaneListener(
                (HitResult hit, Plane plane, MotionEvent motionEvent) -> {
                    if (!hasFinishedLoading) {
                        return;
                    }

                    if (plane.getType() != Plane.Type.HORIZONTAL_UPWARD_FACING) {
                        return;
                    }

                    if (mActiveRoute == null) {
                        placeNewRoute(hit);
                    } else {
                        placeClipToActiveRoute(hit);
                    }
                });

        arFragment.getArSceneView().getScene().setOnUpdateListener(
                frameTime -> {

                    Frame frame = arFragment.getArSceneView().getArFrame();
                    if (frame == null) {
                        return;
                    }

                    if (frame.getCamera().getTrackingState() != TrackingState.TRACKING) {
                        return;
                    }

                    if (mActiveRoute != null && editRouteMode) {
                        //mActiveRoute.moveLinesIfNeeded();
                        mActiveRoute.moveLinesIfNeeded();
                    }

                    arFragment.onUpdate(frameTime);
                });


        // Set a touch listener on the Scene to listen for taps.
        /*arFragment.getArSceneView().getScene().setOnTouchListener(
                (HitTestResult hitTestResult, MotionEvent event) -> {
                    if (mActiveRoute != null) {
                        Toast.makeText(this, "boop", Toast.LENGTH_SHORT).show();
                        //mActiveRoute.moveLinesIfNeeded();
                    }

                    return false;
                });*/
    }


    /**
     * Place a new Route.
     * @param hit HitResult for the spot the user tapped.
     */
    private void placeNewRoute(HitResult hit) {
        Route newRoute = new Route(this, mScene, arFragment.getTransformationSystem(), mRenderableHelper);
        newRoute.addStartingpoint(hit);
        selectRoute(newRoute);
    }


    /**
     * Place a new clip to the active route.
     * @param hit HitResult for the spot the user tapped.
     */
    private void placeClipToActiveRoute(HitResult hit) {
        mActiveRoute.addClip(hit);
    }


    /**
     * Select a new active route.
     * @param route Selected route.
     */
    private void selectRoute(Route route) {
        mActiveRoute = route;
        editRouteMode = true;
    }


    /**
     * Clear route selection.
     */
    private void clearRouteSelection() {
        mActiveRoute = null;
        editRouteMode = false;
    }


    /**
     * TEST: change route color when the FAB button is clicked.
     * @param button FAB 1
     */
    // TODO automatic color change
    public void onClickColor(View button) {
        if (mActiveRoute != null) {
            mActiveRoute.changeRouteColor();
        }
    }


    /**
     * FAB button for something.
     * @param button FAB 2
     */
    public void onClickMove(View button) {
        // TODO do something else with this FAB
    }
}

