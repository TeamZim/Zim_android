import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.zim_android.data.model.CountrySearchResponse
import com.example.zim_android.databinding.ViewMapDialog1ItemBinding

class CountryDropdownAdapter(
    private val onItemClick: (CountrySearchResponse) -> Unit
) : RecyclerView.Adapter<CountryDropdownAdapter.ViewHolder>() {

    private val items = mutableListOf<CountrySearchResponse>()

    fun updateItems(newItems: List<CountrySearchResponse>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ViewMapDialog1ItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CountrySearchResponse) {
            binding.itemCountryName.text = item.countryName
            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewMapDialog1ItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }
}
