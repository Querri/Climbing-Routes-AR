package ninja.siili.climbingroutes;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/** Stores information about the route. */
public class RouteInfo {

    private Context mContext;

    private String mName;
    private int mDiff;
    private int mDiffColor;
    private boolean mIsBoulder;
    private boolean mIsSport;
    private boolean mIsTrad;
    private int mStartHoldCount;
    private boolean mIsSitstart;
    private boolean mIsTopOut;
    private String mNotes;


    /**
     * Constructor for RouteInfo, sets default values.
     * @param context Context of the app
     */
    public RouteInfo(Context context) {
        mContext = context;
        mName = "";
        mDiff = 10;
        mIsBoulder = true;
        mIsSport = false;
        mIsTrad = false;
        mStartHoldCount = 1;
        mIsSitstart = false;
        mIsTopOut = false;
        mNotes = "";
        setDifficultyColor();
    }

    private String getName() {
        if (mName.equals("")) return "Nameless Route";
        else return mName;
    }



    /**
     * Set and get difficulty color based on difficulty.
     */
    private void setDifficultyColor() {
        if (mDiff < 9) {
            mDiffColor = mContext.getColor(R.color.green);
        } else if (mDiff < 18) {
            mDiffColor = mContext.getColor(R.color.yellow);
        } else if (mDiff < 27) {
            mDiffColor = mContext.getColor(R.color.orange);
        } else {
            mDiffColor = mContext.getColor(R.color.red);
        }
    }

    public int getDifficultyColor() {
        return mDiffColor;
    }


    /**
     * Get Route type as a String.
     * @return String value of Route type.
     */
    private String getTypeString() {
        if (mIsBoulder) {
            return mContext.getString(R.string.boulder);
        } else if (mIsSport) {
            return mContext.getString(R.string.sport);
        } else if (mIsTrad) {
            return mContext.getString(R.string.trad);
        } else return "";
    }


