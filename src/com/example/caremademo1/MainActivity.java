package com.example.caremademo1;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener{

	private ImageView imgShow;
	private Button btnPhoto, btnVideo, btnSound;
	private File currentImageFile = null;
	private File currentVideoFile = null;
	private File currentSoundFile = null;
	private Context mContext;

	private boolean isStart = false;
	private MediaRecorder mr = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mContext = this;
		bindViews();
	}

	private void bindViews() {
		imgShow = (ImageView) findViewById(R.id.img_show);
		btnPhoto = (Button) findViewById(R.id.btn_start_photo);
		btnVideo = (Button) findViewById(R.id.btn_start_video);
		btnSound = (Button) findViewById(R.id.btn_start_sound);

		btnPhoto.setOnClickListener(this);
		btnVideo.setOnClickListener(this);
		btnSound.setOnClickListener(this);

	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1001) {

			Log.e("wzl", "currentImageFile-->" + currentImageFile.getAbsolutePath());
			Bitmap bt = BitmapHelper.getimage(currentImageFile.getAbsolutePath());
			imgShow.setImageBitmap(bt); 

		}
		if(requestCode == 1002){
			Toast.makeText(mContext, currentVideoFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.btn_start_photo:
			File dir = new File(Environment.getExternalStorageDirectory(),"pictures");
			if(dir.exists()){
				dir.mkdirs();
			}
			currentImageFile = new File(dir,System.currentTimeMillis() + ".jpg");
			if(!currentImageFile.exists()){
				try {
					currentImageFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			it.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(currentImageFile));
			it.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
			startActivityForResult(it, 1001);
			break;
		case R.id.btn_start_video:
			File file = new File(Environment.getExternalStorageDirectory(),"pictures");
			if(file.exists()){
				file.mkdirs();
			}
			// 用系统当前时间命名
			String tempName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".mp4";
			// 创建文件夹
			currentVideoFile = new File(file, tempName);
			if(!currentVideoFile.exists()){
				try {
					currentVideoFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
			intent.addCategory("android.intent.category.DEFAULT");
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(currentVideoFile));
			intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
			startActivityForResult(intent, 1002);
			break;
		case R.id.btn_start_sound:
			if(!isStart){
				startRecord();
				btnSound.setText("停止录音");
				isStart = true;
			}else{
				stopRecord();
				btnSound.setText("开始录音");
				Toast.makeText(mContext, currentSoundFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
				isStart = false;
			}
			break;
		default:
			break;
		}
	}

	private void startRecord(){
		if(mr == null){
			File dir = new File(Environment.getExternalStorageDirectory(),"sounds");
			if(!dir.exists()){
				dir.mkdirs();
			}
			currentSoundFile = new File(dir,System.currentTimeMillis()+".amr");
			if(!currentSoundFile.exists()){
				try {
					currentSoundFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
			mr = new MediaRecorder();
			mr.setAudioSource(MediaRecorder.AudioSource.MIC);
			mr.setOutputFormat(MediaRecorder.OutputFormat.AMR_WB);
			mr.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);
			mr.setOutputFile(currentSoundFile.getAbsolutePath());
			try {
				mr.prepare();
				mr.start();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	private void stopRecord(){
		if(mr != null){
			mr.stop();
			mr.release();
			mr = null;
		}
	}

}
