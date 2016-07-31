package com.example.user.yamblz_find_number;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

public class CallReceiver extends BroadcastReceiver {
    private static boolean incomingCall = false;
    private static WindowManager windowManager;
    private static ViewGroup windowLayout;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.PHONE_STATE")) {
            String phoneState = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            if (phoneState.equals(TelephonyManager.EXTRA_STATE_RINGING)) {

                String phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                incomingCall = true;
                showWindow(context, phoneNumber);

            } else if (phoneState.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                if (incomingCall) {
                    closeWindow();
                    incomingCall = false;
                }
            } else if (phoneState.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                if (incomingCall) {
                    closeWindow();
                    incomingCall = false;
                }
            }
        }
    }

    private void showWindow(Context context, String phone) {
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ERROR, 0,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP;

        windowLayout = (ViewGroup) layoutInflater.inflate(R.layout.info, null);

        WebView webView = (WebView) windowLayout.findViewById(R.id.webView);
        webView.setWebViewClient(new MyWebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        //выводим список ссылок из яндекса
        webView.loadUrl("https://m.yandex.ru/search/?text=" + phone + "&lr=213");
        Button buttonClose=(Button) windowLayout.findViewById(R.id.buttonClose);
        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeWindow();
            }
        });

        windowManager.addView(windowLayout, params);
    }

    private void closeWindow() {
        if (windowLayout != null){
            windowManager.removeView(windowLayout);
            windowLayout = null;
        }
    }

    private class MyWebViewClient extends WebViewClient
    {
        //переопределил чтобы webView не открывала браузер
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url)
        {
            view.loadUrl(url);
            return true;
        }
    }
}