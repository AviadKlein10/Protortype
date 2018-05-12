package aviad.ikea;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Property;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;

import java.util.ArrayList;

/**
 * Created by moshe on 12/02/2018.
 */

public class AnimUtility {


    private final String ALPHA_PROPERTY = "alpha";
    private final String TRANSLATION_X_PROPERTY = "x";
    private ArrayList<AnimatorSet> animatorSets;

    public AnimUtility(AnimUtilsBuilder animUtillsBuilder) {
        animatorSets = animUtillsBuilder.animatorSets;
    }


    /**
     * calculates a particular percent by the screen height.
     *
     * @param percent flout of the percent that need to be calculate (example: 0.01f for 1% )
     * @return the right value of the percent multiply by the screen height.
     */
    public static int calculateScreenHeightByPercent(float percent, Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenHeight = displayMetrics.heightPixels;
        return (int) (screenHeight * percent);
    }

    /**
     * calculates a particular percent by the screen width.
     *
     * @param percent flout of the percent that need to be calculate (example: 0.01f for 1% )
     * @return the right value of the percent multiply by the screen width.
     */
    public static int calculateScreenWidthByPercent(float percent, Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        return (int) (screenWidth * percent);
    }


    public enum Side {
        LEFT(1), RIGHT(2), TOP(3), BOTTOM(4);

        public int type;

        Side(int type) {
            this.type = type;
        }

    }


