import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kittipobandroid.House
import com.example.kittipobandroid.R
import com.example.kittipobandroid.databinding.ItemHouseBinding

class HouseAdapter(private var house: List<House>) : RecyclerView.Adapter<HouseAdapter.HouseViewHolder>() {

    private val baseImageUrl = "http://10.13.4.106:3000/uploads/" // Your image server URL

    // ViewHolder class
    inner class HouseViewHolder(private val binding: ItemHouseBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(house: House) {
            // Bind data to the views
            binding.houseAreaSizeTextView.text = house.AreaSize.toString()
            binding.bedroomsTextView.text = house.Bedrooms.toString()
            binding.bathroomsTextView.text = house.Bathrooms.toString()
            binding.housePriceTextView.text = house.Price.toString()
            binding.conditionTextView.text = house.HouseCondition
            binding.typeTextView.text = house.HouseType
            binding.yearBuiltTextView.text = house.YearBuilt.toString()
            binding.parkingSpacesTextView.text = house.ParkingSpaces.toString()
            binding.addressTextView.text = house.Address

            // Construct image URL and load it using Glide
            val imageUrl = if (!house.HouseImage.isNullOrEmpty()) {
                "$baseImageUrl${house.HouseImage}"
            } else {
                null
            }

            // Load image with Glide
            Glide.with(binding.houseImageView.context)
                .load(imageUrl)
                .placeholder(R.drawable.ic_launcher_background) // Replace with your placeholder image resource
                .error(R.drawable.ic_launcher_background) // Replace with your error image resource
                .into(binding.houseImageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HouseViewHolder {
        // Inflate the layout for each item
        val binding = ItemHouseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HouseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HouseViewHolder, position: Int) {
        // Bind each item to the ViewHolder
        holder.bind(house[position])
    }

    override fun getItemCount() = house.size

    // Function to update the list of houses and notify the adapter
    fun updateHouses(newHouses: List<House>) {
        val diffCallback = HousesDiffCallback(house, newHouses)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        house = newHouses
        diffResult.dispatchUpdatesTo(this)
    }

    // DiffUtil.Callback implementation to handle list updates efficiently
    private class HousesDiffCallback(
        private val oldList: List<House>,
        private val newList: List<House>
    ) : DiffUtil.Callback() {

        override fun getOldListSize() = oldList.size

        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}
