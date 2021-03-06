package com.mcds.app.android.estar.ui.news;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.mcds.app.android.estar.R;
import com.mcds.app.android.estar.adapter.BaseListAdapter;
import com.mcds.app.android.estar.bean.HeartHomeList;
import com.mcds.app.android.estar.bean.ReturnResult;
import com.mcds.app.android.estar.manager.UserManager;
import com.mcds.app.android.estar.provider.AsyncBitmapLoader;
import com.mcds.app.android.estar.provider.ConnectProviderZX;
import com.mcds.app.android.estar.provider.AsyncBitmapLoader.ImageCallBack;
import com.mcds.app.android.estar.ui.BaseTabActivity;
import com.mcds.app.android.estar.util.LayoutUtility;
import com.mcds.app.android.estar.util.Utility;

public class HeartConstellation extends BaseTabActivity {

	private ListView consListView;
	private ConsAdapter adapter;
	private ReturnResult<HeartHomeList> rrCons;
	private ImageView newsListImgBig;
	private TextView newsListTitleBig;
	private ScrollView scrollView;

	private Runnable scrolltorun = new Runnable() {

		@Override
		public void run() {
			scrollView.scrollTo(0, 0);// 改变滚动条的位置
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.news_heart_constellation);
		initTabNavigate(R.id.newsCons_tabNavigate, TAB_INDEX_NEWS);
		init();
		getData();
	}

	/**
	 * 获取活动列表
	 */
	private void getData() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				showWaitingDialog();

				if (!UserManager.uid.equals("")) {
					rrCons = ConnectProviderZX.getHeartHomeList("0");
					doListViewUI();
				}
				hideWaitingDialog();
			}
		}).start();
	}

	/**
	 * 更新界面
	 */
	private void doListViewUI() {
		this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (rrCons.status.equals("0")) {
					adapter.setItems(rrCons.getDatas());
					LayoutUtility
							.setListViewHeightBasedOnChildren(consListView);
					Handler handler = new Handler();
					handler.postDelayed(scrolltorun, 200); 
					adapter.notifyDataSetChanged();
				}
			}
		});
	}

	private void init() {
		// TODO Auto-generated method stub
		scrollView = (ScrollView)findViewById(R.id.news_cons_big_layout);
		consListView = (ListView) findViewById(R.id.newsCons_listView);
		newsListImgBig = (ImageView) findViewById(R.id.news_cons_big_imgs);
		newsListTitleBig = (TextView) findViewById(R.id.news_cons_big_title);
		adapter = new ConsAdapter();
		consListView.setAdapter(adapter);

		findViewById(R.id.newsCons_back_button).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO 点击返回
						onBackPressed();
					}

				});
		consListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO 进入详情
				Intent intent = new Intent(
						HeartConstellation.this,
						com.mcds.app.android.estar.ui.news.NewsHeartDetailConsActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("eap_detail", rrCons.getDatas()
						.get(arg2));
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
	}

	private class ConsAdapter extends BaseListAdapter<HeartHomeList> {
		private AsyncBitmapLoader asyncLoader = new AsyncBitmapLoader();

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(getApplicationContext())
						.inflate(R.layout.news_heart_constellation_item, null);
				holder = new ViewHolder();
				holder.consItemImage = (ImageView) convertView
						.findViewById(R.id.news_conslist_item_img);
				holder.consItemTitle = (TextView) convertView
						.findViewById(R.id.news_conslist_item_title);
				holder.consItemContent = (TextView) convertView
						.findViewById(R.id.news_conslist_item_content);
				holder.consItemAppr = (TextView) convertView
						.findViewById(R.id.news_conslist_item_appraise);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			if (holder != null) {
				HeartHomeList item = this.getItem(position);
				if (position == 0) {
					newsListTitleBig.setText(rrCons.getDatas().get(0).title);
					DisplayMetrics metrics = new DisplayMetrics();
					getWindowManager().getDefaultDisplay().getMetrics(metrics);
					int width = (metrics.widthPixels * 720) / 720;
					int height = (metrics.widthPixels * 396) / 720;
					Bitmap bmpbig = asyncLoader.loadBitmap(newsListImgBig,
							item.image_url, width, height, new ImageCallBack() {

								@Override
								public void imageLoad(ImageView imageView,
										Bitmap bitmap) {
									if (bitmap == null) {
										bitmap = BitmapFactory
												.decodeResource(
														getResources(),
														R.drawable.list_item_ic_default);
									}
									imageView.setImageBitmap(bitmap);
								}
							});

					if (bmpbig != null) {
						newsListImgBig.setImageBitmap(bmpbig);
					}
				}

				holder.consItemTitle.setText(item.title);
				holder.consItemContent.setText(item.content);
				holder.consItemAppr.setText(item.praise);

				Bitmap bmp = asyncLoader.loadBitmap(holder.consItemImage,
						item.image_url,
						Utility.dip2px(HeartConstellation.this, 100),
						Utility.dip2px(HeartConstellation.this, 100),
						new ImageCallBack() {

							@Override
							public void imageLoad(ImageView imageView,
									Bitmap bitmap) {
								if (bitmap == null) {
									bitmap = BitmapFactory.decodeResource(
											getResources(),
											R.drawable.list_item_ic_default);
								}
								imageView.setImageBitmap(bitmap);
							}
						});

				if (bmp != null) {
					holder.consItemImage.setImageBitmap(bmp);
				}
			}
			return convertView;
		}
	}

	private class ViewHolder {
		ImageView consItemImage;
		TextView consItemTitle;
		TextView consItemContent;
		TextView consItemAppr;
	}

}
