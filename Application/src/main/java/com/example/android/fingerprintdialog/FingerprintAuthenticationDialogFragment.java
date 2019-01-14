/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.example.android.fingerprintdialog;

import android.app.DialogFragment;
import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A dialog which uses fingerprint APIs to authenticate the user
 */
public class FingerprintAuthenticationDialogFragment extends DialogFragment
        implements FingerprintUiHelper.Callback {

    private FingerprintUiHelper mFingerprintUiHelper;

    private MainActivity mActivity;

    private View mFingerprintContent;

    private Button mCancelButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Do not create a new Fragment when the Activity is re-created such as orientation changes.
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fingerprint_dialog_container, container, false);
        mCancelButton = v.findViewById(R.id.cancel_button);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        mFingerprintContent = v.findViewById(R.id.fingerprint_container);
        mFingerprintUiHelper = new FingerprintUiHelper(
                mActivity.getSystemService(FingerprintManager.class),
                (ImageView) v.findViewById(R.id.fingerprint_icon),
                (TextView) v.findViewById(R.id.fingerprint_status), this);

        mCancelButton.setText(R.string.cancel);
        mFingerprintContent.setVisibility(View.VISIBLE);

        if (!mFingerprintUiHelper.isFingerprintAuthAvailable()) {
            Toast.makeText(mActivity, "FingerprintAuth not Available", Toast.LENGTH_SHORT).show();
            dismiss();
            return null;
        }
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        mFingerprintUiHelper.startListening();
    }

    @Override
    public void onPause() {
        super.onPause();
        mFingerprintUiHelper.stopListening();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (MainActivity) getActivity();
    }

    @Override
    public void onAuthenticated() {
        // Callback from FingerprintUiHelper. Let the activity know that authentication was
        // successful.
        mActivity.onFingerprintAuthed();
        dismiss();
    }

    @Override
    public void onError() {
        Toast.makeText(mActivity, "onError()", Toast.LENGTH_SHORT).show();
    }
}
