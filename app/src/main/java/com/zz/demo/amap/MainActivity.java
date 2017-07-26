package com.zz.demo.amap;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.OnInfoWindowClickListener;
import com.amap.api.maps.AMap.OnMapLongClickListener;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapNaviCameraInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AMapServiceAreaInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.RouteSearch.OnRouteSearchListener;
import com.amap.api.services.route.WalkRouteResult;
import com.autonavi.tbt.TrafficFacilityInfo;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.zz.demo.amap.bean.MessageEntity;
import com.zz.demo.amap.route.DrivingRouteOverLay;
import com.zz.demo.amap.util.AMapUtil;
import com.zz.demo.amap.util.BigDecimalUtils;
import com.zz.demo.amap.util.PermissionUtils;
import com.zz.demo.amap.util.ToastUtil;
import com.zz.demo.amap.util.VibratorUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.functions.Action1;

public class MainActivity extends AppCompatActivity implements LocationSource,
        AMapLocationListener, OnRouteSearchListener, OnGeocodeSearchListener, OnMapLongClickListener,
        OnInfoWindowClickListener, AMapNaviListener {

    private static String NO_RESULT = "对不起，没有搜索到相关数据！";

    @Bind(R.id.map)
    MapView mapView;
    @Bind(R.id.input_edittext)
    TextView mInputEdittext;
    @Bind(R.id.btn_search)
    TextView btnSearch;
    @Bind(R.id.tv_navi)
    TextView mTvNavi;
    private AMap aMap;

    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private MainActivity mContext;
    private Circle mCircle;
    private Marker mLocMarker;
    public static final String LOCATION_MARKER_FLAG = "mylocation";
    private LatLonPoint mStartPoint = new LatLonPoint(39.942295, 116.335891);//起点，116.335891,39.942295
    private LatLonPoint mEndPoint = new LatLonPoint(39.995576, 116.481288);//终点，116.481288,39.995576
    private static final int STROKE_COLOR = Color.argb(180, 3, 145, 255);
    private static final int FILL_COLOR = Color.argb(100, 149, 237, 180);
    private static final int TRANSPARENT_COLOR = Color.argb(0, 0, 0, 0);
    private List<NaviLatLng> startList = new ArrayList<NaviLatLng>();
    /**
     * 终点坐标集合［建议就一个终点］
     */
    private List<NaviLatLng> endList = new ArrayList<NaviLatLng>();
    private NaviLatLng endLatlng = new NaviLatLng();
    private NaviLatLng startLatlng = new NaviLatLng();
    private ProgressDialog progDialog;
    private final int ROUTE_TYPE_DRIVE = 2;
    private RouteSearch mRouteSearch;
    private DriveRouteResult mDriveRouteResult;
    private GeocodeSearch geocoderSearch;
    private Circle circle;
    private Marker makerB;
    private LatLng latlngA;
    private LatLng latlngB;
    private float distance;
    private LatLonPoint latLonPointA;
    private LatLonPoint latLonPointB;
    private DrivingRouteOverLay drivingRouteOverlay;
    private String city;
    private EditText edt;
    private AMapNavi mAMapNavi;
    /**
     * 途径点坐标集合
     */
    private List<NaviLatLng> wayList = new ArrayList<NaviLatLng>();

    //标识，用于判断是否只显示一次定位信息和用户重新定位
    private int num = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = MainActivity.this;
        EventBus.getDefault().register(this);//注册
        ButterKnife.bind(this);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        //同时请求多个权限
        RxPermissions.getInstance(MainActivity.this)
                .request(Manifest.permission.ACCESS_COARSE_LOCATION)// 获取位置
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        if (aBoolean) {// 当所有权限都允许之后，返回true
//                            Toast.makeText(mContext, "定位授权成功", Toast.LENGTH_LONG).show();
                            init();
                        } else { //只要有一个权限禁止，返回false， //下一次申请只申请没通过申请的权限
                            PermissionUtils.PermissionDialog(mContext);
                        }
                    }
                });
    }

    /**
     * 初始化AMap对象
     */

    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }
        mRouteSearch = new RouteSearch(this);
        mRouteSearch.setRouteSearchListener(this);
        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);
        progDialog = new ProgressDialog(this);
        mAMapNavi = AMapNavi.getInstance(mContext);
        mAMapNavi.addAMapNaviListener(this);
    }


    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        aMap.setOnMapLongClickListener(this);
        aMap.setOnInfoWindowClickListener(this);// 设置点击infoWindow事件监听器

        // 自定义系统定位小蓝点
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory
                .fromResource(R.mipmap.location_marker));// 设置小蓝点的图标
        myLocationStyle.strokeColor(TRANSPARENT_COLOR);// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(TRANSPARENT_COLOR);// 设置圆形的填充颜色
        // myLocationStyle.anchor(int,int)//设置小蓝点的锚点
        myLocationStyle.strokeWidth(1.0f);// 设置圆形的边框粗细
        // 设置定位的类型为 持续定位不移动到中心点
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW_NO_CENTER);
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        setLocation();
    }

    /**
     * 设置定位
     */
    private void setLocation() {
        mlocationClient = new AMapLocationClient(this);
        mlocationClient.setLocationListener(this);
        mLocationOption = new AMapLocationClientOption();
        //设置定位监听
        mlocationClient.setLocationListener(this);
        //获取一次定位结果：
        mLocationOption.setOnceLocation(true);
        //设置为高精度定位模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否允许模拟位置,默认为true，允许模拟位置
        mLocationOption.setMockEnable(true);
        //设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
        // 在定位结束后，在合适的生命周期调用onDestroy()方法
        // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
        mlocationClient.startLocation();
    }

    /**
     * 显示进度框
     */
    private void showProgressDialog() {
        if (progDialog == null)
            progDialog = new ProgressDialog(this);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(true);
        progDialog.setMessage("正在搜索");
        progDialog.show();
    }

    /**
     * 隐藏进度框
     */
    private void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }

    /**
     * 主线程中执行
     *
     * @param msg
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMainEventBus(MessageEntity msg) {
        getLatlon(msg.getName(), msg.getAdcode());
    }


    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        deactivate();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if (null != mlocationClient) {
            mlocationClient.onDestroy();
        }
        wayList.clear();
        EventBus.getDefault().unregister(this);//注册
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null
                    && amapLocation.getErrorCode() == 0) {
                amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                amapLocation.getLatitude();//获取经度
                amapLocation.getLongitude();//获取纬度;
                amapLocation.getAccuracy();//获取精度信息
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(amapLocation.getTime());
                df.format(date);//定位时间 aMapLocation.getAddress();地址，如果option中设置isNeedAddress为false，则没有此结果 aMapLocation.getCountry();
                amapLocation.getCountry();//国家信息
                amapLocation.getProvince();//省信息
                amapLocation.getCountry();//国家信息
                amapLocation.getProvince();//省信息
                amapLocation.getRoad();//街道信息
                amapLocation.getCityCode();//城市编码
                city = amapLocation.getCity();//城市
                amapLocation.getAdCode();//地区编码
                //点击定位按钮 能够将地图的中心移动到定位点
                mListener.onLocationChanged(amapLocation);
                latlngA = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
                latLonPointA = new LatLonPoint(amapLocation.getLatitude(), amapLocation.getLongitude());
                if (num < 1) {
                    aMap.moveCamera(CameraUpdateFactory.newLatLng(latlngA));
                    aMap.moveCamera(CameraUpdateFactory.zoomTo(18));
                    num++;
                }

            } else {
                String errText = "定位失败," + amapLocation.getErrorCode() + ": " + amapLocation.getErrorInfo();
                Log.e("AmapErr", errText);
            }
        }
    }

    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        aMap.moveCamera(CameraUpdateFactory.zoomTo(18));
    }

    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    /**
     * 开始搜索路径规划方案
     */
    public void searchRouteResult(int routeType, int mode) {
        if (latLonPointA == null) {
            ToastUtil.show(mContext, "定位中，稍后再试...");
            return;
        }
        if (latLonPointB == null) {
            ToastUtil.show(mContext, "终点未设置");
        }
        showProgressDialog();
        final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
                latLonPointA, latLonPointB);
        if (routeType == ROUTE_TYPE_DRIVE) {// 驾车路径规划
            RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo, mode, null,
                    null, "");// 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
            mRouteSearch.calculateDriveRouteAsyn(query);// 异步路径规划驾车模式查询
        }
    }


    /**
     * 响应地理编码
     */
    public void getLatlon(final String name, String adcode) {
        showDialog();
        GeocodeQuery query = new GeocodeQuery(name, adcode);// 第一个参数表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode，
        geocoderSearch.getFromLocationNameAsyn(query);// 设置同步地理编码请求
    }

    /**
     * 显示进度条对话框
     */
    public void showDialog() {
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(true);
        progDialog.setMessage("正在获取地址");
        progDialog.show();
    }

    /**
     * 隐藏进度条对话框
     */
    public void dismissDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }

    //路线规划
    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

    }

    /**
     * 开始搜索路径规划方案
     *
     * @param result
     * @param errorCode
     */
    @Override
    public void onDriveRouteSearched(DriveRouteResult result, int errorCode) {
        dissmissProgressDialog();
//        aMap.clear();// 清理地图上的所有覆盖物
        if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getPaths() != null) {
                if (result.getPaths().size() > 0) {
                    mDriveRouteResult = result;
                    final DrivePath drivePath = mDriveRouteResult.getPaths()
                            .get(0);
                    drivingRouteOverlay = new DrivingRouteOverLay(
                            mContext, aMap, drivePath,
                            mDriveRouteResult.getStartPos(),
                            mDriveRouteResult.getTargetPos(), null);
                    drivingRouteOverlay.setNodeIconVisibility(false);//设置节点marker是否显示
                    drivingRouteOverlay.setIsColorfulline(true);//是否用颜色展示交通拥堵情况，默认true
                    drivingRouteOverlay.removeFromMap();
                    drivingRouteOverlay.addToMap();
                    drivingRouteOverlay.zoomToSpan();
                    int dis = (int) drivePath.getDistance();
                    int dur = (int) drivePath.getDuration();
                    String des = AMapUtil.getFriendlyTime(dur) + "(" + AMapUtil.getFriendlyLength(dis) + ")";
                    //计算范围
                    distance = AMapUtils.calculateLineDistance(latlngA, makerB.getPosition());
                    if (BigDecimalUtils.compareTo(distance + "", edt.getText().toString()) <= 0) {
                        VibratorUtil.Vibrate(mContext, 2000);
                        VibratorUtil.MediaPlayer(mContext);
                        ToastUtil.show(mContext, "进入范围提醒");
                    }

                    /**
                     * 方法: int strategy=mAMapNavi.strategyConvert(congestion, avoidhightspeed, cost, hightspeed, multipleroute); 参数:
                     *
                     * @congestion 躲避拥堵
                     * @avoidhightspeed 不走高速
                     * @cost 避免收费
                     * @hightspeed 高速优先
                     * @multipleroute 多路径
                     *
                     *  说明: 以上参数都是boolean类型，其中multipleroute参数表示是否多条路线，如果为true则此策略会算出多条路线。
                     *  注意: 不走高速与高速优先不能同时为true 高速优先与避免收费不能同时为true
                     */
                    int strategy = 0;
                    try {
                        //再次强调，最后一个参数为true时代表多路径，否则代表单路径
                        strategy = mAMapNavi.strategyConvert(true, false, false, false, false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    startLatlng.setLatitude(latlngA.latitude);
                    startLatlng.setLongitude(latlngA.longitude);
                    endLatlng.setLatitude(latlngB.latitude);
                    endLatlng.setLongitude(latlngB.longitude);
                    startList.add(startLatlng);
                    endList.add(endLatlng);
                    mAMapNavi.calculateDriveRoute(startList, endList, wayList, strategy);
                    mTvNavi.setVisibility(View.VISIBLE);
                } else if (result != null && result.getPaths() == null) {
                    ToastUtil.show(mContext, NO_RESULT);
                }

            } else {
                ToastUtil.show(mContext, NO_RESULT);
            }
        } else {
            ToastUtil.showerror(this.getApplicationContext(), errorCode);
        }
    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

    }

    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

    }

    //响应逆地理编码
    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
        dismissDialog();
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getRegeocodeAddress() != null
                    && result.getRegeocodeAddress().getFormatAddress() != null) {
                String address = result.getRegeocodeAddress().getFormatAddress();
                if (address.length() > 9) {
                    String a1 = "";
                    for (int i = 0; i <= address.length() / 9; i++) {
                        int z = (i + 1) * 9;
                        if (z > address.length()) {
                            z = address.length();
                        }
                        a1 += address.substring(i * 9, z) + "\n";
                    }
                    address = a1;
                }
                makerB = aMap.addMarker(new MarkerOptions().position(latlngB).title("点击此处可设置范围")
                        .snippet(address)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                        .draggable(true));
                makerB.showInfoWindow();// 设置默认显示一个infowinfow
                mTvNavi.setVisibility(View.GONE);
            } else {
                ToastUtil.show(mContext, NO_RESULT);
            }
        } else {
            ToastUtil.showerror(this, rCode);
        }
    }

    /**
     * 响应地理编码
     */
    @Override
    public void onGeocodeSearched(GeocodeResult result, int rCode) {
        dismissDialog();
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getGeocodeAddressList() != null
                    && result.getGeocodeAddressList().size() > 0) {
                GeocodeAddress geocodeAddress = result.getGeocodeAddressList().get(0);
                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        AMapUtil.convertToLatLng(geocodeAddress.getLatLonPoint()), 15));
                String address = geocodeAddress.getFormatAddress();
                if (address.length() > 9) {
                    String a1 = "";
                    for (int i = 0; i <= address.length() / 9; i++) {
                        int z = (i + 1) * 9;
                        if (z > address.length()) {
                            z = address.length();
                        }
                        a1 += address.substring(i * 9, z) + "\n";
                    }
                    address = a1;
                }
                if (makerB != null) {
                    makerB.remove();
                }
                latlngB = AMapUtil.convertToLatLng(geocodeAddress
                        .getLatLonPoint());
                latLonPointB = geocodeAddress.getLatLonPoint();
                makerB = aMap.addMarker(new MarkerOptions().position(AMapUtil.convertToLatLng(geocodeAddress
                        .getLatLonPoint())).title("点击此处可设置范围")
                        .snippet(address)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                        .draggable(true));
                makerB.showInfoWindow();// 设置默认显示一个infowinfow
                mTvNavi.setVisibility(View.GONE);
                String addressName = "经纬度值:" + geocodeAddress.getLatLonPoint() + "\n位置描述:"
                        + address;
                ToastUtil.show(mContext, addressName);
            } else {
                ToastUtil.show(mContext, "对不起，没有搜索到相关数据");
            }
        } else {
            ToastUtil.showerror(this, rCode);
        }
    }

    /**
     * 响应逆地理编码
     */
    public void getAddress(final LatLonPoint latLonPoint) {
        showDialog();
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,
                GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        geocoderSearch.getFromLocationAsyn(query);// 设置异步逆地理编码请求
    }


    @Override
    public void onMapLongClick(LatLng arg0) {
        if (makerB != null) {
            makerB.remove();
        }
        latlngB = arg0;
        latLonPointB = new LatLonPoint(arg0.latitude, arg0.longitude);
        getAddress(latLonPointB);
    }


    //坐标弹窗信息点击事件
    @Override
    public void onInfoWindowClick(final Marker marker) {
        edt = new EditText(mContext);
        edt.setInputType(InputType.TYPE_CLASS_NUMBER);
        Dialog permissionDialog = new AlertDialog.Builder(mContext)
                .setTitle("提示")
                .setCancelable(false)
                .setView(edt)
                .setMessage("请输入提醒的范围")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dismissDialog();
                    }
                })
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (TextUtils.isEmpty(edt.getText().toString())) {
                                    ToastUtil.show(mContext, "请输入提醒的范围");
                                    return;
                                }

                                if (circle != null) {
                                    circle.remove();
                                }
                                circle = aMap.addCircle(new CircleOptions().center(marker.getPosition())
                                        .radius(Integer.parseInt(edt.getText().toString())).strokeColor(STROKE_COLOR)
                                        .fillColor(FILL_COLOR).strokeWidth(5));
                                if (drivingRouteOverlay != null) {
                                    drivingRouteOverlay.removeFromMap();
                                }
                                searchRouteResult(ROUTE_TYPE_DRIVE, RouteSearch.DrivingDefault);


                            }
                        }).create();
        permissionDialog.show();

    }

    @OnClick({R.id.llyt_search, R.id.tv_navi})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.llyt_search://搜索
                if (TextUtils.isEmpty(city)) {
                    ToastUtil.show(mContext, "未定位");
                    return;
                }
                InputtipsActivity.lanugh(mContext, city);
                break;
            case R.id.tv_navi://开始导航
                Intent intent = new Intent(mContext, RouteNaviActivity.class);
                intent.putExtra("gps", true);
                startActivity(intent);
                break;
        }
    }

    /**
     * ************************************************** 在算路页面，以下接口全不需要处理，在以后的版本中我们会进行优化***********************************************************************************************
     **/

    @Override
    public void onInitNaviFailure() {

    }

    @Override
    public void onInitNaviSuccess() {

    }

    @Override
    public void onStartNavi(int i) {

    }

    @Override
    public void onTrafficStatusUpdate() {

    }

    @Override
    public void onLocationChange(AMapNaviLocation aMapNaviLocation) {

    }

    @Override
    public void onGetNavigationText(int i, String s) {

    }

    @Override
    public void onEndEmulatorNavi() {

    }

    @Override
    public void onArriveDestination() {

    }

    @Override
    public void onCalculateRouteFailure(int i) {

    }

    @Override
    public void onReCalculateRouteForYaw() {

    }

    @Override
    public void onReCalculateRouteForTrafficJam() {

    }

    @Override
    public void onArrivedWayPoint(int i) {

    }

    @Override
    public void onGpsOpenStatus(boolean b) {

    }

    @Override
    public void onNaviInfoUpdate(NaviInfo naviInfo) {

    }

    @Override
    public void onNaviInfoUpdated(AMapNaviInfo aMapNaviInfo) {

    }

    @Override
    public void updateCameraInfo(AMapNaviCameraInfo[] aMapNaviCameraInfos) {

    }

    @Override
    public void onServiceAreaUpdate(AMapServiceAreaInfo[] aMapServiceAreaInfos) {

    }

    @Override
    public void showCross(AMapNaviCross aMapNaviCross) {

    }

    @Override
    public void hideCross() {

    }

    @Override
    public void showLaneInfo(AMapLaneInfo[] aMapLaneInfos, byte[] bytes, byte[] bytes1) {

    }

    @Override
    public void hideLaneInfo() {

    }

    @Override
    public void onCalculateRouteSuccess(int[] ints) {

    }

    @Override
    public void notifyParallelRoad(int i) {

    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo aMapNaviTrafficFacilityInfo) {

    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[] aMapNaviTrafficFacilityInfos) {

    }

    @Override
    public void OnUpdateTrafficFacility(TrafficFacilityInfo trafficFacilityInfo) {

    }

    @Override
    public void updateAimlessModeStatistics(AimLessModeStat aimLessModeStat) {

    }

    @Override
    public void updateAimlessModeCongestionInfo(AimLessModeCongestionInfo aimLessModeCongestionInfo) {

    }

    @Override
    public void onPlayRing(int i) {

    }


}
