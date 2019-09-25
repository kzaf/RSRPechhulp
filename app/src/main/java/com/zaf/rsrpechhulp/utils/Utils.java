package com.zaf.rsrpechhulp.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import com.zaf.rsrpechhulp.R;

public class Utils {

    /**
     * Highlights a selected text as hyperlink.
     * @param tv the TextView that contains the hyperlink
     * @param textToHighlight the TextView that will be highlighted
     * @param onClickListener the Callback listener when the user click the hyperlink
     */
    public static void setClickableHighlightedText(final TextView tv, String textToHighlight,
                                                   final View.OnClickListener onClickListener) {
        String tvt = tv.getText().toString();
        int ofe = tvt.indexOf(textToHighlight);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                if (onClickListener != null) onClickListener.onClick(textView);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(tv.getContext().getResources().getColor(R.color.colorPrimary));
                ds.setUnderlineText(true);
            }
        };

        SpannableString wordToSpan = new SpannableString(tv.getText());
        for (int ofs = 0; ofs < tvt.length() && ofe != -1; ofs = ofe + 1) {
            ofe = tvt.indexOf(textToHighlight, ofs);
            if (ofe == -1)
                break;
            else {
                wordToSpan.setSpan(clickableSpan, ofe, ofe +
                        textToHighlight.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                tv.setText(wordToSpan, TextView.BufferType.SPANNABLE);
                tv.setMovementMethod(LinkMovementMethod.getInstance());
            }
        }
    }

    // dialIfAvailable method used to starts dialer if available with given phone number
    public static void dialIfAvailable(Context context, String phoneNumber) {
        // ACTION_CALL directly calls the number instead of CALL_PHONE
        // where first it displays the number in the dialer
        Intent dialIntent = new Intent(Intent.ACTION_CALL);
        dialIntent.setData(Uri.parse("tel:" + phoneNumber));
        //check if exists activity that can be started with dialIntent
        if (context.getPackageManager().queryIntentActivities(dialIntent, 0).size() > 0) {
            context.startActivity(dialIntent);
        }
    }

}
