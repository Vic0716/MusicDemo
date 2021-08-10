package tw.com.musicdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button btnPlay,btnPause,backgroundMusic,networkMusic,nextPage;
    private SoundPool spool;
    private  int sourceid;
    MainActivity context = this;
    Sound sound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
    }

    private void findViews(){
        btnPlay = findViewById(R.id.btnPlay);
        btnPause = findViewById(R.id.btnPause);
        backgroundMusic = findViewById(R.id.backgroundMusic);
        networkMusic = findViewById(R.id.networkMusic);
        nextPage = findViewById(R.id.nextPage);

        //版本確認  Android5.0
        if(Build.VERSION.SDK_INT >= 21) {

            SoundPool.Builder builder = new SoundPool.Builder();
            builder.setMaxStreams(3);
            //封裝音頻屬性
            AudioAttributes.Builder attr = new AudioAttributes.Builder();
            attr.setLegacyStreamType(AudioManager.STREAM_MUSIC);

            builder.setAudioAttributes(attr.build());

            spool = builder.build();

        }
        else {

            spool = new SoundPool(8,AudioManager.STREAM_MUSIC,5);


        }

        sourceid = spool.load(this,R.raw.music,1);

        btnPlay.setOnClickListener(v -> {
            playSound(0);

        });

        btnPause.setOnClickListener(v -> {
            spool.pause(sourceid);

        });

        networkMusic.setOnClickListener(v -> {
            playUrlMusic();
        });

        backgroundMusic.setOnClickListener(v -> {
            sound = new Sound(context);
            sound.playSound(R.raw.music);
            sound.setMusicStatus(true);

        });

        nextPage.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this,MainActivity2.class);
            startActivity(intent);
        });



    }

    protected void onDestroy(){
        super.onDestroy();
        if (sound != null){
            sound.setMusicStatus(false);
            sound.release();


        }
    }

    private void playSound(int playtime) {

        //播放音樂

        AudioManager am = (AudioManager)getApplicationContext().getSystemService(Context.AUDIO_SERVICE);

        //抓取最大音量
        float maxVolumn = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        //抓取目前的音量
        float nowVolumn = am.getStreamVolume(AudioManager.STREAM_MUSIC);

        //左右聲道的範圍是在0.0~1.0 之間
        float vol = nowVolumn / maxVolumn;
        //來源，左聲道，右聲道，優先權，播放次數，速度
        spool.play(sourceid,vol,vol,1,playtime,1);

    }

    private void playUrlMusic() {

        MediaPlayer player = new MediaPlayer();
        try{

            player.setDataSource("https://www.all-birds.com/Sound/Boriolems-2.wav");
            player.prepareAsync();
            player.setOnPreparedListener(playLis);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    MediaPlayer.OnPreparedListener playLis = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
           mp.start();//檔案已經準備好後，開始播放

        }
    };
}