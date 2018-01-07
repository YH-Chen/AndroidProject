package com.example.great.project.Database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.great.project.Model.Task
import com.example.great.project.R
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by acera on 2018/1/5.
 * TODO:UNTESTED
 */

class TaskDB(context: Context?) : SQLiteOpenHelper(context, context!!.getString(R.string.DB_name), null, DB_VERSION)
{
    companion object
    {
        private val TASK_TABLE_NAME = "Task"
        private val REL_TABLE_NAME = "TaskRelation"
        private val DB_VERSION = 1
        private val taskTable = "create table " + TASK_TABLE_NAME +
                " (_id integer primary key autoincrement, " +
                "taskName text not null, " +
                "taskBrief text, " +
                "taskDDL text, " +
                "creatorName text);"
        private val relationTable = "create table " + REL_TABLE_NAME +
                "(tid integer not null," +
                "sName text not null," +
                "acceptInvitation integer," +
                "primary key(tid,sName));"
        private val DTF: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA)
    }

    override fun onCreate(db: SQLiteDatabase?)
    {
        db!!.execSQL(taskTable)
        db.execSQL(relationTable)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int)
    {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    //TODO 修改成text ，删除任务的时候级联
    //插入新任务
    fun newTask(data: Task, acceptInvitation: Boolean)
    {
        val db = writableDatabase
        val values = ContentValues()
        values.put("taskName", data.taskName)
        values.put("taskBrief", data.taskBrief)
        values.put("taskDDL", DTF.format(data.taskDDL))
        values.put("creatorName", data.creatorName)
        db.insert(TASK_TABLE_NAME, null, values)

        val clause = "taskName = ? and taskBrief = ? and taskDDL = ? and creatorName = ?"
        val clauseArgs = arrayOf(data.taskName, data.taskBrief, DTF.format(data.taskDDL), data.creatorName)
        val c = db.query(TASK_TABLE_NAME, null, clause, clauseArgs, null, null, null)

        var taskID = 5
        if (c.moveToNext())
            taskID = c.getInt(0)

        val value2 = ContentValues()
        value2.put("tid", taskID)
        value2.put("sName", data.creatorName)
        value2.put("acceptInvitation", acceptInvitation)
        db.insert(REL_TABLE_NAME, null, value2)

        c.close()
        db.close()
    }

    //根据名字更新一个任务，返回是否成功
    fun updateByTaskName(data: Task, userName: String): Boolean
    {
        var success = false

        val db = writableDatabase

        val selection = "_id = ?"
        val selectionArgs = arrayOf(data.id.toString())
        val c = db.query(TASK_TABLE_NAME, null, selection, selectionArgs, null, null, null)
        if (c.moveToNext())
        {
            if (c.getString(4) == userName)
            {
                val values = ContentValues()
                values.put("taskName", data.taskName)
                values.put("taskBrief", data.taskBrief)
                values.put("taskDDL", DTF.format(data.taskDDL))
                values.put("creatorName", data.creatorName)
                val whereClause = "_id = ?"
                val whereArgs = arrayOf(data.id.toString())
                db.update(TASK_TABLE_NAME, values, whereClause, whereArgs)
                success = true
            }
        }
        c.close()
        db.close()
        return success
    }

    //参与任务 返回是否成功
    fun joinTask(taskID: Int, studentName: String): Boolean
    {
        var success = false

        val db = writableDatabase

        val selection = "_id = ?"
        val selectionArgs = arrayOf(taskID.toString())
        val c = db.query(TASK_TABLE_NAME, null, selection, selectionArgs, null, null, null)
        if (c.moveToNext())
        {
            val values = ContentValues()
            values.put("tid", taskID)
            values.put("sName", studentName)
            val whereClause = "_id = ?"
            val whereArgs = arrayOf(taskID.toString())
            db.update(REL_TABLE_NAME, values, whereClause, whereArgs)
            success = true
        }
        c.close()
        db.close()
        return success
    }

    //退出一个任务
    fun quitTask(taskID: Int, studentName: String)
    {
        val db = writableDatabase
        val whereClause = "tid = ? and sName = ?"
        val whereArgs = arrayOf(taskID.toString(), studentName)
        db.delete(REL_TABLE_NAME, whereClause, whereArgs)

        val selection = "tid = ?"
        val selectionArgs = arrayOf(taskID.toString())
        val c = db.query(REL_TABLE_NAME, null, selection, selectionArgs, null, null, null)

        if (!c.moveToNext())//所有参与者被删除
        {
            val deleteClause = "_id"
            val deleteArgs = arrayOf(taskID.toString())
            db.delete(TASK_TABLE_NAME, deleteClause, deleteArgs)
        }

        c.close()
        db.close()
    }

    //根据名字删除任务
    fun deleteByTaskName(data: Task)
    {
        val db = writableDatabase
        val whereClause = "taskName = ?"
        val whereArgs = arrayOf(data.taskName)
        db.delete(TASK_TABLE_NAME, whereClause, whereArgs)

        //删除任务有关人员
        val deleteClause = "tid = ?"
        val deleteArgs = arrayOf(data.id.toString())
        db.delete(REL_TABLE_NAME, deleteClause, deleteArgs)

        db.close()
    }

    fun searchParticipantsByTaskID(taskID: Int): List<String>
    {
        val ans: LinkedList<String> = LinkedList()
        val db = readableDatabase
        val selection = "tid = ?"
        val selectionArgs = arrayOf(taskID.toString())
        val c = db.query(REL_TABLE_NAME, null, selection, selectionArgs, null, null, null)

        if (c.moveToNext())
        {
            ans.add(c.getString(1))
            while (c.moveToNext())
                ans.add(c.getString(1))
        }
        c.close()
        db.close()
        return ans
    }

    //根据任务名字查询任务
    fun searchByTaskName(taskName: String): List<Task>
    {
        val ans: LinkedList<Task> = LinkedList()
        val db = readableDatabase
        val selection = "taskName = ?"
        val selectionArgs = arrayOf(taskName)
        val c = db.query(TASK_TABLE_NAME, null, selection, selectionArgs, null, null, null)

        if (c.moveToNext())
        {
            ans.add(Task(c.getInt(0), c.getString(1),
                    c.getString(2), DTF.parse(c.getString(3)), c.getString(4)))
            while (c.moveToNext())
                ans.add(Task(c.getInt(0), c.getString(1),
                        c.getString(2), DTF.parse(c.getString(3)), c.getString(4)))
        }
        c.close()
        db.close()
        return ans
    }

    //根据 id 查询任务
    fun searchByTaskID(id: Int): Task?
    {
        var ans: Task? = null
        val db = readableDatabase
        val selection = "_id = ?"
        val selectionArgs = arrayOf(id.toString())
        val c = db.query(TASK_TABLE_NAME, null, selection, selectionArgs, null, null, null)

        if (c.moveToNext())
        {
            ans = Task(c.getInt(0), c.getString(1),
                    c.getString(2), DTF.parse(c.getString(3)), c.getString(4))
        }
        c.close()
        db.close()
        return ans
    }

    //根据创建者 id 获取任务
    fun searchByCreatorID(creatorName: String): List<Task>
    {
        val ans: LinkedList<Task> = LinkedList()
        val db = readableDatabase
        val selection = "creatorName = ?"
        val selectionArgs = arrayOf(creatorName)
        val c = db.query(TASK_TABLE_NAME, null, selection, selectionArgs, null, null, null)

        if (c.moveToNext())
        {
            ans.add(Task(c.getInt(0), c.getString(1),
                    c.getString(2), DTF.parse(c.getString(3)), c.getString(4)))
            while (c.moveToNext())
                ans.add(Task(c.getInt(0), c.getString(1),
                        c.getString(2), DTF.parse(c.getString(3)), c.getString(4)))
        }
        c.close()
        db.close()
        return ans
    }

    //获取所有任务
    fun allTasks(): List<Task>
    {
        val ans = LinkedList<Task>()
        val db = readableDatabase
        val c = db.query(TASK_TABLE_NAME, null, null, null, null, null, null)

        while (c.moveToNext())
        {
            val temp = Task(c.getInt(0), c.getString(1),
                    c.getString(2), DTF.parse(c.getString(3)), c.getString(4))
            ans.add(temp)
        }
        c.close()
        db.close()
        return ans
    }
}