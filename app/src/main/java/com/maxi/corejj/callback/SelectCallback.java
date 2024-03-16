package com.maxi.corejj.callback;

import java.io.Serializable;

public interface SelectCallback extends Serializable {
    void onPositive();

    void onNegative();
}
