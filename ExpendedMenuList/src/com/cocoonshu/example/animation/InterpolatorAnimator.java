package com.cocoonshu.example.animation;

import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

/**
 * Interpolator implemented animator
 * @author Cocoonshu
 * @date 2016-06-16 19:38:17
 */
public class InterpolatorAnimator extends Animator {

    private static final float StartPrecent  = 0f;
    private static final float EndPrecent    = 100f;
    private static final float StepPrecent   = 5f;
    
    private Interpolator mInterpolator   = null;
    private float        mTargetPrecent  = 0f;
    private float        mCurrentPrecent = 0f;
    
    public InterpolatorAnimator() {
        mInterpolator = new DecelerateInterpolator(2.0f);
    }

    @Override
    public boolean animate() {
        float   start          = getStartProgress();
        float   end            = getEndProgress();
        float   currentPrecent = mCurrentPrecent;
        float   stepPrecent    = Math.signum(mTargetPrecent - currentPrecent) * StepPrecent;
        float   nextPrecent    = currentPrecent + stepPrecent;
        float   next           = mInterpolator.getInterpolation(nextPrecent * 0.01f) * (end - start) + start;
        boolean hasMoreFrame   = false;
        
        if (nextPrecent < StartPrecent) {
            next            = start;
            mCurrentPrecent = StartPrecent;
            hasMoreFrame    = false;
        } else if (nextPrecent > EndPrecent) {
            next            = end;
            mCurrentPrecent = EndPrecent;
            hasMoreFrame    = false;
        } else {
            mCurrentPrecent = nextPrecent;
            hasMoreFrame    = true;
        }

        setProgress(next);
        return hasMoreFrame;
    }

    @Override
    public void go(float progress) {
        float start    = getStartProgress();
        float end      = getEndProgress();
        float target   = progress < start ? start : progress > end ? end : progress;
        float precent  = (target - start) / (end - start);
        mTargetPrecent = precent * 100;
    }
}
