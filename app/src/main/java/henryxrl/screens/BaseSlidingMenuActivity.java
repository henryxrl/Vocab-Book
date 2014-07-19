package henryxrl.screens;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import henryxrl.slidingmenu.SlidingMenu;
import henryxrl.slidingmenu.app.SlidingFragmentActivity;

public class BaseSlidingMenuActivity extends SlidingFragmentActivity
{

    protected Fragment    mFrag;

    protected SlidingMenu slidingMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setBehindContentView(R.layout.right_frame);
        FragmentTransaction t = this.getSupportFragmentManager().beginTransaction();
        mFrag = new SlidingMenuFragment();
        t.replace(R.id.right_frame, mFrag);
        t.commit();

        slidingMenu = getSlidingMenu();
        slidingMenu.setMode(SlidingMenu.RIGHT);
        slidingMenu.setShadowWidth(getWindowManager().getDefaultDisplay().getWidth() / 40);
        slidingMenu.setShadowDrawable(R.drawable.right_shadow);
        slidingMenu.setSecondaryShadowDrawable(R.drawable.right_shadow);
        slidingMenu.setBehindOffset(getWindowManager().getDefaultDisplay().getWidth() / 7);
        slidingMenu.setFadeEnabled(true);
        slidingMenu.setFadeDegree(0.4f);
        slidingMenu.setBehindScrollScale(0);
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
    }

}