    public static int convertDpToPx(Context context, float dp) {
        return Math.round(dp * (context.getResources().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }


    public static class AnimUtilsBuilder {
        private ArrayList<AnimatorSet> animatorSets = new ArrayList<>();
        private final String TRANSLATION_X_PROPERTY = "x";
        private final String TRANSLATION_Y_PROPERTY = "y";
        private final String ALPHA_PROPERTY = "alpha";
        private final String BACKGROUND_COLOR_PROPERTY = "backgroundColor";

        public AnimUtilsBuilder() {

        }

        /**
         * Create an AnimatorSet that will hold 2 ObjectAnimator:
         * 1) Moves the view out of the screen in 0 ms.
         * 2) Moves the view to the last position it was draw on before the last anim toke action.
         *
         * @param view     the specific view that will be animated.
         * @param duration the total duration of the anim.
         * @param fromSide the view will appear from the right side of the screen or left (by Side enum).
         * @param context  context.
         * @return AnimatorSet that can be launch by calling start() method.
         */

        public AnimUtilsBuilder transitionFromOutOfScreen(View view, int delay, int duration, Side fromSide, Context context) {
            AnimatorSet animatorSet;
            int fromPosition;
            float toPosition;
            ObjectAnimator objectAnimatorFrom;
            ObjectAnimator objectAnimatorTo;
            if (fromSide == Side.RIGHT) {
                toPosition = view.getX();
                DisplayMetrics displayMetrics = new DisplayMetrics();
                ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                fromPosition = displayMetrics.widthPixels;
                objectAnimatorFrom = ObjectAnimator.ofFloat(view, TRANSLATION_X_PROPERTY, fromPosition);
                objectAnimatorFrom.setDuration(0);

                objectAnimatorTo = ObjectAnimator.ofFloat(view, TRANSLATION_X_PROPERTY, toPosition);
                objectAnimatorTo.setDuration(duration);
                objectAnimatorTo.setStartDelay(delay);
            } else if (fromSide == Side.BOTTOM) {
                toPosition = view.getY();

                DisplayMetrics displayMetrics = new DisplayMetrics();
                ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                fromPosition = displayMetrics.heightPixels;
                objectAnimatorFrom = ObjectAnimator.ofFloat(view, TRANSLATION_Y_PROPERTY, fromPosition);
                objectAnimatorFrom.setDuration(0);

                objectAnimatorTo = ObjectAnimator.ofFloat(view, TRANSLATION_Y_PROPERTY, toPosition);
                objectAnimatorTo.setDuration(duration);
                objectAnimatorTo.setStartDelay(delay);

            } else {
                int viewsWidth = view.getWidth();
                toPosition = view.getX();
                fromPosition = (-viewsWidth);
                objectAnimatorFrom = ObjectAnimator.ofFloat(view, TRANSLATION_X_PROPERTY, fromPosition);
                objectAnimatorFrom.setDuration(0);

                objectAnimatorTo = ObjectAnimator.ofFloat(view, TRANSLATION_X_PROPERTY, toPosition);
                objectAnimatorTo.setDuration(duration);
                objectAnimatorTo.setStartDelay(delay);

            }
            objectAnimatorTo.setInterpolator(new DecelerateInterpolator());
            objectAnimatorTo.setInterpolator(new DecelerateInterpolator());


            animatorSet = new AnimatorSet();
            animatorSet.play(objectAnimatorFrom).before(objectAnimatorTo);
            animatorSets.add(animatorSet);
            return this;
        }

        /**
         * Create an AnimatorSet going over all the views and hide from the screen:
         * 1) Moves the view out of the screen in 0 ms.
         *
         * @param viewList List of items that will be hidden.
         * @param context  context.
         * @return AnimatorSet that can be launch by calling start() method.
         */
        public AnimUtilsBuilder hideViewOutOfScreen(Context context, View... viewList) {
            AnimatorSet animatorSet = new AnimatorSet();
            int fromPositionX;

            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            fromPositionX = displayMetrics.widthPixels;

            for (View view : viewList) {
                ObjectAnimator objectAnimatorFrom = ObjectAnimator.ofFloat(view, TRANSLATION_X_PROPERTY, fromPositionX);
                objectAnimatorFrom.setDuration(0);
                animatorSet.playTogether(objectAnimatorFrom);
            }

            animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
            animatorSets.add(animatorSet);
            return this;
        }


        /**
         * Create an AnimatorSet going over all the views and hide from the screen:
         * 1) Moves the view out of the screen in 0 ms.
         *
         * @param viewList      List of items that will be hidden.
         * @param context       context.
         * @param side          the side that the view should move to.
         * @param scaleViewSize increase/decrease the view transition by float factor.
         * @return AnimatorSet that can be launch by calling start() method.
         */
        public AnimUtilsBuilder hideViewOutOfScreen(Side side, int duration, Context context, float scaleViewSize, View... viewList) {
            AnimatorSet animatorSet = new AnimatorSet();
            int fromPositionX = 0;
            if (side == Side.RIGHT) {
                DisplayMetrics displayMetrics = new DisplayMetrics();
                ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                fromPositionX = displayMetrics.widthPixels;
            }

            for (View view : viewList) {
                if (side == Side.LEFT) {
                    fromPositionX = (-view.getWidth());

                    if (scaleViewSize != 1) {
                        fromPositionX = (int) (fromPositionX - (view.getWidth() * scaleViewSize));
                    }
                } else {
                    if (scaleViewSize != 1) {
                        fromPositionX = (int) (fromPositionX + (view.getWidth() * scaleViewSize));
                    }
                }


                ObjectAnimator objectAnimatorFrom = ObjectAnimator.ofFloat(view, TRANSLATION_X_PROPERTY, fromPositionX);
                objectAnimatorFrom.setDuration(duration);
                objectAnimatorFrom.setInterpolator(new AccelerateInterpolator());
                animatorSet.playTogether(objectAnimatorFrom);
            }

            animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
            animatorSets.add(animatorSet);
            return this;
        }



       /* *//**
         * <<<<<<< HEAD
         * Create an AnimatorSet going over all the views and hide from the screen:
         * 1) Moves the view out of the screen in 0 ms.
         *
         * @param viewList      List of items that will be hidden.
         * @param context       context.
         * @param side          the side that the view should move to.
         * @param scaleViewSize increase/decrease the view transition by float factor.
         * @return AnimatorSet that can be launch by calling start() method.
         *//*
        public AnimUtilsBuilder hideViewOutOfScreen(Side side, int duration, Context context, float scaleViewSize, View... viewList) {
            AnimatorSet animatorSet = new AnimatorSet();
            int fromPositionX = 0;
            if (side == Side.RIGHT) {
                DisplayMetrics displayMetrics = new DisplayMetrics();
                ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                fromPositionX = displayMetrics.widthPixels;
            }

            for (View view : viewList) {
                if (side == Side.LEFT) {
                    fromPositionX = (-view.getWidth());

                    if (scaleViewSize != 1) {
                        fromPositionX = (int) (fromPositionX - (view.getWidth() * scaleViewSize));
                    }
                } else {
                    if (scaleViewSize != 1) {
                        fromPositionX = (int) (fromPositionX + (view.getWidth() * scaleViewSize));
                    }
                }


                ObjectAnimator objectAnimatorFrom = ObjectAnimator.ofFloat(view, TRANSLATION_X_PROPERTY, fromPositionX);
                objectAnimatorFrom.setDuration(duration);
                objectAnimatorFrom.setInterpolator(new AccelerateInterpolator());
                animatorSet.playTogether(objectAnimatorFrom);
            }
            return this;
        }*/

        /**
         * Create an AnimatorSet for all views that was provides and moving them slight to the side sent:
         * Recommended to use with showViewItem method to revile the view back to the position
         * 1) Moves the view out of the screen in 0 ms.
         * 2) Moves the view to the new position by 30dp.
         *
         * @param views    list of views that will be animated.
         * @param duration the total duration of the anim.
         * @param fromSide the view will appear from the side received (by Side enum).
         * @return AnimatorSet that can be launch by calling start() method.
         */
        public AnimUtilsBuilder hideViewItem(Side fromSide, float scalePercentage, int duration, View... views) {
            AnimatorSet animatorSet = new AnimatorSet();
            float fromPosition;
            for (View view : views) {
                ObjectAnimator objectAnimator;

                if (fromSide == Side.RIGHT) {
                    fromPosition = view.getX() + (view.getWidth() * scalePercentage);
                    objectAnimator = ObjectAnimator.ofFloat(view, TRANSLATION_X_PROPERTY, fromPosition);
                } else if (fromSide == Side.LEFT) {
                    fromPosition = view.getX() - (view.getWidth() * scalePercentage);
                    objectAnimator = ObjectAnimator.ofFloat(view, TRANSLATION_X_PROPERTY, fromPosition);
                } else if (fromSide == Side.TOP) {
                    fromPosition = view.getY() - (view.getHeight() * scalePercentage);
                    objectAnimator = ObjectAnimator.ofFloat(view, TRANSLATION_Y_PROPERTY, fromPosition);
                } else {
                    fromPosition = view.getY() + (view.getHeight() * scalePercentage);
                    objectAnimator = ObjectAnimator.ofFloat(view, TRANSLATION_Y_PROPERTY, fromPosition);
                }
                objectAnimator.setDuration(duration);
                animatorSet.playTogether(objectAnimator);
            }

            animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
            animatorSets.add(animatorSet);
            return this;
        }


        /**
         * Create an AnimatorSet going over all the views and hide from the screen:
         * 1) Moves the view out of the screen in 0 ms.
         *
         * @param viewList List of items that will be hidden.
         * @param context  context.
         * @param side     the side that the view should move to.
         * @return AnimatorSet that can be launch by calling start() method.
         */
        public AnimUtilsBuilder hideViewOutOfScreen(Side side, int duration, Context context, int delay, View... viewList) {
            AnimatorSet animatorSet = new AnimatorSet();
            int fromPositionX = 0;
            if (side == Side.RIGHT) {
                DisplayMetrics displayMetrics = new DisplayMetrics();
                ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                fromPositionX = displayMetrics.widthPixels + 100;
            }

            for (View view : viewList) {
                if (side == Side.LEFT) {
                    fromPositionX = (-view.getWidth());
                }
                ObjectAnimator objectAnimatorFrom = ObjectAnimator.ofFloat(view, TRANSLATION_X_PROPERTY, fromPositionX);
                objectAnimatorFrom.setDuration(duration);
                objectAnimatorFrom.setInterpolator(new AccelerateInterpolator());
                animatorSet.playTogether(objectAnimatorFrom);
            }
            animatorSet.setStartDelay(delay);
            animatorSets.add(animatorSet);
            return this;
        }

        /**
         * Create an AnimatorSet going over all the views and hide from the screen:
         * 1) Moves the view out of the screen in 0 ms.
         *
         * @param viewList List of items that will be hidden.
         * @param context  context.
         * @param side     the side that the view should move to.
         * @return AnimatorSet that can be launch by calling start() method.
         */
        public AnimUtilsBuilder hideViewOutOfScreen(Side side, int duration, Context context, View... viewList) {
            AnimatorSet animatorSet = new AnimatorSet();
            int fromPositionX = 0;
            if (side == Side.RIGHT) {
                DisplayMetrics displayMetrics = new DisplayMetrics();
                ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                fromPositionX = displayMetrics.widthPixels + 100;
            }

            for (View view : viewList) {
                if (side == Side.LEFT) {
                    fromPositionX = (-view.getWidth());
                }
                ObjectAnimator objectAnimatorFrom = ObjectAnimator.ofFloat(view, TRANSLATION_X_PROPERTY, fromPositionX);
                objectAnimatorFrom.setDuration(duration);
                objectAnimatorFrom.setInterpolator(new AccelerateInterpolator());
                animatorSet.playTogether(objectAnimatorFrom);
            }
            animatorSets.add(animatorSet);
            return this;
        }

        /**
         * Create an AnimatorSet that will hold 2 ObjectAnimator:
         * 1) Moves the view to 0 position (start position)
         *
         * @param view             the specific view that will be animated.
         * @param duration         the total duration of the anim.
         * @param side             the view will appear from the side.
         * @param originalPosition the original position of the view.
         * @return AnimatorSet that can be launch by calling start() method.
         */
        public AnimUtilsBuilder translateToExactPosition(View view, float originalPosition, int duration, Side side, int delay) {

            AnimatorSet animatorSet;

            ObjectAnimator objectAnimatorFrom;
            if (side == Side.RIGHT || side == Side.LEFT) {
                objectAnimatorFrom = ObjectAnimator.ofFloat(view, TRANSLATION_X_PROPERTY, originalPosition);
            } else {
                objectAnimatorFrom = ObjectAnimator.ofFloat(view, TRANSLATION_Y_PROPERTY, originalPosition);
            }
            objectAnimatorFrom.setDuration(duration);
            animatorSet = new AnimatorSet();
            animatorSet.play(objectAnimatorFrom);
            animatorSet.setStartDelay(delay);

            animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
            animatorSets.add(animatorSet);
            return this;
        }


        public AnimUtilsBuilder rotateViewAnimation(View view, int duration, int angle, int delay, int repeatCount) {
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "rotation", angle);
            objectAnimator.setDuration(duration);
            objectAnimator.setStartDelay(delay);
            objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            objectAnimator.setRepeatCount(repeatCount);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.play(objectAnimator);
            animatorSets.add(animatorSet);
            return this;
        }

        /**
         * Create an AnimatorSet that will hold 2 ObjectAnimator:
         * 1) Moves the view to 0 position (start position)
         *
         * @param view       the specific view that will be animated.
         * @param duration   the total duration of the anim.
         * @param fromSide   the view will appear from the right side of the screen or left (by Side enum).
         * @param startDelay the animation will start after the set time).
         * @param context    context.
         * @return AnimatorSet that can be launch by calling start() method.
         */

        public AnimUtilsBuilder showViewOnScreen(View view, int duration, Side fromSide, Context context, long startDelay) {
            AnimatorSet animatorSet;

            if (fromSide == Side.RIGHT) {
                DisplayMetrics displayMetrics = new DisplayMetrics();
                ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            } else {

            }

            ObjectAnimator objectAnimatorTo = ObjectAnimator.ofFloat(view, TRANSLATION_X_PROPERTY, 0);
            objectAnimatorTo.setDuration(duration);
            objectAnimatorTo.setInterpolator(new AccelerateInterpolator());
            animatorSet = new AnimatorSet();
            animatorSet.play(objectAnimatorTo);
            animatorSet.setStartDelay(startDelay);
            animatorSets.add(animatorSet);
            return this;
        }

        /**
         * Create an AnimatorSet that will hold 2 ObjectAnimator:
         * 1) Moves the view to 0 position (start position)
         *
         * @param view       the specific view that will be animated.
         * @param duration   the total duration of the anim.
         * @param startDelay the animation will start after the set time).
         * @param context    context.
         * @return AnimatorSet that can be launch by calling start() method.
         */

        public AnimUtilsBuilder moveViewToScreensCenter(View view, int duration, Context context, long startDelay) {
            AnimatorSet animatorSet;

            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            float centerOfScreenX = (displayMetrics.widthPixels / 2) - (view.getWidth() / 2);
            float centerOfScreenY = (displayMetrics.heightPixels / 2) - (view.getHeight() / 2);



            ObjectAnimator objectAnimatorToX = ObjectAnimator.ofFloat(view, TRANSLATION_X_PROPERTY, centerOfScreenX);
            objectAnimatorToX.setDuration(duration);
            objectAnimatorToX.setInterpolator(new AccelerateInterpolator());

            ObjectAnimator objectAnimatorToY = ObjectAnimator.ofFloat(view, TRANSLATION_Y_PROPERTY, centerOfScreenY);
            objectAnimatorToY.setDuration(duration);
            objectAnimatorToY.setInterpolator(new AccelerateInterpolator());

            animatorSet = new AnimatorSet();
            animatorSet.playTogether(objectAnimatorToX,objectAnimatorToY);
            animatorSet.setStartDelay(startDelay);
            animatorSets.add(animatorSet);
            return this;
        }


        public AnimUtilsBuilder animateViewToFadeOut(View view, int duration) {
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, ALPHA_PROPERTY, 0f);
            objectAnimator.setDuration(duration);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.play(objectAnimator);
            animatorSets.add(animatorSet);
            return this;

        }

        public AnimUtilsBuilder animateViewGoneFromScreen(final View view) {

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    view.setVisibility(View.GONE);
                }
            });
            animatorSets.add(animatorSet);
            return this;

        }

        /**
         * creates an AnimatorSet thats hold an FadeIn animation.
         *
         * @param view     the specific view that will be animated.
         * @param duration the total duration of the anim.
         * @return AnimatorSet that can be launch by calling start() method.
         */

        public AnimUtilsBuilder animateViewToFadeIn(View view, int duration) {
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, ALPHA_PROPERTY, 0f, 1f);
            objectAnimator.setDuration(duration);
            objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.play(objectAnimator);
            animatorSets.add(animatorSet);
            return this;
        }

        public AnimUtilsBuilder animateViewToFadeIn(View view, int duration, int delay) {
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, ALPHA_PROPERTY, 0f, 1f);
            objectAnimator.setDuration(duration);
            objectAnimator.setStartDelay(delay);
            objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.play(objectAnimator);
            animatorSets.add(animatorSet);
            return this;
        }

        public AnimUtilsBuilder rotateViewAnimation(View view, int duration, int angle, int delay) {
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "rotation", angle);
            objectAnimator.setDuration(duration);
            objectAnimator.setStartDelay(delay);
            objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.play(objectAnimator);
            animatorSets.add(animatorSet);
            return this;
        }

        public AnimUtilsBuilder scaleViewAnimation(View view, int duration, float xScale, float yScale) {
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", xScale);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", yScale);
            scaleX.setInterpolator(new AccelerateDecelerateInterpolator());
            scaleY.setInterpolator(new AccelerateDecelerateInterpolator());

            scaleX.setDuration(duration);
            scaleY.setDuration(duration);

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.play(scaleX).with(scaleY);
            animatorSets.add(animatorSet);
            return this;
        }

        public AnimUtilsBuilder scaleExactViewAnimation(int duration, int fromColor, int toColor, View onView, View toView) {
            ObjectAnimator objectAnimatorWidth = ObjectAnimator.ofInt(onView, new ScaleWidthProperty(), onView.getWidth(), toView.getWidth());
            objectAnimatorWidth.setDuration(duration);
            objectAnimatorWidth.setInterpolator(new AccelerateDecelerateInterpolator());

            ObjectAnimator objectAnimatorHeight = ObjectAnimator.ofInt(onView, new ScaleHeightProperty(), onView.getHeight(), toView.getHeight());
            objectAnimatorHeight.setDuration(duration);
            objectAnimatorHeight.setInterpolator(new AccelerateDecelerateInterpolator());

            ObjectAnimator objectAnimatorColor = ObjectAnimator.ofInt(onView, BACKGROUND_COLOR_PROPERTY, fromColor, toColor);
            objectAnimatorColor.setDuration(duration);
            objectAnimatorColor.setEvaluator(new ArgbEvaluator());
            objectAnimatorColor.setInterpolator(new AccelerateDecelerateInterpolator());

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.play(objectAnimatorWidth).with(objectAnimatorHeight).with(objectAnimatorColor);
            animatorSets.add(animatorSet);
            return this;
        }

        public AnimUtilsBuilder scaleExactViewAnimation(int duration, View onView, View toView) {
            ObjectAnimator objectAnimatorWidth = ObjectAnimator.ofInt(onView, new ScaleWidthProperty(), onView.getWidth(), toView.getWidth());
            objectAnimatorWidth.setDuration(duration);
            objectAnimatorWidth.setInterpolator(new AccelerateDecelerateInterpolator());

            ObjectAnimator objectAnimatorHeight = ObjectAnimator.ofInt(onView, new ScaleHeightProperty(), onView.getHeight(), toView.getHeight());
            objectAnimatorHeight.setDuration(duration);
            objectAnimatorHeight.setInterpolator(new AccelerateDecelerateInterpolator());


            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.play(objectAnimatorWidth).with(objectAnimatorHeight);
            animatorSets.add(animatorSet);
            return this;
        }


        public DisplayMetrics getScreenDisplayMetrics(Context context) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            return displayMetrics;
        }


        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        public AnimUtilsBuilder revealViewFromRightShowEnd(final View view, int duration, int startDelay) {
            int viewWidth = view.getWidth();
            int viewHeight = view.getHeight() / 2;
            float finalRadius = (float) Math.hypot(viewWidth, viewHeight);
            Animator animator = ViewAnimationUtils.createCircularReveal(view, viewWidth, viewHeight, 0, finalRadius);
            animator.setDuration(duration);
            animator.setStartDelay(startDelay);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationEnd(animation);
                    view.setVisibility(View.VISIBLE);

                }
            });
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.play(animator);
            animatorSets.add(animatorSet);
            return this;
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        public void revealViewFromRightShow(final View view, int duration, int startDelay) {
            int viewWidth = view.getWidth();
            int viewHeight = view.getHeight() / 2;
            float finalRadius = (float) Math.hypot(viewWidth, viewHeight);
            Animator animator = ViewAnimationUtils.createCircularReveal(view, viewWidth, viewHeight, 0, finalRadius);
            animator.setDuration(duration);
            animator.setStartDelay(startDelay);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationEnd(animation);
                    view.setVisibility(View.VISIBLE);

                }
            });
            animator.start();

        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        public void revealViewFromRightHide(final View view, int duration) {
            int viewWidth = view.getWidth();
            int viewHeight = view.getHeight() / 2;
            float startRadius = (float) Math.hypot(viewWidth, viewHeight);
            Animator animator = ViewAnimationUtils.createCircularReveal(view, 0, viewHeight, startRadius, 0);
            animator.setDuration(duration);
            animator.addListener(new AnimatorListenerAdapter() {

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    view.setVisibility(View.INVISIBLE);
                }
            });
            animator.start();
        }


        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        public void revealViewFromTopShow(final View view, int duration, int startDelay) {
            int viewWidth = view.getWidth() / 2;
            int viewHeight = view.getHeight();
            float finalRadius = (float) Math.hypot(viewWidth, viewHeight);
            Animator animator = ViewAnimationUtils.createCircularReveal(view, viewWidth, 0, 0, finalRadius);
            animator.setDuration(duration);
            animator.setStartDelay(startDelay);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationEnd(animation);
                    view.setVisibility(View.VISIBLE);

                }
            });
            animator.start();

        }


        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        public void revealViewFromTopHide(final View view, int duration) {
            int viewWidth = view.getWidth() / 2;
            int viewHeight = view.getHeight();
            float startRadius = (float) Math.hypot(viewWidth, viewHeight);
            Animator animator = ViewAnimationUtils.createCircularReveal(view, viewWidth, viewHeight, startRadius, 0);
            animator.setDuration(duration);
            animator.addListener(new AnimatorListenerAdapter() {

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    view.setVisibility(View.INVISIBLE);
                }
            });
            animator.start();
        }


        /**
         * Combines Animator Set into single Animator set.
         *
         * @param animatorSets An individual or a list  AnimatorSet that should bind to 1 set.
         * @return a Single Combined Animator Set.
         */
        public AnimatorSet bindSets(ArrayList<AnimatorSet> animatorSets) {
            AnimatorSet fullAnimatorSet = new AnimatorSet();
            for (AnimatorSet animatorSet : animatorSets) {
                fullAnimatorSet.playTogether(animatorSet);
            }
            return fullAnimatorSet;
        }

        /**
         * Combines Animator Set into single Animator set.
         *
         * @param animatorSets An individual or a list  AnimatorSet that should bind to 1 set.
         * @return a Single Combined Animator Set.
         */
        public AnimatorSet bindSets(AnimatorSet... animatorSets) {
            AnimatorSet fullAnimatorSet = new AnimatorSet();
            for (AnimatorSet animatorSet : animatorSets) {
                fullAnimatorSet.playTogether(animatorSet);
            }
            return fullAnimatorSet;
        }


        /**
         * Combines Animator Set and set the play sequence to one after another provided.
         *
         * @param animatorSets An individual or a list  AnimatorSet that should bind to 1 set.
         * @return a Single Combined Animator Set.
         */
        public AnimatorSet syncBindSets(AnimatorSet... animatorSets) {
            AnimatorSet fullAnimatorSet = new AnimatorSet();
            fullAnimatorSet.playSequentially(animatorSets);
            return fullAnimatorSet;
        }


        public AnimatorSet build() {
            AnimatorSet fullAnimatorSet;
            fullAnimatorSet = bindSets(animatorSets);
            return fullAnimatorSet;
        }

        public AnimUtilsBuilder translateViewByItSelf(View view, Side side, float translateBy, int duration, Interpolator interpolator, int delay) {

            AnimatorSet animatorSet = new AnimatorSet();
            float distance = 0;
            float origX, origY;
            String propertyName = "x";


            origX = view.getX();
            origY = view.getY();

            if (Side.RIGHT == side) {
                distance = origX + (view.getWidth() * translateBy);
                propertyName = TRANSLATION_X_PROPERTY;
            } else if (Side.LEFT == side) {
                distance = origX + (view.getWidth() * (-translateBy));
                propertyName = TRANSLATION_X_PROPERTY;
            } else if (Side.TOP == side) {
                distance = origY + (view.getHeight() * (-translateBy));
                propertyName = TRANSLATION_Y_PROPERTY;
            } else if (Side.BOTTOM == side) {
                distance = origY + (view.getHeight() * translateBy);
                propertyName = TRANSLATION_Y_PROPERTY;
            }

            ObjectAnimator anim = ObjectAnimator.ofFloat(view, propertyName, distance);

            if (interpolator != null) {
                anim.setInterpolator(interpolator);
            }

            anim.setDuration(duration);
            animatorSet.play(anim);
            animatorSets.add(animatorSet);
            return this;
        }

        public AnimUtilsBuilder scaleViewAnimationFromZeroToItsSelfWithPivot(View view, int duration, float xScale, float yScale, float pivotX, float pivotY, int delay) {
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", xScale);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", yScale);
            scaleX.setInterpolator(new OvershootInterpolator());
            scaleY.setInterpolator(new OvershootInterpolator());

            view.setPivotX(pivotX);
            view.setPivotY(pivotY);

            scaleX.setDuration(duration);
            scaleY.setDuration(duration);

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.play(scaleX).with(scaleY);
            animatorSet.setStartDelay(delay);
            animatorSets.add(animatorSet);
            return this;

        }

        public AnimUtilsBuilder bounceRight(View view) {

            return this;

        }

        public AnimUtilsBuilder bounceView(View view, int duration, int delay) {
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "translationX", 0, 25, -25, 25, -25, 15, -15, 6, -6, 0);
            objectAnimator.setDuration(duration);
            objectAnimator.setStartDelay(delay);

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.play(objectAnimator);
            animatorSets.add(animatorSet);

            return this;

        }

        public AnimUtilsBuilder translateViewToCenterOfOtherView(View animatedView, View animatedViewTo, int duration, int delay) {

            int[] location = new int[2];
            animatedViewTo.getLocationOnScreen(location);

            ObjectAnimator objectAnimatorX = ObjectAnimator.ofFloat(animatedView, "translationX", location[0]);
            ObjectAnimator objectAnimatorY = ObjectAnimator.ofFloat(animatedView, "translationY", location[1]);

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.setDuration(duration);
            animatorSet.setStartDelay(delay);
            animatorSet.playTogether(objectAnimatorX, objectAnimatorY);
            animatorSets.add(animatorSet);
            return this;

        }

        public AnimUtilsBuilder translateViewToCenterOfView(View animatedView, View animatedViewTo, View baseAnimView, int duration, int delay) {

            float y = (animatedViewTo.getHeight() / 2) - (baseAnimView.getHeight() / 2);

            ObjectAnimator objectAnimatorY = ObjectAnimator.ofFloat(animatedView, "translationY", y);

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.setDuration(duration);
            animatorSet.setStartDelay(delay);
            animatorSet.playTogether(objectAnimatorY);
            animatorSets.add(animatorSet);
            return this;

        }


        public AnimUtilsBuilder animateScaleOfParent(final View view, float size, int duration, int delay) {
            ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(view, PropertyValuesHolder.ofFloat("scaleY", size));
            objectAnimator.setDuration(duration);
            objectAnimator.setStartDelay(delay);

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.play(objectAnimator);
            animatorSets.add(animatorSet);

            return this;

        }

        public AnimUtilsBuilder translateViewByItSelfRepeat(View view, Side side, float translateBy, int duration, Interpolator interpolator, int delay) {
            AnimatorSet animatorSet = new AnimatorSet();
            float distance = 0;
            float origX, origY;
            String propertyName = "x";


            origX = view.getX();
            origY = view.getY();

            if (Side.RIGHT == side) {
                distance = origX + (view.getWidth() * translateBy);
                propertyName = TRANSLATION_X_PROPERTY;
            } else if (Side.LEFT == side) {
                distance = origX + (view.getWidth() * (-translateBy));
                propertyName = TRANSLATION_X_PROPERTY;
            } else if (Side.TOP == side) {
                distance = origY + (view.getHeight() * (-translateBy));
                propertyName = TRANSLATION_Y_PROPERTY;
            } else if (Side.BOTTOM == side) {
                distance = origY + (view.getHeight() * translateBy);
                propertyName = TRANSLATION_Y_PROPERTY;
            }

            final ObjectAnimator anim = ObjectAnimator.ofFloat(view, propertyName, distance);

            if (interpolator != null) {
                anim.setInterpolator(interpolator);
            }
            anim.setStartDelay(delay);
            anim.setRepeatMode(ObjectAnimator.REVERSE);
            anim.setRepeatCount(1);
            anim.setDuration(duration);
            animatorSet.play(anim);
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    anim.setStartDelay(800);
                    anim.start();
                }
            });
            animatorSets.add(animatorSet);
            return this;
        }

        public AnimUtilsBuilder setVisibility(View view, int visible) {
            view.setVisibility(visible);
            return this;
        }
    }


    static class ScaleWidthProperty extends Property<View, Integer> {


        public ScaleWidthProperty() {
            super(Integer.class, "width");
        }

        @Override
        public Integer get(View object) {
            return object.getWidth();
        }

        @Override
        public void set(View object, Integer value) {
            object.getLayoutParams().width = value;
            object.setLayoutParams(object.getLayoutParams());
        }
    }

    static class ScaleHeightProperty extends Property<View, Integer> {


        public ScaleHeightProperty() {
            super(Integer.class, "height");
        }

        @Override
        public Integer get(View object) {
            return object.getHeight();
        }

        @Override
        public void set(View object, Integer value) {
            object.getLayoutParams().height = value;
            object.setLayoutParams(object.getLayoutParams());
        }
    }
}
