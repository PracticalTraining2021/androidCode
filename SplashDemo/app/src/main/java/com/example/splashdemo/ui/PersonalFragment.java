package com.example.splashdemo.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Toast;

import com.example.splashdemo.utils.JsJavaBridge;
import com.example.splashdemo.LoginActivity;
import com.example.splashdemo.MainActivity;
import com.example.splashdemo.R;
import com.example.splashdemo.utils.WebUtil;

import WebKit.Bean.LoginBean;
import WebKit.RetrofitFactory;
import WebKit.Service.LoginService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


public class PersonalFragment extends Fragment {
    private WebView personalWebView;
    private ValueCallback<Uri> uploadFile;
    private ValueCallback<Uri[]> uploadFiles;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal, container, false);

        judgeState();

        personalWebView = (WebView) view.findViewById(R.id.personal_webView);

        WebUtil webUtil = new WebUtil(personalWebView, getContext());
        webUtil.webViewSetting("PersonalCenter", 0);

        personalWebView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
                    if (personalWebView != null && personalWebView.canGoBack()){
                        personalWebView.goBack();
                    } else {
                        getActivity().getFragmentManager().popBackStack();
                    }
                    return true;
                }
                return false;
            }
        });
        //???Vue??????
        personalWebView.addJavascriptInterface(new JsJavaBridge(getActivity(), personalWebView, getContext()), "$App");
        //???????????????
        personalWebView.setWebChromeClient(new WebChromeClient() {
            public boolean onShowFileChooser(WebView webView,
                                             ValueCallback<Uri[]> filePathCallback,
                                             WebChromeClient.FileChooserParams fileChooserParams) {
                uploadFiles = filePathCallback;
                openFileChooseProcess();
                return true;
            }

            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                uploadFile = uploadFile;
                openFileChooseProcess();
            }
        });
        return view;
    }

    /**
     * ??????????????????
     */
    private void judgeState(){
        LoginService service = RetrofitFactory.getLoginService(getActivity());
        Call<LoginBean> call = service.getUserMessage();
        call.enqueue(new Callback<LoginBean>() {
            @Override
            public void onResponse(Call<LoginBean> call, Response<LoginBean> response) {

            }

            @Override
            public void onFailure(Call<LoginBean> call, Throwable t) {
                Toast.makeText(getContext(), "???????????????", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivityForResult(intent, 100);
                getActivity().overridePendingTransition(R.anim.rightin_enter, R.anim.no_anim);
            }
        });
    }

    /**
     * ????????????
     */
    private void openFileChooseProcess() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("*/*");
        startActivityForResult(Intent.createChooser(i, "????????????"), 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 100) {
            Boolean result = data.getExtras().getBoolean("result", false);
            if (result) {
                //?????????????????????WebView
                personalWebView.reload();
            } else {
                //????????????????????????
                MainActivity.instance.jumpToItem(R.id.navigation_home);
            }
        }
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 0:
                    if (uploadFile != null) {
                        Uri res = data == null || resultCode != RESULT_OK ? null
                                : data.getData();
                        uploadFile.onReceiveValue(res);
                        uploadFile = null;
                    }
                    if (uploadFiles != null) {
                        Uri res = data == null || resultCode != RESULT_OK ? null
                                : data.getData();
                        uploadFiles.onReceiveValue(new Uri[]{res});
                        uploadFiles = null;
                    }
                    break;
                default:
                    break;
            }
        } else if (resultCode == RESULT_CANCELED) {
            if (uploadFile != null) {
                uploadFile.onReceiveValue(null);
                uploadFile = null;
            }
            if (uploadFiles != null) {
                uploadFiles.onReceiveValue(null);
                uploadFiles = null;
            }
        }
    }
}