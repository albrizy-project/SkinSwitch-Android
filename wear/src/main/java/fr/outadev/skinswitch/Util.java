/*
 * SkinSwitch - Util
 * Copyright (C) 2014-2015  Baptiste Candellier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package fr.outadev.skinswitch;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.Wearable;

import java.io.InputStream;

/**
 * Random useful methods.
 *
 * @author outadoc
 */
public abstract class Util {

	public static final String TAG = "SkinSwitch/Wear";

	/**
	 * Fades a view into another view.
	 *
	 * @param oldView  the view that's currently visible and will be replaced
	 * @param newView  the view that will be visible once it's replaced the old view
	 * @param animTime the animation time, in milliseconds
	 */
	public static void crossfade(final View oldView, final View newView, int animTime) {

		// Set the new view to 0% opacity but visible, so that it is visible
		// (but fully transparent) during the animation.
		newView.setAlpha(0f);
		newView.setVisibility(View.VISIBLE);

		// Animate the new view to 100% opacity, and clear any animation
		// listener set on the view.
		newView.animate()
				.alpha(1f)
				.setDuration(animTime)
				.setListener(null);

		// Animate the old view to 0% opacity. After the animation ends,
		// set its visibility to GONE as an optimization step (it won't
		// participate in layout passes, etc.)
		oldView.animate()
				.alpha(0f)
				.setDuration(animTime)
				.setListener(new AnimatorListenerAdapter() {
					@Override
					public void onAnimationEnd(Animator animation) {
						oldView.setVisibility(View.GONE);
					}
				});
	}

	/**
	 * Loads a bitmap from an asset that's been downloaded from the phone.
	 *
	 * @param asset           the asset coming from the client
	 * @param googleApiClient the API client, used to get the asset's file descriptor
	 * @return the asset as a bitmap
	 */
	public static Bitmap loadBitmapFromAsset(Asset asset, GoogleApiClient googleApiClient) {
		if(asset == null) {
			throw new IllegalArgumentException("Asset must be non-null");
		}

		// convert asset into a file descriptor and block until it's ready
		InputStream assetInputStream = Wearable.DataApi.getFdForAsset(googleApiClient, asset).await().getInputStream();

		if(assetInputStream == null) {
			Log.w(TAG, "Requested an unknown Asset.");
			return null;
		}

		// decode the stream into a bitmap
		return BitmapFactory.decodeStream(assetInputStream);
	}

}
