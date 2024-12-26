package com.fancy.exam_words1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class StarActivity extends AppCompatActivity {
    private MyBaseAdapter myAdapter; // 提升为成员变量
    private WordDAO wordDAO;
    private List<Words> wordsList;
//    private String[] englishList = {"a","b","c","d","e","f","g","h","i"};
//    private String[] chineseList = {"路飞","索隆","娜美","香吉士","乌索普","乔巴","罗宾","佛朗基","布鲁克"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_star);
        ListView mListView = findViewById(R.id.lv);
        wordDAO = new WordDAO(this);
        wordDAO.open();

        // 获取所有单词
        wordsList = wordDAO.getAllWords();
        myAdapter = new MyBaseAdapter(wordsList);
        mListView.setAdapter(myAdapter);

        // 筛选栏的 RadioGroup
        RadioGroup filterGroup = findViewById(R.id.star_top).findViewById(R.id.typeRadioGroup);
        filterGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.typeAll:
                        wordsList = wordDAO.getAllWords(); // 获取所有单词
                        break;
                    case R.id.typeFirst:
                        wordsList = wordDAO.getWordsByType(TypeConst.FIRST); // 完全陌生
                        break;
                    case R.id.typeKnown:
                        wordsList = wordDAO.getWordsByType(TypeConst.KOWN); // 似曾相识
                        break;
                    case R.id.typeMaster:
                        wordsList = wordDAO.getWordsByType(TypeConst.MASTER); // 熟悉
                        break;
                    default:
                        wordsList = wordDAO.getAllWords(); // 默认显示所有单词
                        break;
                }
                // 更新适配器数据
                myAdapter.updateData(wordsList);
            }
        });

        Log.i("111", "创建完成");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        wordDAO.close();
    }

    class MyBaseAdapter extends BaseAdapter {
        private List<Words> wordsList; // 声明 wordsList

        // 构造函数，传入 wordsList
        public MyBaseAdapter(List<Words> wordsList) {
            this.wordsList = wordsList;
        }

        @Override
        public int getCount() {
            return wordsList.size(); // 获取 wordsList 的大小
        }

        @Override
        public Object getItem(int position) {
            return wordsList.get(position); // 获取 wordsList 中的单词对象
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void updateData(List<Words> newWordsList) {
            this.wordsList = newWordsList; // 更新数据
            notifyDataSetChanged(); // 刷新视图
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final int currentPosition = position; // 捕获 position 的值
            ViewHolder holder; // 使用 ViewHolder 提高性能
            if (convertView == null) {
                // 将 list_item.xml 转化为 View 对象
                convertView = View.inflate(StarActivity.this, R.layout.list_item, null);
                Log.i("222", convertView + "");

                // 初始化 ViewHolder
                holder = new ViewHolder();
                holder.english = convertView.findViewById(R.id.list_englishId);
                holder.chinese = convertView.findViewById(R.id.list_chineseId);
                holder.radioGroup = convertView.findViewById(R.id.radioGroupId);
                holder.radioButton1 = convertView.findViewById(R.id.radio_button1);
                holder.radioButton2 = convertView.findViewById(R.id.radio_button2);
                holder.radioButton3 = convertView.findViewById(R.id.radio_button3);
                holder.line = convertView.findViewById(R.id.lineId);
                holder.deleteTextView = convertView.findViewById(R.id.deleteId); // 获取删除按钮

                convertView.setTag(holder); // 缓存 ViewHolder
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            // 获取当前单词
            final Words word = wordsList.get(position);  // 获取当前单词

            // 设置文本内容
            holder.english.setText(word.getEnglish());  // 从 wordsList 获取 english
            holder.chinese.setText(word.getChinese());  // 从 wordsList 获取 chinese

            // 设置 RadioGroup 的逻辑
            holder.radioGroup.setOnCheckedChangeListener(null); // 清除之前的监听器
            holder.radioGroup.clearCheck(); // 避免视图重用问题

            // 根据 type 设置选中的单选框
            switch (word.getType()) {
                case TypeConst.FIRST:
                    holder.radioButton1.setChecked(true);  // 完全陌生
                    break;
                case TypeConst.KOWN:
                    holder.radioButton2.setChecked(true);  // 似曾相识
                    break;
                case TypeConst.MASTER:
                    holder.radioButton3.setChecked(true);  // 熟悉
                    break;
                default:
                    holder.radioGroup.clearCheck();  // 默认情况下没有选中
                    break;
            }

            holder.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    switch (checkedId) {
                        case R.id.radio_button1:
//                            word.setType(1);  // 更新单词的熟悉度
//                           更新数据库的单词熟悉度
                            wordDAO.updateWordType(word.getId(), TypeConst.FIRST);
                            break;
                        case R.id.radio_button2:
                            wordDAO.updateWordType(word.getId(),TypeConst.KOWN);
                            break;
                        case R.id.radio_button3:
                            wordDAO.updateWordType(word.getId(),TypeConst.MASTER);
                            break;
                    }
                }
            });

            // 删除按钮点击事件
            holder.deleteTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 从数据库中删除单词
                    int deletedRows = wordDAO.deleteWordById(word.getId());
                    if (deletedRows > 0) {
                        // 从列表中删除单词
                        wordsList.remove(currentPosition);
                        // 刷新列表视图
                        notifyDataSetChanged();
                        Toast.makeText(StarActivity.this, "单词已删除", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(StarActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                    }
                }
            });


            return convertView;
        }
    }

        // ViewHolder 类，用于缓存控件引用
    class ViewHolder {
        TextView english;
        TextView chinese;
        RadioGroup radioGroup;
        RadioButton radioButton1;
        RadioButton radioButton2;
        RadioButton radioButton3;
        TextView deleteTextView;
        View line;
    }

}
