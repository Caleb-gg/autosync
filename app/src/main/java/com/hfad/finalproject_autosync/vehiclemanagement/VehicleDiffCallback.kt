import androidx.recyclerview.widget.DiffUtil
import com.hfad.finalproject_autosync.vehiclemanagement.VehicleInfo

// DiffUtil.Callback implementation to efficiently update RecyclerView data
class VehicleDiffCallback(
    private val oldList: List<VehicleInfo>,  // List currently displayed in RecyclerView
    private val newList: List<VehicleInfo>   // New list with updated vehicle data
) : DiffUtil.Callback() {

    // Returns the size of the old list
    override fun getOldListSize(): Int = oldList.size

    // Returns the size of the new list
    override fun getNewListSize(): Int = newList.size

    // Checks if two items represent the same vehicle based on their unique ID
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    // Checks if the content of the two items is the same
    // If true, RecyclerView won't rebind that item
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}
