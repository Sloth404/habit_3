import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.moehr.habit_3.R
import com.moehr.habit_3.data.model.HabitType
import com.moehr.habit_3.data.model.RepeatPattern
import com.moehr.habit_3.ui.edit.EditItem

/**
 * RecyclerView Adapter for editing sections in habit details.
 * Supports two types of items: HabitTypeContent and ReminderContent.
 *
 * @param items Mutable list of EditItem to display and edit.
 */
class EditSectionAdapter(
    val items: MutableList<EditItem>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_HABIT_TYPE = 0
        private const val TYPE_REMINDER = 1
    }

    /**
     * Returns the view type for the item at the given position.
     */
    override fun getItemViewType(position: Int): Int = when (items[position]) {
        is EditItem.HabitTypeContent -> TYPE_HABIT_TYPE
        is EditItem.ReminderContent -> TYPE_REMINDER
    }

    /**
     * Creates appropriate ViewHolder based on the view type.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            TYPE_HABIT_TYPE -> HabitTypeViewHolder(
                inflater.inflate(R.layout.item_habit_type, parent, false)
            )
            TYPE_REMINDER -> ReminderViewHolder(
                inflater.inflate(R.layout.item_reminder, parent, false)
            )
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    /**
     * Returns total number of items to display.
     */
    override fun getItemCount(): Int = items.size

    /**
     * Binds the data for each ViewHolder depending on the item type.
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is EditItem.HabitTypeContent -> (holder as HabitTypeViewHolder).bind(item)
            is EditItem.ReminderContent -> (holder as ReminderViewHolder).bind(item)
        }
    }

    /**
     * ViewHolder for the habit type editing section.
     * Allows toggling between Build and Break habit types,
     * editing repeat pattern, unit, and target value.
     */
    inner class HabitTypeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val btnBuild: Button = view.findViewById(R.id.btnBuild)
        private val btnBreak: Button = view.findViewById(R.id.btnBreak)
        private val btnRepeat: Button = view.findViewById(R.id.btnRepeat)
        private val inputUnit: EditText = view.findViewById(R.id.etUnit)
        private val inputValue: EditText = view.findViewById(R.id.etTarget)

        /**
         * Binds the HabitTypeContent data to UI and handles interaction logic.
         */
        fun bind(item: EditItem.HabitTypeContent) {
            // Highlight Build button initially (color used for enabled)
            btnBuild.setTextColor(ContextCompat.getColor(itemView.context, R.color.torquoise))

            // Toggle to BUILD habit type when btnBuild clicked
            btnBuild.setOnClickListener {
                item.habitType = HabitType.BUILD
                btnBuild.isEnabled = false
                btnBreak.isEnabled = true
                btnBuild.setTextColor(ContextCompat.getColor(itemView.context, R.color.torquoise))
                btnBreak.setTextColor(ContextCompat.getColor(itemView.context, R.color.white))
            }

            // Toggle to BREAK habit type when btnBreak clicked
            btnBreak.setOnClickListener {
                item.habitType = HabitType.BREAK
                btnBreak.isEnabled = false
                btnBuild.isEnabled = true
                btnBuild.setTextColor(ContextCompat.getColor(itemView.context, R.color.white))
                btnBreak.setTextColor(ContextCompat.getColor(itemView.context, R.color.torquoise))
            }

            // Synchronize initial button enabled states based on current habit type
            btnBuild.isEnabled = item.habitType != HabitType.BUILD
            btnBreak.isEnabled = item.habitType != HabitType.BREAK

            // Cycle repeat pattern between DAILY and WEEKLY on btnRepeat click
            btnRepeat.setOnClickListener {
                item.repeatPattern = when (item.repeatPattern) {
                    RepeatPattern.DAILY -> RepeatPattern.WEEKLY
                    RepeatPattern.WEEKLY -> RepeatPattern.DAILY
                }
                btnRepeat.text = item.repeatPattern.name
            }
            // Set repeat button text to current pattern initially
            btnRepeat.text = item.repeatPattern.name

            // Set unit and target input fields from data
            inputUnit.setText(item.unit)
            inputValue.setText(item.target.toString())

            // Save unit value when input loses focus
            inputUnit.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    item.unit = inputUnit.text.toString()
                }
            }

            // Save target value when input loses focus, defaulting to 0 if invalid
            inputValue.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    val text = inputValue.text.toString()
                    item.target = text.toIntOrNull() ?: 0
                }
            }
        }
    }

    /**
     * ViewHolder for reminder editing section.
     * Manages push notification toggle and selecting notification time.
     */
    inner class ReminderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val swPN: Switch = view.findViewById(R.id.swPN)
        private val btnMorning: Button = view.findViewById(R.id.btnMorning)
        private val btnNoon: Button = view.findViewById(R.id.btnNoon)
        private val btnEvening: Button = view.findViewById(R.id.btnEvening)
        private val btnCustom: Button = view.findViewById(R.id.btnCustom)

        // List pairing time labels with their respective buttons for easy iteration
        private val buttons = listOf(
            "MORNING" to btnMorning,
            "NOON" to btnNoon,
            "EVENING" to btnEvening,
            "CUSTOM" to btnCustom
        )

        /**
         * Binds ReminderContent data to UI and handles toggle and button state updates.
         */
        fun bind(item: EditItem.ReminderContent) {
            swPN.isChecked = item.pushEnabled

            /**
             * Updates buttons' enabled state and colors depending on push notification state and selected time.
             */
            fun updateButtons() {
                val isEnabled = swPN.isChecked
                val selected = item.timeOfDay

                buttons.forEach { (label, button) ->
                    button.isEnabled = isEnabled
                    val textColor = when {
                        !isEnabled -> ContextCompat.getColor(itemView.context, R.color.dark_grey)
                        label == selected -> ContextCompat.getColor(itemView.context, R.color.torquoise)
                        else -> ContextCompat.getColor(itemView.context, R.color.white)
                    }
                    button.setTextColor(textColor)
                }
            }

            // Toggle push notification enabled state and update buttons accordingly
            swPN.setOnCheckedChangeListener { _, isChecked ->
                item.pushEnabled = isChecked

                // If enabling push notifications with no selected time, default to MORNING
                if (isChecked && item.timeOfDay.isEmpty()) {
                    item.timeOfDay = "MORNING"
                }

                updateButtons()
            }

            /**
             * Handles click on a time button to select that time for notification.
             */
            fun handleTimeClick(label: String) {
                item.timeOfDay = label
                updateButtons()
            }

            // Set click listeners for each time button
            btnMorning.setOnClickListener { handleTimeClick("MORNING") }
            btnNoon.setOnClickListener { handleTimeClick("NOON") }
            btnEvening.setOnClickListener { handleTimeClick("EVENING") }
            btnCustom.setOnClickListener { handleTimeClick("CUSTOM") }

            // Initialize buttons state on binding
            updateButtons()
        }
    }
}
