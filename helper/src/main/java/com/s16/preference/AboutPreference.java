package com.s16.preference;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;


public class AboutPreference  extends Preference implements
		DialogInterface.OnClickListener, DialogInterface.OnDismissListener,
		PreferenceManager.OnActivityDestroyListener {

	private static final float PADDING = 10.0f;
	private static final int[] ATTRS = new int[] {
			android.R.attr.dialogMessage
	};

	private AlertDialog.Builder mBuilder;
	private String mVersionTextFormat;
	private String mAboutMessage;

	/** The dialog, if it is showing. */
	private Dialog mDialog;
	
	public AboutPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }
	
	public AboutPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
	}

	private void init(Context context, AttributeSet attrs, int defStyle) {
		mVersionTextFormat = "Version - %s";
		mAboutMessage = "";

		TypedArray a = context.obtainStyledAttributes(attrs, ATTRS);
		mAboutMessage = a.getString(0);
		a.recycle();
	}
	
	private float convertDpToPixel(float dp){
	    DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
	    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics);
	}
	
	@Override
	protected void onBindView(View view) {
		try {
			PackageInfo pInfo = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0);
			String versionName = pInfo.versionName;
			
			String regex = "^([\\d]+).([\\d]+).([\\d]+).([\\d]{4,})([\\d]{2,})([\\d]{2,})$";
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(versionName);
			if (matcher.matches()) {
				versionName = matcher.group(1)+"."+matcher.group(2)+"."+matcher.group(3);
				versionName += " (Date: "+matcher.group(6)+"/"+matcher.group(5)+"/"+matcher.group(4)+")";
			}
			
			setSummary(String.format(mVersionTextFormat, versionName));
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
		super.onBindView(view);
	}

	@Override
	public void onActivityDestroy() {
		if (mDialog == null || !mDialog.isShowing()) {
			return;
		}
		mDialog.dismiss();
	}

	@Override
	public void onClick(DialogInterface dialogInterface, int which) {

	}

	@Override
	protected void onClick() {
		if (mDialog != null && mDialog.isShowing()) return;
		showDialog(null);
	}

	@Override
	public void onDismiss(DialogInterface dialogInterface) {
		mDialog = null;
	}

	/**
	 * Shows the dialog associated with this Preference. This is normally initiated
	 * automatically on clicking on the preference. Call this method if you need to
	 * show the dialog on some other event.
	 *
	 * @param state Optional instance state to restore on the dialog
	 */
	protected void showDialog(Bundle state) {
		mBuilder = new AlertDialog.Builder(getContext())
				.setTitle(getTitle()) // R.string.prefs_about
				.setIcon(android.R.drawable.ic_dialog_info)
				.setPositiveButton(null, null)
				.setNegativeButton(android.R.string.ok, this);

		View contentView = onCreateDialogView();
		mBuilder.setView(contentView);

		// Create the dialog
		final Dialog dialog = mDialog = mBuilder.create();
		if (state != null) {
			dialog.onRestoreInstanceState(state);
		}
		dialog.setOnDismissListener(this);
		dialog.show();
	}
	
	protected View onCreateDialogView() {
		String html = mAboutMessage;
		final TextView message = new TextView(getContext());
		int padding = (int)convertDpToPixel(PADDING);
		message.setPadding(padding, padding, padding, padding);
		//message.setTextColor(context.getResources().getColor(android.R.color.black));
		message.setMovementMethod(LinkMovementMethod.getInstance());
		message.setText(Html.fromHtml(html));
		return message;
	}

	@Override
	protected Parcelable onSaveInstanceState() {
		final Parcelable superState = super.onSaveInstanceState();
		if (mDialog == null || !mDialog.isShowing()) {
			return superState;
		}

		final SavedState myState = new SavedState(superState);
		myState.isDialogShowing = true;
		myState.dialogBundle = mDialog.onSaveInstanceState();
		return myState;
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		if (state == null || !state.getClass().equals(SavedState.class)) {
			// Didn't save state for us in onSaveInstanceState
			super.onRestoreInstanceState(state);
			return;
		}

		SavedState myState = (SavedState) state;
		super.onRestoreInstanceState(myState.getSuperState());
		if (myState.isDialogShowing) {
			showDialog(myState.dialogBundle);
		}
	}

	private static class SavedState extends BaseSavedState {
		boolean isDialogShowing;
		Bundle dialogBundle;

		public SavedState(Parcel source) {
			super(source);
			isDialogShowing = source.readInt() == 1;
			dialogBundle = source.readBundle();
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeInt(isDialogShowing ? 1 : 0);
			dest.writeBundle(dialogBundle);
		}

		public SavedState(Parcelable superState) {
			super(superState);
		}

		public static final Creator<SavedState> CREATOR =
				new Creator<SavedState>() {
					public SavedState createFromParcel(Parcel in) {
						return new SavedState(in);
					}

					public SavedState[] newArray(int size) {
						return new SavedState[size];
					}
				};
	}
}
