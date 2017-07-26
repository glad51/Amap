package com.zz.demo.amap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.amap.api.services.core.AMapException;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.Inputtips.InputtipsListener;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.zz.demo.amap.bean.MessageEntity;
import com.zz.demo.amap.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InputtipsActivity extends Activity implements TextWatcher, InputtipsListener {

	private static String CITY = "city";
	private EditText mKeywordText;
	private ListView minputlist;
	private InputtipsActivity mContext;
	private ArrayList<HashMap<String, String>> listString;
	private List<Tip> mTipList;
	private String city;

	public static void lanugh(Context mContext,String city){
		Intent intent=new Intent(mContext,InputtipsActivity.class);
		intent.putExtra(CITY,city);
		mContext.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inputtip);
		mContext = InputtipsActivity.this;
		city=getIntent().getStringExtra(CITY);
		minputlist = (ListView)findViewById(R.id.inputlist);
		mKeywordText = (EditText)findViewById(R.id.input_edittext);
        mKeywordText.addTextChangedListener(this);
		//列表点击
		minputlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				EventBus.getDefault().post(new MessageEntity(mTipList.get(i).getName(),
						mTipList.get(i).getAdcode()));
				mContext.finish();
			}
		});
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		String newText = s.toString().trim();
        InputtipsQuery inputquery = new InputtipsQuery(newText, city);
        inputquery.setCityLimit(true);
        Inputtips inputTips = new Inputtips(InputtipsActivity.this, inputquery);
        inputTips.setInputtipsListener(this);
        inputTips.requestInputtipsAsyn();
        
		
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetInputtips(final List<Tip> tipList, int rCode) {
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
			mTipList=tipList;
             listString = new ArrayList<HashMap<String, String>>();
            for (int i = 0; i < tipList.size(); i++) {
            	HashMap<String, String> map = new HashMap<String, String>();
            	map.put("name", tipList.get(i).getName());
            	map.put("address", tipList.get(i).getDistrict());
                listString.add(map);
            }
			SimpleAdapter aAdapter = new SimpleAdapter(getApplicationContext(), listString, R.layout.item_layout,
					new String[]{"name", "address"}, new int[]{R.id.poi_field_id, R.id.poi_value_id});

            minputlist.setAdapter(aAdapter);
            aAdapter.notifyDataSetChanged();

        } else {
//			ToastUtil.showerror(this.getApplicationContext(), rCode);
		}
		
	}

}
