package ninja.siili.climbingroutes;

import android.content.Context;
import android.widget.Toast;

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
        mStartHoldCount = 0;
        mIsSitstart = false;
        mIsTopOut = false;
        mNotes = "";
        setDifficultyColor();
    }


    /**
     * Set and get Route name.
     * @param newName String for new name.
     */
    public void setName(String newName) { mName = newName; }
    public String getName() { return mName; }


    /**
     * Set and get Route difficulty.
     * @param newDiff Integer for new difficulty.
     */
    public void setDifficulty(int newDiff) {
        mDiff = newDiff;
        setDifficultyColor();
    }

    public int getDifficulty() { return mDiff; }


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

    public int getDifficultyColor() { return mDiffColor; }


    /**
     * Set and get route types.
     * @param value Boolean value.
     */
    public void setIsBoulder(boolean value) { mIsBoulder = value; }
    public boolean getIsBoulder() { return mIsBoulder; }

    public void setIsSport(boolean value) { mIsSport = value; }
    public boolean getIsSport() { return mIsSport; }

    public void setIsTrad(boolean value) { mIsTrad = value; }
    public boolean getIsTrad() { return mIsTrad; }


    /**
     * Get Route type as a String.
     * @return String value of Route type.
     */
    public String getTypeString() {
        if (mIsBoulder) {
            return mContext.getString(R.string.route_type_boulder);
        } else if (mIsSport) {
            return mContext.getString(R.string.route_type_sport);
        } else if (mIsTrad) {
            return mContext.getString(R.string.route_type_trad);
        } else return "";
    }


    /**
     * Get and set sitstart, one/two hold start and topout.
     * @param value Boolean value.
     */
    public void setIsSitstart(boolean value) { mIsSitstart = value; }
    public boolean getIsSitstart() { return mIsSitstart; }

    public void setStartHoldCount(int value) { mStartHoldCount = value; }
    public int getStartHoldCount() { return mStartHoldCount; }

    public void setIsTopout(boolean value) { mIsTopOut = value; }
    public boolean getIsTopout() { return mIsTopOut; }


    /**
     * Set and get notes.
     * @param value String value of new notes.
     */
    public void setNotes(String value) { mNotes = value; }
    public String getNotes() { return mNotes; }


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
    public String getDifficultyText() {
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
}
