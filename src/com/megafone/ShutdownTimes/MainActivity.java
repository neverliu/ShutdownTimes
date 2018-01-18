package com.megafone.ShutdownTimes;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.text.SpannableString;
import android.text.SpannedString;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.view.KeyEvent;

public class MainActivity extends Activity{
	private static final String TAG = "MainActivity";
	private Context mContext;
	private PowerManager pManager;
	Handler mRebootHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 0) {
				pManager.reboot(null);
			} else if (msg.what > 0) {
				timer = new CountDownTimer(msg.what * 1000, 1 * 1000) {
					@Override
					public void onTick(long millisUntilFinished) {
						rebootText.setText((millisUntilFinished / 1000)
								+ getString(R.string.timer_text_1));
					}

					@Override
					public void onFinish() {
						rebootText.setEnabled(true);
						rebootText.setText(R.string.timer_text_2);
						putData(REBOOTTIMES,getData(REBOOTTIMES) + 1);
						pManager.reboot(null);
					}
				};
				timer.start();
			}
		};
	};
	private CountDownTimer timer;
	private Button reboot;
	private Button autoReboot;
	private Button stop;
	private EditText rebootTime;
	private TextView rebootText;
	private TextView rebootTimes;
	private List<String> list = null;
	private SharedPrefsUtil spu;
	private boolean mEditFocus;
	private static final String REBOOTTIMES = "rebootTimes";


	private static final String  COUNTDOWNTIME= "countdowntime";
	// private void NewCountDownTimer(int ){
	//
	// }
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mContext = this;
		initControl();
		CheckTimes();
	}

	// @Override
	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// 	if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
	// 		moveTaskToBack(true);
	// 	}
	// 	return super.onKeyDown(keyCode, event);
	// }
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (timer != null) {
			timer.cancel();
		}
	}

	@Override
	public void finish() {
		moveTaskToBack(true);
	}

	private void CheckTimes() {
		if (timer == null) {
			stop.setVisibility(View.VISIBLE);
			autoReboot.setVisibility(View.GONE);
			stop.requestFocus();
			Message obtain = Message.obtain();
			if(getData(COUNTDOWNTIME) == 0){
				obtain.what = 30;
			}else{
				obtain.what = getData(COUNTDOWNTIME);	
			}
			
			SpannableString ss = new SpannableString(obtain.what + "");
			rebootTime.setHint(new SpannedString(ss));
			rebootTime.setEnabled(false);
			rebootTime.setText("");
			mRebootHandler.sendMessage(obtain);
		}
	}

	private int getData(String key) {
		int value = SharedPrefsUtil.getValue(mContext, key, 0);
		return value;
	}

	private void putData(String key, int value) {
		SharedPrefsUtil.putValue(mContext, key, value);
	}

	private void initControl() {
		pManager = (PowerManager) mContext
				.getSystemService(Context.POWER_SERVICE);
		reboot = (Button) findViewById(R.id.reboot);
		reboot.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				rebootOnclick();
			}
		});
		autoReboot = (Button) findViewById(R.id.bt_auto_reboot);
		autoReboot.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AutoRebootOnclick();
			}
		});
		rebootTime = (EditText) findViewById(R.id.reboot_time);
		rebootTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                mEditFocus = hasFocus;
            }
        });
		rebootText = (TextView) findViewById(R.id.reboot_time_text);
		rebootTimes = (TextView) findViewById(R.id.reboot_times);
		stop = (Button) findViewById(R.id.stop);
		stop.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				stopOnclick();
			}
		});
		rebootTimes.setText(getString(R.string.reboot_times_text) + getData(REBOOTTIMES));
	}


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        
        switch (keyCode){
            case KeyEvent.KEYCODE_BACK:
                if(mEditFocus){
                    if(rebootTime.getText() != null){
                        if(rebootTime.getText().toString().length() > 0){
                            int select = rebootTime.getSelectionStart();
                            rebootTime.getText().delete(select-1 , select);
                            return true;
                        }
                    }
                }
                break;

        }
        return super.onKeyDown(keyCode, event);
    }

	private void stopOnclick(){
		if (timer != null) {
			timer.cancel();
			autoReboot.setVisibility(View.VISIBLE);
			stop.setVisibility(View.GONE);
			autoReboot.requestFocus();
			rebootText.setText("");
			rebootTime.setEnabled(true);
			SpannableString ss = new SpannableString("");
			rebootTime.setHint(new SpannedString(ss));
			int num = 30;
			if(getData(COUNTDOWNTIME) == 0){
				num = 30;
			}else{
				num = getData(COUNTDOWNTIME);	
			}
			rebootTime.setText(num+"");
			rebootTime.setSelection(rebootTime.getText().length());
		}
	}
	private void rebootOnclick() {
		new AlertDialog.Builder(mContext)
				.setMessage(R.string.power_off)
				.setPositiveButton(getString(R.string.ok),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								SharedPrefsUtil.putValue(mContext, REBOOTTIMES,
										0);
								rebootTimes
										.setText(getString(R.string.reboot_times_text)
												+ getData(REBOOTTIMES));
								// Message obtain = Message.obtain();
								// obtain.what = 0;
								// mRebootHandler.sendMessage(obtain);
							}
						}).setNegativeButton(getString(R.string.cancel), null)
				.show();
	}

	private void AutoRebootOnclick() {
		Message obtain = Message.obtain();
		if (null != rebootTime.getText() && !rebootTime.getText().equals("")
				&& !TextUtils.isEmpty(rebootTime.getText())) {
			String text = rebootTime.getText().toString().trim();
			int time = Integer.parseInt(text);
			putData(COUNTDOWNTIME, time);
			SpannableString ss = new SpannableString("" + text);
			stop.setVisibility(View.VISIBLE);
			autoReboot.setVisibility(View.GONE);
			stop.requestFocus();
			rebootTime.setHint(new SpannedString(ss));
			rebootTime.setText("");
			rebootTime.setEnabled(false);
			if (time > 0) {
				obtain.what = time;
				mRebootHandler.sendMessage(obtain);
				Toast.makeText(mContext,
						getString(R.string.toast_notification) + time + "s",
						Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(mContext, getString(R.string.Toast_Edit_isEmpt),
					Toast.LENGTH_SHORT).show();
		}
	}
}