    /**
     * Check that values given to the route are fine.
     * @return true if all is fine
     */
    public boolean checkValuesAreAllRight() {
        if (!mIsBoulder && !mIsSport && !mIsTrad) {
            Toast.makeText(mContext, "Choose route type.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (mStartHoldCount > 2) {
            Toast.makeText(mContext, "Too many start holds.", Toast.LENGTH_SHORT).show();
        } else if (mName.equals("")) {
            mName = "Nameless Route";
        }
        return true;
    }


    /**
     * Get the number + letter combination that is the grade of the route (ex. 6b).
     * @return String of the grade
     */
    private String getDifficultyText() {
        int value = mDiff;
        String number;
        String letter = "";

        // TODO more grades
        if (value < 9) {
            number = "5";
        } else if (value < 18) {
            number = "6";
        } else if (value < 27) {
            number = "7";
        } else {
            number = "8";
        }

        switch(value) {
            case 0: case 9: case 18: case 27:
                letter = "A-";
                break;
            case 1: case 10: case 19: case 28:
                letter = "A ";
                break;
            case 2: case 11: case 20: case 29:
                letter = "A+";
                break;
            case 3: case 12: case 21: case 30:
                letter = "B-";
                break;
            case 4: case 13: case 22: case 31:
                letter = "B ";
                break;
            case 5: case 14: case 23: case 32:
                letter = "B+";
                break;
            case 6: case 15: case 24: case 33:
                letter = "C-";
                break;
            case 7: case 16: case 25: case 34:
                letter = "C ";
                break;
            case 8: case 17: case 26: case 35:
                letter = "C+";
                break;
        }

        /*
        A:  0 1 2    9 10 11   18 19 20   27 28 29
        B:  3 4 5   12 13 14   21 22 23   30 31 32
        C:  6 7 8   15 16 17   24 25 26   33 34 35

        +:  2 5 8   11 14 17   20 13 16   29 32 35
         :  1 4 7   10 13 16   19 22 25   28 31 34
        -:  0 3 6    9 12 15   18 21 24   27 30 33
         */

        return number + letter;
    }


    /**
     * Update all values in RouteInfo.
     * @param infoView baabaa
     * @return True if succeeded.
     */
    public boolean updateAll(View infoView) {
        ArrayList<View> views = findAllViews(infoView);

        if (views != null && views.size() == 8) {
            mName = ((TextView) views.get(0)).getText().toString();
            mDiff = ((SeekBar) views.get(1)).getProgress();
            mIsBoulder = ((RadioButton) views.get(2)).isChecked();
            mIsSport = ((RadioButton) views.get(3)).isChecked();
            mIsTrad = ((RadioButton) views.get(4)).isChecked();
            mIsSitstart = ((CheckBox) views.get(5)).isChecked();
            mIsTopOut = ((CheckBox) views.get(6)).isChecked();
            mStartHoldCount = 1;
            mNotes = ((TextView) views.get(7)).getText().toString();

            setDifficultyColor();
            return true;
        }
        Toast.makeText(mContext, "Failed to update RouteInfo.", Toast.LENGTH_SHORT).show();
        return false;
    }


    /**
     * Update the info view with RouteInfo's values.
     * @param infoView the infoview.
     */
    public void updateInfoView(View infoView) {
        ArrayList<View> views = findAllViews(infoView);
        // TODO colored textview and listener for difficulty

        if (views != null && views.size() == 8) {
            ((TextView) views.get(0)).setText(mName);
            ((SeekBar) views.get(1)).setProgress(mDiff);
            ((RadioButton) views.get(2)).setChecked(mIsBoulder);
            ((RadioButton) views.get(3)).setChecked(mIsSport);
            ((RadioButton) views.get(4)).setChecked(mIsTrad);
            ((CheckBox) views.get(5)).setChecked(mIsSitstart);
            ((CheckBox) views.get(6)).setChecked(mIsTopOut);
            ((TextView) views.get(7)).setText(mNotes);
        }
        Toast.makeText(mContext, "Failed to update info view.", Toast.LENGTH_SHORT).show();
    }


    /**
     * Update the info card with RouteInfo's values.
     * @param infoCardView the info card.
     */
    public void updateInfoCardView(View infoCardView) {
        if (infoCardView != null) {
            TextView nameTV = infoCardView.findViewById(R.id.name);
            TextView diffTV = infoCardView.findViewById(R.id.diff_number);
            TextView typeTV = infoCardView.findViewById(R.id.type);
            TextView sitstartIC = infoCardView.findViewById(R.id.sitstart);
            TextView holdcountIC = infoCardView.findViewById(R.id.start_hold_count);
            TextView topoutIC = infoCardView.findViewById(R.id.topout);
            TextView notesTV = infoCardView.findViewById(R.id.notes);

            if (nameTV == null || diffTV == null || typeTV == null || sitstartIC == null
                    || holdcountIC == null || topoutIC == null || notesTV == null)  {
                Toast.makeText(mContext, "Null field in info card.", Toast.LENGTH_SHORT).show();
                return;
            }

            nameTV.setText(getName());
            diffTV.setText(getDifficultyText());
            diffTV.setTextColor(mDiffColor);
            typeTV.setText(getTypeString());

            if (mIsSitstart) sitstartIC.setVisibility(View.VISIBLE);
            else sitstartIC.setVisibility(View.INVISIBLE);

            if (mStartHoldCount == 2) holdcountIC.setVisibility(View.VISIBLE);
            if (mStartHoldCount == 1) holdcountIC.setVisibility(View.VISIBLE);
            else holdcountIC.setVisibility(View.INVISIBLE);

            if (mIsTopOut) topoutIC.setVisibility(View.VISIBLE);
            else topoutIC.setVisibility(View.INVISIBLE);

            notesTV.setText(mNotes);
        } else {
            Toast.makeText(mContext, "Null info card.", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Find all Views from the info view.
     * @param infoView the info view.
     * @return ListArray of the views.
     */
    private ArrayList<View> findAllViews(View infoView) {
        ArrayList<View> views = new ArrayList<>();
        
        views.add(infoView.findViewById(R.id.name));
        views.add(infoView.findViewById(R.id.diff_seekbar));
        views.add(infoView.findViewById(R.id.boulder));
        views.add(infoView.findViewById(R.id.sport));
        views.add(infoView.findViewById(R.id.trad));
        views.add(infoView.findViewById(R.id.sitstart));
        views.add(infoView.findViewById(R.id.topout));
        views.add(infoView.findViewById(R.id.notes));

        for (View view : views) {
            if (view == null) {
                return null;
            }
        }
        return views;
    }
}
