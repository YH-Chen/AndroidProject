package com.example.great.project.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.great.project.Database.CourseDB;
import com.example.great.project.Database.TaskDB;
import com.example.great.project.Model.CourseModel;
import com.example.great.project.Model.Task;
import com.example.great.project.R;
import com.example.great.project.View.TitleBar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LessonDetail extends AppCompatActivity {


    //课程详细信息。根据main传来的intent中课程名称去数据库搜索相关信息，填充页面。
    //初步计划信息：课程名，课程时间，课程地点，老师名称，以及一个课程任务列表
    //课程要有一个修改按钮，跳转到LessonEdit进行编辑。可以不提供删除，因为加了之后act的跳转比较难。
    //页面排版很重要，记得做美观一点。
    //记得每次修改后要及时更新信息，包括数据库和界面上的内容
    //记得设置返回按键之类的东西
    //单击课程表里的任务表，跳转到任务界面
    //课程下可以新增任务，类似于从主界面新增课程。TaskEdit类为新增任务

    private SimpleDateFormat DTF = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
    private CourseDB cdb = new CourseDB(this);
    private TaskDB tdb = new TaskDB(this);

    private TextView courseName;
    private TextView courseRoom;
    private TextView courseStratTime;
    private TextView courseEndTime;
    private TextView courseTeacher;
    private TextView courseDate;
    private Button changeBtn;
    private Button deleteBtn;
    private Button newTaskBtn;
    private TitleBar titleBar;
    private RecyclerView TaskRec;

    private String sname;
    private List<Map<String, Object>> taskItem = new ArrayList<>();
    private CommonAdapter<Task> taskAdp;
    private CourseModel course;

    private void initial(){
        courseName = findViewById(R.id.course_detail_name);
        courseRoom = findViewById(R.id.course_detail_room);
        courseStratTime = findViewById(R.id.course_detail_start_time);
        courseEndTime = findViewById(R.id.course_detail_end_time);
        courseTeacher = findViewById(R.id.course_detail_teacher);
        courseDate = findViewById(R.id.course_detail_weekday);
        titleBar = findViewById(R.id.course_titlebar);
        changeBtn = findViewById(R.id.course_detail_change);
        deleteBtn = findViewById(R.id.course_detail_delete);
        newTaskBtn = findViewById(R.id.course_detail_new_task_btn);
        TaskRec = findViewById(R.id.course_detail_taskrec);

        Intent intent = getIntent();
        sname = intent.getExtras().getString("sname");
        course = (CourseModel) intent.getSerializableExtra("course");

        titleBar.setLeftText("返回");
        titleBar.setTitle("课程详情及任务");
        titleBar.setLeftImageResource(R.drawable.ic_left_black);

        courseName.setText(course.getCourseName());
        courseRoom.setText(course.getRoom());
        courseStratTime.setText(course.getStartTime());
        courseEndTime.setText(course.getEndTime());
        courseTeacher.setText(course.getTeacherName());
        courseDate.setText(course.getWeekDay());

        //任务列表初始化
        /*taskAdp = new CommonAdapter<Task>(this, R.layout.course_detail_task_items, tdb.searchByParticipantName(sname)) {
            @Override
            public void convert(ViewHolder viewHolder, Task task) {
                TextView name = findViewById(R.id.course_detail_task_name);
                name.setText(task.getTaskName());
                TextView ddl = findViewById(R.id.course_detail_task_ddl);
                ddl.setText(DTF.format(task.getTaskDDL()));
            }
        };*/
    }

    private void setListener(){
        titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(2);
                LessonDetail.this.finish();
            }
        });

        changeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder editCourse = new AlertDialog.Builder(LessonDetail.this);
                editCourse.setTitle("修改课程");
                LayoutInflater factor = LayoutInflater.from(LessonDetail.this);
                View view_in = factor.inflate(R.layout.course_edit_dialog_layout, null);
                editCourse.setView(view_in);
                final EditText editCourseName = view_in.findViewById(R.id.course_edit_name);
                final EditText editCourseRoom = view_in.findViewById(R.id.course_edit_room);
                final EditText editCourseStartHour = view_in.findViewById(R.id.course_edit_start_hour);
                final EditText editCourseStratMinute = view_in.findViewById(R.id.course_edit_start_minute);
                final EditText editCourseEndHour = view_in.findViewById(R.id.course_edit_end_hour);
                final EditText editCourseEndMinute = view_in.findViewById(R.id.course_edit_end_minute);
                final EditText editCourseTeacher = view_in.findViewById(R.id.course_edit_teacher);
                final Spinner editCourseweekday = view_in.findViewById(R.id.course_edit_weekday);
                editCourseName.setText(course.getCourseName());
                editCourseRoom.setText(course.getRoom());
                editCourseTeacher.setText(course.getTeacherName());
                editCourse.setPositiveButton("修改课程", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        course.setRoom(editCourseRoom.getText().toString());
                        course.setCourseName(editCourseName.getText().toString());
                        course.setStartTime(editCourseStartHour.getText().toString() + ":" + editCourseStratMinute.getText().toString());
                        course.setEndTime(editCourseEndHour.getText().toString() + ":" + editCourseEndMinute.getText().toString());
                        course.setWeekDay(editCourseweekday.getSelectedItem().toString());
                        course.setTeacherName(editCourseTeacher.getText().toString());
                        if (!editCourseName.getText().toString().isEmpty()) cdb.updateCourse(sname, course);
                        courseName.setText(course.getCourseName());
                        courseRoom.setText(course.getRoom());
                        courseStratTime.setText(course.getStartTime());
                        courseEndTime.setText(course.getEndTime());
                        courseTeacher.setText(course.getTeacherName());
                        courseDate.setText(course.getWeekDay());
                    }
                });
                editCourse.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });
                editCourse.show();
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder deleteCourse = new AlertDialog.Builder(LessonDetail.this);
                deleteCourse.setTitle("确认删除该课程？");
                deleteCourse.setPositiveButton("删除课程", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cdb.deleteCourse(sname, course.getCourseId());
                        setResult(2);
                        LessonDetail.this.finish();
                    }
                });
                deleteCourse.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });
                deleteCourse.show();
            }
        });

        newTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder addTaskAlertDialog = new AlertDialog.Builder(LessonDetail.this);
                addTaskAlertDialog.setTitle("添加任务");
                LayoutInflater factor = LayoutInflater.from(LessonDetail.this);
                View view_in = factor.inflate(R.layout.course_detail_add_task_dialog_layout, null);
                addTaskAlertDialog.setView(view_in);
                final EditText editTaskName = view_in.findViewById(R.id.course_detail_add_task_dialog_taskname);
                final EditText editTaskBrief = view_in.findViewById(R.id.course_detail_add_task_dialog_taskbrief);
                final EditText editTaskDDLYear = view_in.findViewById(R.id.course_detail_add_task_dialog_taskDDL_year);
                final EditText editTaskDDLMonth = view_in.findViewById(R.id.course_detail_add_task_dialog_taskDDL_month);
                final EditText editTaskDDLDay = view_in.findViewById(R.id.course_detail_add_task_dialog_taskDDL_day);
                addTaskAlertDialog.setPositiveButton("添加任务", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                addTaskAlertDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });
                addTaskAlertDialog.show();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_detail);
        initial();
        setListener();
    }
}
