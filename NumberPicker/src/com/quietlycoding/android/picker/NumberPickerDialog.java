/*
 * Copyright (C) 2010-2011 Mike Novak <michael.novakjr@gmail.com>
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
 * limitations under the License.
 */
package com.quietlycoding.android.picker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.LayoutInflater;
import android.view.View;

public class NumberPickerDialog extends AlertDialog implements OnClickListener {
    private OnNumberSetListener mListener;
    private NumberPicker mNumberPicker;
    private OnCancelListener mCancel;
    private int maxRefill=10000000;
    private int mInitialValue;
    
    public NumberPickerDialog(Context context, int theme, int initialValue) {
        super(context, theme);
        mInitialValue = initialValue;

        setButton(BUTTON_POSITIVE, context.getString(R.string.dialog_set_number), this);
        setButton(BUTTON_NEGATIVE, context.getString(R.string.dialog_cancel), (OnClickListener) null);
        this.setTitle("Select Minutes");
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.number_picker_pref, null);
        setView(view);

        mNumberPicker = (NumberPicker) view.findViewById(R.id.pref_num_picker);
        mNumberPicker.setCurrent(mInitialValue);
        
    }
    
    public void setRange(int min, int max){
    	mNumberPicker.setRange(min, max);
    }
    public void setCurrent(int current){
    	mNumberPicker.setCurrent(current);
    }
    public void setMaxRefill(int inc){
    	maxRefill = inc;
    }
    public int getMaxRefill(){
    	return maxRefill;
    }
    public void setMinInc(int inc){
    	mNumberPicker.setMinInc(inc);
    }
    public void setOnNumberSetListener(OnNumberSetListener listener) {
        mListener = listener;
    }
    @Override
	public void setOnCancelListener(OnCancelListener listener){
    	mCancel = listener;
    }

    @Override
	public void onClick(DialogInterface dialog, int which) {
        if (mListener != null) {
            mListener.onNumberSet(mNumberPicker.getCurrent());
        }
    }

    public interface OnNumberSetListener {
        public void onNumberSet(int selectedNumber);
    }
}

