import android.R
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.zim_android.data.model.CountryItem
import com.example.zim_android.databinding.MypageDialogGridItemBinding

class DialogMypage1Adapter(
    private val context: Context,
    private val items: List<CountryItem> // CountryItem 데이터 클래스로 받아오기
) : BaseAdapter() {

    override fun getCount(): Int = items.size
    override fun getItem(position: Int): Any = items[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val binding: MypageDialogGridItemBinding
        val view: View

        if (convertView == null) {
            val inflater = LayoutInflater.from(context)
            binding = MypageDialogGridItemBinding.inflate(inflater, parent, false)
            view = binding.root
            view.tag = binding
        } else {
            view = convertView
            binding = view.tag as MypageDialogGridItemBinding
        }

        val item = items[position]
        binding.flagText.text = item.flag
        binding.countryText.text = item.countryName

        return view
    }
}