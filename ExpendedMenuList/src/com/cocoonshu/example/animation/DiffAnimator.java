package com.cocoonshu.example.animation;

/**
 * Differential implemented animator
 * @author Cocoonshu
 * @date 2016-06-16 19:36:23
 */
public class DiffAnimator extends Animator {

    private static final float FACTOR = 2E-1F;
    private static final float ERROR  = 1E-2F;
    
    private float mTargetProgress = 0f;
    
    public DiffAnimator() {
        setRange(0, -1);
    }
    
    @Override
    public boolean animate() {
        float   current      = getProgress();
        float   next         = current + (mTargetProgress - current) * FACTOR;
        boolean hasMoreFrame = false;
        
        if (current == next) {
            hasMoreFrame = false;
        } else {
            float delta = mTargetProgress - next;
            if (Math.abs(delta) < ERROR) {
                next = mTargetProgress;
                hasMoreFrame = false;
            } else {
                hasMoreFrame = true;
            }
        }
        
        setProgress(next);
        return hasMoreFrame;
    }

    @Override
    public void go(float progress) {
        float start = getStartProgress();
        float end   = getEndProgress();
        if (end >= start) {
            mTargetProgress = progress < start ? start : progress > end ? end : progress;
        } else {
            mTargetProgress = progress;
        }
    }
    
    @Override
    public void setRange(float start, float end) {
        super.setRange(start, end);
        mTargetProgress = start;
    }
}
