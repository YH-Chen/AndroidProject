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
import com.example.great.project.Model.CourseModel;
import com.example.great.project.R;

import java.util.ArrayList;
import java.util.List;
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

    private CourseDB cdb = new CourseDB(this);

    private TextView courseName;
    private TextView courseRoom;
    private TextView courseStratTime;
    private TextView courseEndTime;
    private TextView courseTeacher;
    private TextView courseDate;
    private ImageView backBtn;
    private Button changeBtn;
    private Button deleteBtn;
    private RecyclerView TaskRec;

    private String sname;
    private List<Map<String, Object>> taskItem = new ArrayList<>();
    private CommonAdapter taskAdp;
    private CourseModel course;

    private void initial(){
        courseName = findViewById(R.id.course_detail_name);
        courseRoom = findViewById(R.id.course_detail_room);
        courseStratTime = findViewById(R.id.course_detail_start_time);
        courseEndTime = findViewById(R.id.course_detail_end_time);
        courseTeacher = findViewById(R.id.course_detail_teacher);
        courseDate = findViewById(R.id.course_detail_weekday);
        backBtn = findViewById(R.id.course_detail_back);
        changeBtn = findViewById(R.id.course_detail_change);
        deleteBtn = findViewById(R.id.course_detail_delete);
        TaskRec = findViewById(R.id.course_detail_taskrec);

        Intent intent = getIntent();
        sname = intent.getExtras().getString("sname");
        course = (CourseModel) intent.getSerializableExtra("course");

        courseName.setText(course.getCourseName());
        courseRoom.setText(course.getRoom());
        courseStratTime.setText(course.getStartTime());
        courseEndTime.setText(course.getEndTime());
        courseTeacher.setText(course.getTeacherName());
        courseDate.setText(course.getWeekDay());
    }

    private void setListener(){
        backBtn.setOnClickListener(new View.OnClickListener() {
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
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_detail);
        initial();
        setListener();
    }
}
