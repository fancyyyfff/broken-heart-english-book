package com.fancy.exam_words1;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private WordDAO wordDAO;
    private String english;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wordDAO = new WordDAO(this);
        wordDAO.open();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        wordDAO.close();
    }

    //    跳转到收藏夹界面
    public void clickStarBtn(View view) {
        Intent intent = new Intent(MainActivity.this, StarActivity.class);
        startActivity(intent); // 直接启动 StarActivity
    }

    //   ： 获取输入的英文，发送请求，接收响应，显示中文到对应的控件
    public void clickSearch(View view) {
        // 获取输入的英文单词
        EditText inputEnglish = findViewById(R.id.englishId); // 假设你有一个 EditText 输入框
        english= inputEnglish.getText().toString().trim();

        if (!english.isEmpty()) {
            TextView translationTextView = findViewById(R.id.chineseId);
            translationTextView.setText("");
            // 发送请求并获取结果
            new FetchTranslationTask().execute(english);
        } else {
            Toast.makeText(this, "请输入单词", Toast.LENGTH_SHORT).show();
        }
    }

    //    处理发送请求和获取响应数据：
    class FetchTranslationTask extends AsyncTask<String, Void, String> {

        //        处理请求数据
        @Override
        protected String doInBackground(String... params) {
            String word = params[0];
            String urlString = "https://dict.youdao.com/suggest?q=" + word + "&num=1&doctype=json";
            StringBuilder result = new StringBuilder();

            try {
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setConnectTimeout(5000);
                urlConnection.setReadTimeout(5000);

                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

            return result.toString();
        }

        //        处理返回数据：
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result != null) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    // 获取 "result" 对象
                    JSONObject resultObject = jsonObject.getJSONObject("result");

                    int code = resultObject.getInt("code");
                    if (code != 200) {
                        return;
                    }

                    JSONObject dataObject = jsonObject.getJSONObject("data");
                    JSONArray entriesArray = dataObject.getJSONArray("entries");
                    String explain = entriesArray.getJSONObject(0).getString("explain");
                    // 显示前两个中文翻译到 UI 控件
                    TextView translationTextView = findViewById(R.id.chineseId);
                    translationTextView.setText(explain);
//                    只存第一个分号前的内容
                    String chinese="";
                    int semicolonIndex = explain.indexOf('；');
                    if (semicolonIndex != -1) {
                        chinese = explain.substring(0, semicolonIndex);
                    }
                    if(english.isEmpty()) {
                        return;
                    }
                    //判断查询到的单词在数据库中是否已经存在，若存在，结束
                    if( wordDAO.isWordExist(english)) {
                        return;
                    }else{
                        Log.i("chinese", "onPostExecute: "+chinese);
                        wordDAO.insertWord(english,chinese,1);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "解析错误", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "请求失败", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
