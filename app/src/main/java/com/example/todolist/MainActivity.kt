package com.example.todolist

import android.graphics.Paint
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.databinding.ActivityMainBinding
import com.example.todolist.databinding.ItemTodoBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

//         어뎁터에 임시로 데이터를 넣는다
//        data.add(Todo("숙제"))
//        data.add(Todo("청소", true))

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = TodoAdapter(
                emptyList(),
                onClickDeleteIcon = {
                    viewModel.deleteTodo(it)
                },
                onClickItem = {
                    viewModel.toggleTodo(it)
                }
            )
        }
        //adapter 처음 적용하면 안될 수도 있는데 rebuilding하자
        binding.addButton.setOnClickListener() {
            val todo = Todo(binding.editText.text.toString())
            viewModel.addTodo(todo)
        }

        //관창 -> ui 업데이트
        viewModel.todoLiveData.observe(this, Observer {
            (binding.recyclerView.adapter as TodoAdapter).setData(it)
        })
    }
}

data class Todo(
    val text: String,
    var isDone: Boolean = false
) //자동으로 게터세터 모델클래스로 사용가능한 클래스 data class

//리사이클러뷰 안에 데이터를 어떻게 표현할지를 정의 하는게 어뎁터고
//리사이클러뷰에 어뎁터를 상속받는 나만의 어뎁터를 만들어야함 어뎁터 이름수

class TodoAdapter(
    private var dataSet: List<Todo>,
    val onClickDeleteIcon: (todo: Todo) -> Unit, //외부로 데이터를 넘기기 위한 함수
    val onClickItem: (todo: Todo) -> Unit //외부로 데이터를 넘기기 위한 함수
) :
    RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    //아이템하나에 뷰를 받겠다
    // 뷰 홀더 인자를 뷰로 쓸 경우
//    class TodoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//         val textView: TextView
//         init {//뷰바인딩을쓰면 findViewByIdf를 안쓰고도 xml에정의된 아이디를 다 쓸수 있다
//             textView = view.findViewById(R.id.todo_text)
//         }
//     }

    // 뷰 홀더 인자를 바인딩으로 쓸 경우
    class TodoViewHolder(val binding: ItemTodoBinding) : RecyclerView.ViewHolder(binding.root)
    //모든 바인딩객체는 루트라는 프로퍼티가있어서 본인이 어떤 뷰로부터 생성된건지 알 수 있음.

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): TodoViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_todo, viewGroup, false)
//         return TodoViewHolder(view)
        return TodoViewHolder(ItemTodoBinding.bind(view))
    }

    override fun onBindViewHolder(viewHolder: TodoViewHolder, position: Int) {
        //기본적인 뷰 내용을 담당함
        val todo = dataSet[position]
        viewHolder.binding.todoText.text = todo.text

        //완료 사선, 글씨체 구현
        if(todo.isDone) {
            viewHolder.binding.todoText.apply { //apply this로 받
                this.paintFlags = this.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                setTypeface(null, Typeface.ITALIC)
            }
        }else{
            viewHolder.binding.todoText.apply {
                this.paintFlags = 0
                setTypeface(null, Typeface.NORMAL)
            }
        }

        viewHolder.binding.deleteImageView.setOnClickListener(){
            onClickDeleteIcon.invoke(todo)
        }
        viewHolder.binding.root.setOnClickListener(){
            onClickItem.invoke(todo) //invoke 함수를 실행
        }
        // 클래스의 text를 받아옴
        //viewHolder.textView.text = dataSet[position].text
    }

    override fun getItemCount() = dataSet.size

    //라이브데이터 값이 변경되었을 때 필요한 메소 - 데이터갱신
    fun setData(newData:List<Todo>){
        dataSet = newData
        notifyDataSetChanged()
    }

}

class MainViewModel: ViewModel(){
    //view model
    //메인 액티비티의 데이터관련을 뷰모델로 다 몰고 액티비티에서는 ui적 요소만관리
    //왜냐하면 rotate같은 행위가 추가되면 액티비티는 destory되고 create되기 때문에 다 저장한 데이터가 다 날라간다.
    //뷰모델의 life cycle은 finish전까지 유효하기 때문에 액티비티의 생명주기와 무관하게 데이터를 관리할 수 있다.

    //live data
    //데이터를 관찰하고 있다가 데이터가 바뀌면 적용할 수 있도록 해주는 간편한
    //상태를 변경가능하고 관찰가능한 라이브데이터 객체
    //화면 갱신코드를 모아놓을 수 있고, 어느타이밍에 화면을 갱신해줘야하는 것을 신경쓰지 않아도 된다.

    val todoLiveData = MutableLiveData<List<Todo>>()
    private val data = arrayListOf<Todo>()

    fun toggleTodo(todo: Todo) {
        todo.isDone = !todo.isDone
        todoLiveData.value = data
    }

    fun addTodo(todo: Todo) {
        data.add(todo)
        //value를 변경된 최신 데이터로 바꾸겠다
        todoLiveData.value = data
    }

    fun deleteTodo(todo: Todo) {
        data.remove(todo)
        todoLiveData.value = data
    }
}
