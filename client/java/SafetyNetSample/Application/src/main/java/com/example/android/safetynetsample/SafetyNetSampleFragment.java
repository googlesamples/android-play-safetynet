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

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.MenuItem;

import com.example.android.common.logger.Log;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.safetynet.SafetyNetClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Random;

/**
 * Sample that demonstrates the use of the SafetyNet Google Play Services API.
 * It handles option items (defined in menu/main.xml) to call the API and share its result via an
 * Intent.
 * Note that you have to configure an API key for the SafetyNet attestation API first. This can be
 * done by overriding the gradle.properties file.
 */
public class SafetyNetSampleFragment extends Fragment {

    private static final String TAG = "SafetyNetSample";

    private static final String BUNDLE_RESULT = "result";

    private final Random mRandom = new SecureRandom();

    private String mResult;

    private String mPendingResult;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (savedInstanceState != null && savedInstanceState.containsKey(BUNDLE_RESULT)) {
            // Store data as pending result for display after activity has resumed.
            mPendingResult = savedInstanceState.getString(BUNDLE_RESULT);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPendingResult != null) {
            // Restore the previous result once the Activity has fully resumed and the logging
            // framework has been set up.
            mResult = mPendingResult;
            mPendingResult = null;
            Log.d(TAG, "SafetyNet result:\n" + mResult + "\n");
        }
    }

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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(BUNDLE_RESULT, mResult);
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

        /*
         Call the SafetyNet API asynchronously.
         The result is returned through the success or failure listeners.
         First, get a SafetyNetClient for the foreground Activity.
         Next, make the call to the attestation API. The API key is specified in the gradle build
         configuration and read from the gradle.properties file.
         */
        SafetyNetClient client = SafetyNet.getClient(getActivity());
        Task<SafetyNetApi.AttestationResponse> task = client.attest(nonce, BuildConfig.API_KEY);

        task.addOnSuccessListener(getActivity(), mSuccessListener)
                .addOnFailureListener(getActivity(), mFailureListener);

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
     * Called after successfully communicating with the SafetyNet API.
     * The #onSuccess callback receives an
     * {@link com.google.android.gms.safetynet.SafetyNetApi.AttestationResponse} that contains a
     * JwsResult with the attestation result.
     */
    private OnSuccessListener<SafetyNetApi.AttestationResponse> mSuccessListener =
            new OnSuccessListener<SafetyNetApi.AttestationResponse>() {
                @Override
                public void onSuccess(SafetyNetApi.AttestationResponse attestationResponse) {
                    /*
                     Successfully communicated with SafetyNet API.
                     Use result.getJwsResult() to get the signed result data. See the server
                     component of this sample for details on how to verify and parse this result.
                     */
                    mResult = attestationResponse.getJwsResult();
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
                }
            };

    /**
     * Called when an error occurred when communicating with the SafetyNet API.
     */
    private OnFailureListener mFailureListener = new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            // An error occurred while communicating with the service.
            mResult = null;

            if (e instanceof ApiException) {
                // An error with the Google Play Services API contains some additional details.
                ApiException apiException = (ApiException) e;
                Log.d(TAG, "Error: " +
                        CommonStatusCodes.getStatusCodeString(apiException.getStatusCode()) + ": " +
                        apiException.getStatusMessage());
            } else {
                // A different, unknown type of error occurred.
                Log.d(TAG, "ERROR! " + e.getMessage());
            }

        }
    };

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
