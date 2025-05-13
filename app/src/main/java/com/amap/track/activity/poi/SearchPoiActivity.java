package com.amap.track.activity.poi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Poi;
import com.amap.api.navi.view.PoiInputItemWidget;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.track.demo.R;
import com.amap.track.activity.route.RestRouteShowActivity;

import java.util.ArrayList;
import java.util.List;

public class SearchPoiActivity extends Activity implements TextWatcher,
        Inputtips.InputtipsListener, AdapterView.OnItemClickListener, View.OnTouchListener, View.OnClickListener, PoiSearch.OnPoiSearchListener {
    private AutoCompleteTextView mKeywordText;
    private ListView resultList;
    private List<Tip> mCurrentTipList;
    private SearchResultAdapter resultAdapter;
    private ProgressBar loadingBar;
    private TextView tvMsg;
    private Poi selectedPoi;
    private String city = "北京市";
    private int pointType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_poi);
        findViews();
        resultList.setOnItemClickListener(this);
        resultList.setOnTouchListener(this);
        tvMsg.setVisibility(View.GONE);
        mKeywordText.addTextChangedListener(this);
        mKeywordText.requestFocus();
        Bundle bundle = getIntent().getExtras();
        pointType = bundle.getInt("pointType", -1);
    }


    private void findViews() {
        mKeywordText = (AutoCompleteTextView) findViewById(R.id.search_input);
        resultList = (ListView) findViewById(R.id.resultList);
        loadingBar = (ProgressBar) findViewById(R.id.search_loading);
        tvMsg = (TextView) findViewById(R.id.tv_msg);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        try {
            {
                if (tvMsg.getVisibility() == View.VISIBLE) {
                    tvMsg.setVisibility(View.GONE);
                }
                String newText = s.toString().trim();
                if (!TextUtils.isEmpty(newText)) {
                    setLoadingVisible(true);
                    //构造 InputtipsQuery 对象，通过 InputtipsQuery(java.lang.String keyword, java.lang.String city) 设置搜索条件。
                    InputtipsQuery inputquery = new InputtipsQuery(newText, city);
                    //构造 Inputtips 对象，并设置监听
                    Inputtips inputTips = new Inputtips(getApplicationContext(), inputquery);
                    inputTips.setInputtipsListener(this);
                    //调用 PoiSearch 的 requestInputtipsAsyn() 方法发送请求。
                    inputTips.requestInputtipsAsyn();
                } else {
                    resultList.setVisibility(View.GONE);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void setLoadingVisible(boolean isVisible) {
        if (isVisible) {
            loadingBar.setVisibility(View.VISIBLE);
        } else {
            loadingBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //点击提示后再次进行搜索，获取POI出入口信息
        if (mCurrentTipList != null) {
            Tip tip = (Tip) parent.getItemAtPosition(position);
            selectedPoi = new Poi(tip.getName(), new LatLng(tip.getPoint().getLatitude(), tip.getPoint().getLongitude()), tip.getPoiID());
            if (!TextUtils.isEmpty(selectedPoi.getPoiId())) {
                //构造 PoiSearch.Query 对象，通过 PoiSearch.Query(String query, String ctgr, String city) 设置搜索条件。
                //keyWord表示搜索字符串，
//第二个参数表示POI搜索类型，二者选填其一，选用POI搜索类型时建议填写类型代码，码表可以参考下方（而非文字）
//cityCode表示POI搜索区域，可以是城市编码也可以是城市名称，也可以传空字符串，空字符串代表全国在全国范围内进行搜索
                PoiSearch.Query query = new PoiSearch.Query(selectedPoi.getName(), "", city);
                query.setDistanceSort(false);
                query.requireSubPois(true);
                PoiSearch poiSearch = null;
                try {
                    //构造 PoiSearch 对象，并设置监听。
                    poiSearch = new PoiSearch(getApplicationContext(), query);
                    poiSearch.setOnPoiSearchListener(this);
                    //通过关键字检索、周边检索以及多边形检索，或者任意形式得到的高德POI ID信息，可通过ID检索来获取POI完整详细信息。
                    poiSearch.searchPOIIdAsyn(selectedPoi.getPoiId());
                } catch (AMapException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    @Override
    public void onGetInputtips(List<Tip> tipList, int rCode) {
        //通过回调接口 onGetInputtips 解析返回的结果，获取输入提示返回的信息。
        setLoadingVisible(false);
        try {
            if (rCode == 1000) {
                mCurrentTipList = new ArrayList<Tip>();
                for (Tip tip : tipList) {
                    if (null == tip.getPoint()) {
                        continue;
                    }
                    mCurrentTipList.add(tip);
                }

                if (null == mCurrentTipList || mCurrentTipList.isEmpty()) {
                    tvMsg.setText("抱歉，没有搜索到结果，请换个关键词试试");
                    tvMsg.setVisibility(View.VISIBLE);
                    resultList.setVisibility(View.GONE);
                } else {
                    resultList.setVisibility(View.VISIBLE);
                    resultAdapter = new SearchResultAdapter(getApplicationContext(), mCurrentTipList);
                    resultList.setAdapter(resultAdapter);
                    resultAdapter.notifyDataSetChanged();
                }
            } else {
                tvMsg.setText("出错了，请稍后重试");
                tvMsg.setVisibility(View.VISIBLE);
            }
        } catch (Throwable e) {
            tvMsg.setText("出错了，请稍后重试");
            tvMsg.setVisibility(View.VISIBLE);

        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {
//通过回调接口 onPoiSearched 解析返回的结果，将查询到的 POI 以绘制点的方式显示在地图上。
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int errorCode) {
        //通过回调接口 onPoiItemSearched 解析返回的结果。由于是检索具体的某一个POI，直接回调该POI对象 PoiItem。
        try {
            LatLng latLng = null;
            int code = 0;
            if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
                if (poiItem == null) {
                    return;
                }
                LatLonPoint exitP = poiItem.getExit();
                LatLonPoint enterP = poiItem.getEnter();
                if (pointType == PoiInputItemWidget.TYPE_START) {
                    code = 100;
                    if (exitP != null) {
                        latLng = new LatLng(exitP.getLatitude(), exitP.getLongitude());
                    } else {
                        if (enterP != null) {
                            latLng = new LatLng(enterP.getLatitude(), enterP.getLongitude());
                        }
                    }
                }
                if (pointType == PoiInputItemWidget.TYPE_DEST) {
                    code = 200;
                    if (enterP != null) {
                        latLng = new LatLng(enterP.getLatitude(), enterP.getLongitude());
                    }
                }
            }
            Poi poi;
            if (latLng != null) {
                poi = new Poi(selectedPoi.getName(), latLng, selectedPoi.getPoiId());
            } else {
                poi = selectedPoi;
            }
            Intent intent = new Intent(this, RestRouteShowActivity.class);
            intent.putExtra("poi", poi);
            setResult(code, intent);
            finish();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
