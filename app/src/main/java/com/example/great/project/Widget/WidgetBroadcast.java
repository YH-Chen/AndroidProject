package com.example.great.project.Widget;

import android.content.Intent;
import android.os.Bundle;

/**
 * Created by 61915 on 18/01/09.
 */

public class WidgetBroadcast {
    public void send(String sName){
        Bundle bundle = new Bundle();
        bundle.putString("sName", sName);
        Intent intent = new Intent("static_action");

    }
}
