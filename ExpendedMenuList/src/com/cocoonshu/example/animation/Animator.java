package com.cocoonshu.example.animation;

/**
 * Base animator
 * @author Cocoonshu
 * @date 2016-06-16 19:34:17
 */
public abstract class Animator {

    private float mCurrentProgress = 0;
    private float mStartProgress   = 0;
    private float mEndProgress     = 0;
    
    public static Animator buildInterpolatorAnimator() {
        return new InterpolatorAnimator();
    }
    
    public static Animator buildDiffAnimator() {
        return new DiffAnimator();
    }
    
    protected final float getStartProgress() {
        return mStartProgress;
    }
    
    protected final float getEndProgress() {
        return mEndProgress;
    }
    
    public void setProgress(float progress) {
        mCurrentProgress = progress;
    }
    
    public final float getProgress() {
        return mCurrentProgress;
    }
    
    public void setRange(float start, float end) {
        mStartProgress = start;
        mEndProgress   = end;
    }
    
    public abstract void go(float progress);
    
    public abstract boolean animate();
}
