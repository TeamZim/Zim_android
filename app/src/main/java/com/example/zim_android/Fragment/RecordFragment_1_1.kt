package com.example.zim_android.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.zim_android.R
import com.example.zim_android.databinding.Record11FragmentBinding

class RecordFragment_1_1: Fragment(R.layout.record_1_1_fragment){

    // 프래그먼트의 뷰 바인딩 객체를 담는 변수
    // 언더바 _는 외부에서 직접 접근하지 말고 내부에서만 쓸게요 라는 개발자의 의도 표현
    private var _binding: Record11FragmentBinding? = null
    // _binding을 non-null로 안전하게 꺼내 쓰기 위한 프로퍼티
    private val binding get() = _binding!!


    // 뷰를 생성해서 반환하는 함
    // 꼭 view를 return 해야함.
    // 여기서 레이아웃 파일을 연결하거나, ViewBinding의 inflate()를 써서 binding.root를 return함
    // binding.root는 **바인딩된 레이아웃 XML의 최상위 뷰(View)**를 의미
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // ViewBinding을 이용해 레이아웃 XML을 인플레이트(화면에 올리는 작업) 하는 코
        // Android Studio가 자동으로 만들어주는 바인딩 클래스 이름
        // record_1_1_fragment.xml ↔ Record11FragmentBinding 이렇게 매칭

        // .inflate(inflater, container, false)
        // inflater → XML을 실제 View 객체로 변환해주는 도구 (LayoutInflater)
        //container → 프래그먼트가 붙을 부모 ViewGroup (보통 FrameLayout 같은 거)
        //false → 바로 container에 붙이지 말라는 의미 (우리가 수동으로 return할 거니까!)

        // record_1_1_fragment.xml이라는 XML을 화면에 그릴 수 있도록 View 객체로 바꿔서 _binding에 담는다
        _binding = Record11FragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // 부모 클래스인 Fragment의 onViewCreated() 메서드를 호출
        // 부모(Fragment) 클래스가 원래 하기로 한 기본 작업들도 그대로 실행되게 해주는 것
        super.onViewCreated(view, savedInstanceState)

        // tvtitle text 변경
        binding.commonHeader.tvTitle.text = "기록하기"

        // constraint1 버튼 클릭 navigate 전환 이루어짐
        binding.constraint1.setOnClickListener {
            findNavController().navigate(R.id.action_recordFragment_1_1_to_recordFragment_1_2)
        }

//        binding.constraint2.setOnClickListener {
//            findNavController().navigate(R.id.action_recordFragment_1_1_to_recordFragment_1_2)
//        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
