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

class EditSectionAdapter(
    private val items: MutableList<EditItem>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_HABIT_TYPE = 0
        private const val TYPE_REMINDER = 1
    }

    override fun getItemViewType(position: Int): Int = when (items[position]) {
        is EditItem.HabitTypeContent -> TYPE_HABIT_TYPE
        is EditItem.ReminderContent -> TYPE_REMINDER
    }

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

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is EditItem.HabitTypeContent -> (holder as HabitTypeViewHolder).bind(item)
            is EditItem.ReminderContent -> (holder as ReminderViewHolder).bind(item)
        }
    }

    inner class HabitTypeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val btnBuild: Button = view.findViewById(R.id.btnBuild)
        private val btnBreak: Button = view.findViewById(R.id.btnBreak)
        private val btnRepeat: Button = view.findViewById(R.id.btnRepeat)
        private val inputUnit: EditText = view.findViewById(R.id.etUnit)
        private val inputValue: EditText = view.findViewById(R.id.etTarget)

        fun bind(item: EditItem.HabitTypeContent) {
            btnBuild.setTextColor(ContextCompat.getColor(itemView.context, R.color.torquoise))

            // Toggle habit type
            btnBuild.setOnClickListener {
                item.habitType = HabitType.BUILD
                btnBuild.isEnabled = false
                btnBreak.isEnabled = true
                btnBuild.setTextColor(ContextCompat.getColor(itemView.context, R.color.torquoise))
                btnBreak.setTextColor(ContextCompat.getColor(itemView.context, R.color.white))
            }

            btnBreak.setOnClickListener {
                item.habitType = HabitType.BREAK
                btnBreak.isEnabled = false
                btnBuild.isEnabled = true
                btnBuild.setTextColor(ContextCompat.getColor(itemView.context, R.color.white))
                btnBreak.setTextColor(ContextCompat.getColor(itemView.context, R.color.torquoise))
            }

            // Initial UI sync
            btnBuild.isEnabled = item.habitType != HabitType.BUILD
            btnBreak.isEnabled = item.habitType != HabitType.BREAK

            // Cycle repeat pattern
            btnRepeat.setOnClickListener {
                item.repeatPattern = when (item.repeatPattern) {
                    RepeatPattern.DAILY -> RepeatPattern.WEEKLY
                    RepeatPattern.WEEKLY -> RepeatPattern.DAILY
                }
                btnRepeat.text = item.repeatPattern.name
            }
            btnRepeat.text = item.repeatPattern.name

            // Handle unit and target value
            inputUnit.setText(item.unit)
            inputValue.setText(item.target.toString())

            inputUnit.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) item.unit = inputUnit.text.toString()
            }

            inputValue.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    val text = inputValue.text.toString()
                    item.target = text.toIntOrNull() ?: 0
                }
            }
        }
    }

    inner class ReminderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val swPN: Switch = view.findViewById(R.id.swPN)
        private val btnMorning: Button = view.findViewById(R.id.btnMorning)
        private val btnNoon: Button = view.findViewById(R.id.btnNoon)
        private val btnEvening: Button = view.findViewById(R.id.btnEvening)
        private val btnCustom: Button = view.findViewById(R.id.btnCustom)

        private val buttons = listOf(
            "MORNING" to btnMorning,
            "NOON" to btnNoon,
            "EVENING" to btnEvening,
            "CUSTOM" to btnCustom
        )

        fun bind(item: EditItem.ReminderContent) {
            swPN.isChecked = item.pushEnabled

            fun updateButtons() {
                val isEnabled = swPN.isChecked
                val selected = item.timesOfDay.firstOrNull()

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

            swPN.setOnCheckedChangeListener { _, isChecked ->
                item.pushEnabled = isChecked

                // Reset selection if turning ON with empty selection
                if (isChecked && item.timesOfDay.isEmpty()) {
                    item.timesOfDay = listOf("MORNING")
                }

                updateButtons()
            }

            fun handleTimeClick(label: String) {
                item.timesOfDay = listOf(label)
                updateButtons()
            }

            btnMorning.setOnClickListener { handleTimeClick("MORNING") }
            btnNoon.setOnClickListener { handleTimeClick("NOON") }
            btnEvening.setOnClickListener { handleTimeClick("EVENING") }
            btnCustom.setOnClickListener { handleTimeClick("CUSTOM") }

            updateButtons()
        }
    }
}
