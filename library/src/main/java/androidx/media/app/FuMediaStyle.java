package androidx.media.app;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.RemoteViews;

import androidx.core.app.NotificationBuilderWithBuilderAccessor;

import com.chengfu.android.fuplayer.achieve.dj.R;

public class FuMediaStyle extends NotificationCompat.MediaStyle {

    private static final int MAX_MEDIA_BUTTONS_IN_COMPACT = 3;
    private static final int MAX_MEDIA_BUTTONS = 5;

    @Override
    public NotificationCompat.MediaStyle setShowCancelButton(boolean show) {
        mShowCancelButton = show;
        return this;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void apply(NotificationBuilderWithBuilderAccessor builder) {
        if (mShowCancelButton) {
            builder.getBuilder().setOngoing(true);
        }
    }

    @Override
    public RemoteViews makeContentView(NotificationBuilderWithBuilderAccessor builder) {
        return generateContentView();
    }

    @SuppressLint("RestrictedApi")
    RemoteViews generateContentView() {
        RemoteViews view = applyStandardTemplate(false /* showSmallIcon */,
                getContentViewLayoutResource(), true /* fitIn1U */);

        final int numActions = mBuilder.mActions.size();
        final int numActionsInCompact = mActionsToShowInCompact == null
                ? 0
                : Math.min(mActionsToShowInCompact.length, MAX_MEDIA_BUTTONS_IN_COMPACT);
        view.removeAllViews(R.id.media_actions);
        if (numActionsInCompact > 0) {
            for (int i = 0; i < numActionsInCompact; i++) {
                if (i >= numActions) {
                    throw new IllegalArgumentException(String.format(
                            "setShowActionsInCompactView: action %d out of bounds (max %d)",
                            i, numActions - 1));
                }

                final androidx.core.app.NotificationCompat.Action action =
                        mBuilder.mActions.get(mActionsToShowInCompact[i]);
                final RemoteViews button = generateMediaActionButton(action);
                view.addView(R.id.media_actions, button);
            }
        }
        if (mShowCancelButton) {
            view.setViewVisibility(R.id.end_padder, View.GONE);
            view.setViewVisibility(R.id.cancel_action, View.VISIBLE);
            view.setOnClickPendingIntent(R.id.cancel_action, mCancelButtonIntent);
            view.setInt(R.id.cancel_action, "setAlpha", mBuilder.mContext
                    .getResources().getInteger(R.integer.cancel_button_image_alpha));
        } else {
            view.setViewVisibility(R.id.end_padder, View.VISIBLE);
            view.setViewVisibility(R.id.cancel_action, View.GONE);
        }
        return view;
    }

    @SuppressLint("RestrictedApi")
    private RemoteViews generateMediaActionButton(
            androidx.core.app.NotificationCompat.Action action) {
        final boolean tombstone = (action.getActionIntent() == null);
        RemoteViews button = new RemoteViews(mBuilder.mContext.getPackageName(),
                R.layout.fu_notification_media_action);
        button.setImageViewResource(R.id.action0, action.getIcon());
        if (!tombstone) {
            button.setOnClickPendingIntent(R.id.action0, action.getActionIntent());
        }
        button.setContentDescription(R.id.action0, action.getTitle());
        return button;
    }

    int getContentViewLayoutResource() {
        return R.layout.fu_media_notification;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public RemoteViews makeBigContentView(NotificationBuilderWithBuilderAccessor builder) {
        final int actionCount = Math.min(mBuilder.mActions.size(), MAX_MEDIA_BUTTONS);
        RemoteViews big = applyStandardTemplate(false /* showSmallIcon */,
                getBigContentViewLayoutResource(actionCount), false /* fitIn1U */);

        big.removeAllViews(R.id.media_actions);
        if (actionCount > 0) {
            for (int i = 0; i < actionCount; i++) {
                final RemoteViews button = generateMediaActionButton(mBuilder.mActions.get(i));
                big.addView(R.id.media_actions, button);
            }
        }
        if (mShowCancelButton) {
            big.setViewVisibility(R.id.cancel_action, View.VISIBLE);
            big.setInt(R.id.cancel_action, "setAlpha", mBuilder.mContext
                    .getResources().getInteger(R.integer.cancel_button_image_alpha));
            big.setOnClickPendingIntent(R.id.cancel_action, mCancelButtonIntent);
        } else {
            big.setViewVisibility(R.id.cancel_action, View.GONE);
        }
        return big;
    }

    int getBigContentViewLayoutResource(int actionCount) {
        return R.layout.fu_media_notification_big;
    }
}
