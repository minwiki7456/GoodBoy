package fun.yuuu.goodboy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";
    private final int mMsgCurrentTimeKey = 1;//设置用来用来作msg的what的参数，表示值是当前的时间
    private Thread mTimeThread = null;//获取时间的线程
    private StringBuffer mTimeStringBuffer = null;//时间字符串
    private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//时间格式
    private TextView mTextView = null;
    private Handler mTimeHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            switch (message.what){
                case mMsgCurrentTimeKey:
                    if(mTextView != null){
                        mTextView.setText(mTimeStringBuffer.toString());//更新时间文本
                    }
                    break;
                default:
                    Log.d(TAG,"mHandler's msg.what default!");
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = findViewById(R.id.currentTimeTextView);

        //更新时间线程
        mTimeThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                for(;;){
                    if(mTimeStringBuffer == null)
                        mTimeStringBuffer = new StringBuffer("");
                    else
                        mTimeStringBuffer.setLength(0);//清空mTimeStringBuffer的字符串
                    mTimeStringBuffer.append(mSimpleDateFormat.format(new Date()));//获取系统时间
                    Message msg = mTimeHandler.obtainMessage();
                    msg.what = mMsgCurrentTimeKey;
                    mTimeHandler.sendMessage(msg);//传递消息
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;//sleep检测到中断异常则结束
                    }
                }
                Looper.loop();
            }
        });
        mTimeThread.start();

        //获取Button对象，设置按钮点击时间监听器。
        Button btn = findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);//获取系统剪切板
                cm.setPrimaryClip(ClipData.newPlainText("currentTime",mTimeStringBuffer.toString()));//保存当前时间到剪切板
                Toast.makeText(MainActivity.this, "CurrentTime is saved to clip board", Toast.LENGTH_SHORT).show();//获取成功提示
                mTimeThread.interrupt();//设置时间线程中断，停止更新时间
            }
        });

    }
}