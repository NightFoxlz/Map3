package com.example.liav.map3.CustomClickListeners;

import android.view.View;

/**
 * Created by Liav on 3/7/2018.
 */

public interface ClickListener {
    void onClick(View view, int position);

    void onLongClick(View view, int position);
}
