 package com.example.todolist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

 class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}

 data class Todo(val text : String,var isDone : Boolean) //자동으로 게터세터 모델클래스로 사용가능한 클래스 data class

 //리사이클러뷰 안에 데이터를 어떻게 표현할지를 정의 하는게 어뎁터고
 //리사이클러뷰에 어뎁터를 상속받는 나만의 어뎁터를 만들어야함 어뎁터 이름수

 class TodoAdapter(private val dataSet: List <Todo>) :
     RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

     //아이템하나에 뷰를 받겠
    class TodoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
         val textView: TextView
         init {//뷰바인딩을쓰면 findViewByIdf를 안쓰고도 xml에정의된 아이디를 다 쓸수 있다 
             textView = view.findViewById(R.id.todo_text)
         }
     }

     override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): TodoViewHolder {
         val view = LayoutInflater.from(viewGroup.context)
             .inflate(R.layout.item_todo, viewGroup, false)
         return TodoViewHolder(view)
     }

     override fun onBindViewHolder(viewHolder: TodoViewHolder, position: Int) {
         // 클래스의 text를 받아
         viewHolder.textView.text = dataSet[position].text
     }

     override fun getItemCount() = dataSet.size

 }
