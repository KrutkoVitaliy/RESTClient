package appcorp.mmb.activities.other;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.VideoView;

import appcorp.mmb.R;
import appcorp.mmb.classes.FireAnal;
import appcorp.mmb.classes.Storage;

public class FullscreenVideoPreview extends Activity {

    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fullscreen_video_preview);
        videoView = (VideoView) findViewById(R.id.fullscreenVideo);
        Storage.init(getApplicationContext());
        initFirebase();

        FireAnal.sendString("Fullscreen video preview", "Open", "Activity");

        videoView.setMinimumWidth(Storage.getInt("Width", 480));
        videoView.setMinimumHeight(Storage.getInt("Width", 480));
        videoView.setLayoutParams(new ViewGroup.LayoutParams(Storage.getInt("Width", 480), Storage.getInt("Width", 480)));
        videoView.setVideoURI(Uri.parse("http://195.88.209.17/storage/videos/" + getIntent().getStringExtra("Source")));
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoView.start();
            }
        });
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                videoView.start();
            }
        });
    }

    private void initFirebase() {
        FireAnal.setContext(getApplicationContext());
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}