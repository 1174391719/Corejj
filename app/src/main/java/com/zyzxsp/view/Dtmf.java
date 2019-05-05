package com.zyzxsp.view;

import android.text.TextUtils;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.TextView;

import com.zyzxsp.R;

import java.util.logging.Logger;

public class Dtmf implements OnTouchListener, OnLongClickListener {
    private static final Logger LOGGER = Logger.getLogger(Dtmf.class.getName());
    private SparseArray<String> dtmfMap = new SparseArray<>();

    public interface DtmfListener {
        void onDtmfKey(String key);
    }

    private final View dtmfView;
    private final TextView text;
    private final DtmfListener l;


    private boolean longClicked;

    public Dtmf(View dtmfView, DtmfListener l) {
//        Asserts.assertNullArgument(dtmfView, "dtmfView cannot be null");
//        Asserts.assertNullArgument(l, "DtmfListener cannot be null");
        this.dtmfView = dtmfView;
        this.l = l;
        text = (TextView) dtmfView.findViewById(R.id.dtmf_text);
        setClickListener(R.id.dtmf_0, R.id.dtmf_1, R.id.dtmf_2, R.id.dtmf_3, R.id.dtmf_4, R.id.dtmf_5, R.id.dtmf_6,
                R.id.dtmf_7, R.id.dtmf_8, R.id.dtmf_9, R.id.dtmf_0, R.id.dtmf_p, R.id.dtmf_s);
        setLongClickListener(R.id.dtmf_0);
        setLongClickListener(R.id.dtmf_s);

        dtmfMap.put(R.id.dtmf_0, "0");
        dtmfMap.put(R.id.dtmf_1, "1");
        dtmfMap.put(R.id.dtmf_2, "2");
        dtmfMap.put(R.id.dtmf_3, "3");
        dtmfMap.put(R.id.dtmf_4, "4");
        dtmfMap.put(R.id.dtmf_5, "5");
        dtmfMap.put(R.id.dtmf_6, "6");
        dtmfMap.put(R.id.dtmf_7, "7");
        dtmfMap.put(R.id.dtmf_8, "8");
        dtmfMap.put(R.id.dtmf_9, "9");
        dtmfMap.put(R.id.dtmf_s, "*");
        dtmfMap.put(R.id.dtmf_p, "#");
    }

    private void setClickListener(int... ids) {
        for (int id : ids) {
            dtmfView.findViewById(id).setOnTouchListener(this);
        }
    }

    private void setLongClickListener(int id) {
        dtmfView.findViewById(id).setOnLongClickListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_UP:
                if (v instanceof TextView && !longClicked) {
                    String dtmf = dtmfMap.get(v.getId());

                    CharSequence charSequence = TextUtils.ellipsize(text.getText() + dtmf, text.getPaint(), text.getWidth(), TextUtils.TruncateAt.START);
                    text.setText(charSequence);
                    if (l != null) l.onDtmfKey(dtmf);
                }
                longClicked = false;
                v.performClick();
                break;
        }
        return false;
    }

    @Override
    public boolean onLongClick(View v) {
        if (v.getId() == R.id.dtmf_0) {
            text.setText(text.getText() + "+");
            if (l != null) l.onDtmfKey("+");
            longClicked = true;
        }
        if (v.getId() == R.id.dtmf_s) {
            text.setText(text.getText() + ".");
            if (l != null) l.onDtmfKey(".");
            longClicked = true;
        }
        return true;
    }

    public void show() {
        LOGGER.info("lsx  show dtmfView" + dtmfView);
        dtmfView.setVisibility(View.VISIBLE);
    }

    public void hide() {
        dtmfView.setVisibility(View.GONE);
        clearText();
    }

    public boolean isVisible() {
        return dtmfView.getVisibility() == View.VISIBLE;
    }

    public String getText() {
        return text.getText().toString();
    }

    public void clearText() {
        text.setText("");
    }
}
