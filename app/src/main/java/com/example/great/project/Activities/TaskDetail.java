package com.example.great.project.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.great.project.Adapter.HorizontalListView;
import com.example.great.project.Database.TaskDB;
import com.example.great.project.Database.TaskInfoDB;
import com.example.great.project.Model.Task;
import com.example.great.project.Model.TaskInfo;
import com.example.great.project.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TaskDetail extends AppCompatActivity {

    //该act为任务详情act。根据用户id、课程id和任务名，从Database中读取详情。
    //页面包括DB中读取的信息填充相应文本框
    //提供编辑功能_TaskEdit
    //随后根据任务id从taskinfo表里读取该任务的相关信息，填充列表
    //该页面显示该任务所有共享者，从任务-user表中根据taskid，选择已经同意加入的用户进来
    //同时可以邀请用户加入。邀请时在任务_user表中新建条目，是否加入置否。
    //可以发布新的info，taskid在task_info表中更新内容

    TaskDB myTaskDB = new TaskDB(TaskDetail.this);
    TaskInfoDB myTaskInfoDB = new TaskInfoDB(TaskDetail.this);

    RelativeLayout headerLayout;
    TextView taskNameTextView;
    TextView briefTextView;
    TextView creatorTextView;
    HorizontalListView participantListView;
    RecyclerView taskInfoListView;
    EditText pusherEditor;
    Button sendBtn;
    CommonAdapter<TaskInfo> taskInfoAdapter;

    Task curr_task;
    int taskId = 0;
    int courseId = 0;
    String sName = "";

    void initial(){
        headerLayout = findViewById(R.id.taskDetail_header);
        taskNameTextView = findViewById(R.id.taskDetail_taskName);
        briefTextView = findViewById(R.id.taskDetail_brief);
        creatorTextView = findViewById(R.id.taskDetail_creator);
        participantListView = findViewById(R.id.taskDetail_participants);
        taskInfoListView = findViewById(R.id.taskDetail_taskInfoList);
        pusherEditor = findViewById(R.id.taskDetail_editor);
        sendBtn = findViewById(R.id.taskDetail_sendBtn);

        taskNameTextView.setText(curr_task.getTaskName());
        briefTextView.setText(curr_task.getTaskBrief());
        creatorTextView.setText(curr_task.getCreatorName());

        //参与者列表
        List<String> participantNameList = myTaskDB.searchParticipantsByTaskID(taskId);
        SimpleAdapter participantSimpleAdaptor = new SimpleAdapter(this, turnStringsIntoList(participantNameList),
                R.layout.task_detail_participants_listitem,new String[]{"name"}, new int[]{R.id.taskDetail_participants_name});
        participantListView.setAdapter(participantSimpleAdaptor);

        //任务信息列表
        taskInfoAdapter = new CommonAdapter<TaskInfo>(this, R.layout.task_info_item_layout, myTaskInfoDB.queryByTask(taskId)){
            @Override
            public void convert(ViewHolder viewHolder, TaskInfo taskInfo) {
                TextView content = viewHolder.getView(R.id.taskDetail_taskInfo_content);
                content.setText(taskInfo.getContent());
                TextView pusher = viewHolder.getView(R.id.taskDetail_taskInfo_pusher);
                pusher.setText(taskInfo.getPusherId());
            }
        };
        taskInfoListView.setAdapter(taskInfoAdapter);
        taskInfoListView.setLayoutManager(new LinearLayoutManager(this));
        if(taskInfoAdapter.getItemCount() > 0){
            taskInfoListView.smoothScrollToPosition(taskInfoAdapter.getItemCount()-1);
        }
    }

    void setListeners(){
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputStr = pusherEditor.getText().toString();
                if(!inputStr.equals("")){
                    int newId = myTaskInfoDB.addTaskInfo(new TaskInfo(1, taskId, sName, inputStr));
                    TaskInfo temp = new TaskInfo(newId, taskId, sName, inputStr);
                    taskInfoAdapter.addItem(taskInfoAdapter.getItemCount(), temp);
                    pusherEditor.setText("");
                    taskInfoListView.smoothScrollToPosition(taskInfoAdapter.getItemCount()-1);
                }
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            taskId = extras.getInt("taskId");
            courseId = extras.getInt("courseId");
            sName = extras.getString("sName");
        }
        curr_task = myTaskDB.searchByTaskID(taskId);

        initial();
        setListeners();
    }

    ArrayList<Map<String, Object>> turnStringsIntoList(List<String> raw_list){
        ArrayList<Map<String, Object>> res = new ArrayList<>();
        for(int i  = 0; i < raw_list.size(); i++){
            HashMap<String, Object> temp = new HashMap<>();
            temp.put("name", raw_list.get(i));
            res.add(temp);
        }
        return res;
    }
}
