/*
* Copyright 2016 Google Inc.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.example.android.safetynetsample;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;

import com.example.android.common.logger.Log;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.MenuItem;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Random;

/**
 * Sample that demonstrates the use of the SafetyNet Google Play Services API.
 * It handles option items (defined in menu/main.xml) to call the API and share its result via an
 * Intent.
 */
public class SafetyNetSampleFragment extends Fragment
        implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "SafetyNetSample";

    private final Random mRandom = new SecureRandom();

    private GoogleApiClient mGoogleApiClient;

    private String mResult;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create an automanaged GoogleApiClient
        buildGoogleApiClient();

        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                shareResult();
                return true;
            case R.id.action_verify:
                sendSafetyNetRequest();
                return true;
        }
        return false;
    }

    private void sendSafetyNetRequest() {
        Log.i(TAG, "Sending SafetyNet API request.");

         /*
        Create a nonce for this request.
        The nonce is returned as part of the response from the
        SafetyNet API. Here we append the string to a number of random bytes to ensure it larger
        than the minimum 16 bytes required.
        Read out this value and verify it against the original request to ensure the
        response is correct and genuine.
        NOTE: A nonce must only be used once and a different nonce should be used for each request.
        As a more secure option, you can obtain a nonce from your own server using a secure
        connection. Here in this sample, we generate a String and append random bytes, which is not
        very secure. Follow the tips on the Security Tips page for more information:
        https://developer.android.com/training/articles/security-tips.html#Crypto
         */
        // TODO(developer): Change the nonce generation to include your own, used once value,
        // ideally from your remote server.
        String nonceData = "Safety Net Sample: " + System.currentTimeMillis();
        byte[] nonce = getRequestNonce(nonceData);

        // Call the SafetyNet API asynchronously. The result is returned through the result callback.
        SafetyNet.SafetyNetApi.attest(mGoogleApiClient, nonce)
                .setResultCallback(new ResultCallback<SafetyNetApi.AttestationResult>() {

                    @Override
                    public void onResult(SafetyNetApi.AttestationResult result) {
                        Status status = result.getStatus();
                        if (status.isSuccess()) {
                            /*
                             Successfully communicated with SafetyNet API.
                             Use result.getJwsResult() to get the signed result data. See the server
                             component of this sample for details on how to verify and parse this
                             result.
                             */
                            mResult = result.getJwsResult();
                            Log.d(TAG, "Success! SafetyNet result:\n" + mResult + "\n");

                            /*
                             TODO(developer): Forward this result to your server together with
                             the nonce for verification.
                             You can also parse the JwsResult locally to confirm that the API
                             returned a response by checking for an 'error' field first and before
                             retrying the request with an exponential backoff.

                             NOTE: Do NOT rely on a local, client-side only check for security, you
                             must verify the response on a remote server!
                             */

                        } else {
                            // An error occurred while communicating with the service.
                            Log.d(TAG, "ERROR! " + status.getStatusCode() + " " + status
                                    .getStatusMessage());
                            mResult = null;
                        }
                    }
                });
    }

    /**
     * Generates a 16-byte nonce with additional data.
     * The nonce should also include additional information, such as a user id or any other details
     * you wish to bind to this attestation. Here you can provide a String that is included in the
     * nonce after 24 random bytes. During verification, extract this data again and check it
     * against the request that was made with this nonce.
     */
    private byte[] getRequestNonce(String data) {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        byte[] bytes = new byte[24];
        mRandom.nextBytes(bytes);
        try {
            byteStream.write(bytes);
            byteStream.write(data.getBytes());
        } catch (IOException e) {
            return null;
        }

        return byteStream.toByteArray();
    }

    /**
     * Constructs an automanaged {@link GoogleApiClient} for the {@link SafetyNet#API}.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addApi(SafetyNet.API)
                .enableAutoManage(getActivity(), this)
                .build();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // Error occurred during connection to Google Play Services that could not be
        // automatically resolved.
        Log.e(TAG,
                "Error connecting to Google Play Services." + connectionResult.getErrorMessage());
    }

    /**
     * Shares the result of the SafetyNet API call via an {@link Intent#ACTION_SEND} intent.
     * You can use this call to extract the result from the device for testing purposes.
     */
    private void shareResult() {
        if (mResult == null) {
            Log.d(TAG, "No result received yet. Run the verification first.");
            return;
        }

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, mResult);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }
}
