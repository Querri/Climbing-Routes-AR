package ninja.siili.climbingroutes;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Point;
import com.google.ar.core.Trackable;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ArActivity extends AppCompatActivity {

    private static final String TAG = ArActivity.class.getSimpleName();

    private ArFragment arFragment;
    private Scene mScene;

    private RenderableHelper mRenderableHelper;
    private GestureDetector gestureDetector;

    private Route mActiveRoute;
    private ArrayList<Route> mRoutes;
    private boolean editMode = false;
    private TextView modeTextView;


    private View mInfoView;
    private View mInfoViewFAB;

    private boolean hasFinishedLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);
        mInfoView = findViewById(R.id.include);
        mInfoView.setVisibility(View.GONE);
        mInfoViewFAB = findViewById(R.id.fab_info_view);
        mInfoViewFAB.setVisibility(View.INVISIBLE);
        modeTextView = findViewById(R.id.tv_mode);

        mRoutes = new ArrayList<>();

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
                        mRenderableHelper = new RenderableHelper(this, mScene,
                                clipStageGreen.get(), clipStageYellow.get(), clipStageOrange.get(), clipStageRed.get(),
                                lineStageGreen.get(), lineStageYellow.get(), lineStageOrange.get(), lineStageRed.get());
                        hasFinishedLoading = true;

                    } catch (InterruptedException | ExecutionException ex) {
                        Toast.makeText(this, "Failed to load renderables", Toast.LENGTH_LONG).show();
                    }

                    return null;
                });
        

        // Update listener for moving lines.
        arFragment.getArSceneView().getScene().setOnUpdateListener(
                frameTime -> {

                    Frame frame = arFragment.getArSceneView().getArFrame();
                    if (frame == null) {
                        return;
                    }

                    if (frame.getCamera().getTrackingState() != TrackingState.TRACKING) {
                        return;
                    }

                    if (mActiveRoute != null && editMode) {
                        // TODO do this only when there is touch event?
                        mActiveRoute.moveLinesIfNeeded();
                    }

                    arFragment.onUpdate(frameTime);
                });


        // Gesture detector.
        gestureDetector = new GestureDetector
                (this, new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onSingleTapUp(MotionEvent e) {
                        onSingleTap(e);
                        return true;
                    }

                    @Override
                    public boolean onDown(MotionEvent e) {
                        return true;
                    }
                });

        
        // Touch listener
        arFragment.getArSceneView().getScene().setOnTouchListener(
                (HitTestResult hitTestResult, MotionEvent event) -> {
                    return gestureDetector.onTouchEvent(event);
                }
        );
    }


    /**
     * User has tapped the screen.
     * @param tap MotionEvent for the tap.
     */
    private void onSingleTap(MotionEvent tap) {
        Frame frame = arFragment.getArSceneView().getArFrame();
        if (frame != null) {
            if (mActiveRoute == null && editMode) {
                if (tryPlaceNewRoute(tap, frame)) {
                    selectRoute(mRoutes.get(mRoutes.size() - 1));
                    editingRoute(editMode);
                } else {
                    Toast.makeText(this, "nope", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (editMode) {
                    if (!tryPlaceClip(tap, frame)) {
                        Toast.makeText(this, "nope", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    selectRoute(null);
                }
            }
        }
    }


    /**
     * Try to place a new Route.
     * @param tap MotionEvent fot the tap.
     * @param frame Current frame.
     */
    private boolean tryPlaceNewRoute(MotionEvent tap, Frame frame) {
        if (tap != null && frame.getCamera().getTrackingState() == TrackingState.TRACKING) {
            for (HitResult hit : frame.hitTest(tap)) {
                Trackable trackable = hit.getTrackable();
                if (trackable instanceof Point) {
                    Route newRoute = new Route(this, arFragment.getTransformationSystem(), mRenderableHelper);
                    newRoute.addClip(hit);
                    mRoutes.add(newRoute);
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * Try to place a new clip to active Route.
     * @param tap MotionEvent fot the tap.
     * @param frame Current frame.
     */
    private boolean tryPlaceClip(MotionEvent tap, Frame frame) {
        if (tap != null && frame.getCamera().getTrackingState() == TrackingState.TRACKING) {
            for (HitResult hit : frame.hitTest(tap)) {
                Trackable trackable = hit.getTrackable();
                if (trackable instanceof Point) {
                    mActiveRoute.addClip(hit);
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * Select a new active route.
     * @param route Selected route.
     */
    private void selectRoute(Route route) {
        mActiveRoute = route;
        updateModeText();
    }


    /**
     * Switch mode to adding route or exit it.
     * @param startAdd True if starting adding, false if calcelling or done.
     */
    private void addingRoute(boolean startAdd) {
        editMode = startAdd;
        updateModeText();
    }


    /**
     * Switch mode to editing route or exit it.
     * @param startEdit True if starting edit, false if cancelling or done.
     */
    private void editingRoute(boolean startEdit) {
        mActiveRoute.enableClipTransforming(startEdit);
        editMode = startEdit;
        updateModeText();

        // Show FAB for info view only when a route is in editing mode.
        if (startEdit) {
            mInfoViewFAB.setVisibility(View.VISIBLE);
        } else {
            mInfoViewFAB.setVisibility(View.INVISIBLE);
        }
    }


    /**
     * Update the text informing what mode is on.
     */
    private void updateModeText() {
        if (mActiveRoute == null) {
            if (editMode) {
                modeTextView.setText(this.getString(R.string.add_route));
                // TODO change icon to cancel
            } else {
                modeTextView.setText("");
                // TODO change icon to add
            }
        } else {
            if (editMode) {
                modeTextView.setText(this.getString(R.string.edit_route));
                // TODO change icon to save
            } else {
                modeTextView.setText(this.getString(R.string.route_active));
                // TODO change icon to edit
            }
        }
    }


    /**
     * FAB button for changing mode.
     * @param button 2. FAB button
     */
    public void onClickChangeMode(View button) {
        if (mActiveRoute == null) {
            addingRoute(!editMode);
        } else {
            editingRoute(!editMode);
        }
    }


    /**
     * FAB button for toggling Info View's visibility.
     * @param button 1. FAB button
     */
    public void onClickToggleInfoView(View button) {
        if (mActiveRoute != null) {
            if (mInfoView.getVisibility() == View.GONE) {
                mActiveRoute.updateInfoView(mInfoView);
                mInfoView.setVisibility(View.VISIBLE);
            } else {
                mInfoView.setVisibility(View.GONE);
                mActiveRoute.updateRouteInfo(mInfoView);
            }
        }
    }
}

