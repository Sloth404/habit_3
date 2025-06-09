import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.moehr.habit_3.R
import com.moehr.habit_3.data.model.HabitType
import com.moehr.habit_3.data.model.RepeatPattern
import com.moehr.habit_3.ui.edit.EditItem
import com.moehr.habit_3.ui.edit.SectionType

class EditSectionAdapter(
    private val items: MutableList<EditItem>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_HABIT_TYPE = 1
        private const val TYPE_REMINDER = 2
    }

    override fun getItemViewType(position: Int): Int = when (items[position]) {
        is EditItem.Header -> TYPE_HEADER
        is EditItem.HabitTypeContent -> TYPE_HABIT_TYPE
        is EditItem.ReminderContent -> TYPE_REMINDER
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            TYPE_HEADER -> HeaderViewHolder(
                inflater.inflate(
                    R.layout.item_section_header,
                    parent,
                    false
                )
            )

            TYPE_HABIT_TYPE -> HabitTypeViewHolder(
                inflater.inflate(
                    R.layout.item_habit_type,
                    parent,
                    false
                )
            )

            TYPE_REMINDER -> ReminderViewHolder(
                inflater.inflate(
                    R.layout.item_reminder,
                    parent,
                    false
                )
            )

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is EditItem.Header -> (holder as HeaderViewHolder).bind(item, position)
            is EditItem.HabitTypeContent -> (holder as HabitTypeViewHolder).bind(item)
            is EditItem.ReminderContent -> (holder as ReminderViewHolder).bind(item)
        }
    }

    inner class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val title: TextView = view.findViewById(R.id.tvHeaderTitle)

        fun bind(header: EditItem.Header, position: Int) {
            title.text = header.title

            itemView.setOnClickListener {
                if (header.isExpanded) {
                    // collapse
                    header.isExpanded = false

                    // Avoid index out of bounds or wrong removal
                    if (position + 1 < items.size && !isHeader(items[position + 1])) {
                        items.removeAt(position + 1)
                        notifyItemRemoved(position + 1)
                    }

                } else {
                    // expand
                    header.isExpanded = true
                    val content = when (header.type) {
                        SectionType.HABIT_TYPE -> EditItem.HabitTypeContent(
                            habitType = HabitType.BUILD,
                            repeatPattern = RepeatPattern.DAILY,
                            unit = "",
                            target = 1
                        )
                        SectionType.REMINDER -> EditItem.ReminderContent()
                    }
                    items.add(position + 1, content)
                    notifyItemInserted(position + 1)
                }
            }
        }

        private fun isHeader(item: EditItem): Boolean = item is EditItem.Header
    }

    inner class HabitTypeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val btnBuild: Button = view.findViewById(R.id.btnBuild)
        private val btnBreak: Button = view.findViewById(R.id.btnBreak)
        private val btnRepeat: Button = view.findViewById(R.id.btnRepeatPattern)
        private val inputUnit: EditText = view.findViewById(R.id.editTextText)
        private val inputValue: EditText = view.findViewById(R.id.editTextText2)

        fun bind(item: EditItem.HabitTypeContent) {
            // Toggle habit type
            btnBuild.setOnClickListener {
                item.habitType = HabitType.BUILD
                btnBuild.isEnabled = false
                btnBreak.isEnabled = true
            }

            btnBreak.setOnClickListener {
                item.habitType = HabitType.BREAK
                btnBreak.isEnabled = false
                btnBuild.isEnabled = true
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

        fun bind(item: EditItem.ReminderContent) {
            swPN.isChecked = item.pushEnabled

            fun updateButtons() {
                val enabled = swPN.isChecked
                val selected = item.timesOfDay.firstOrNull()

                val timeButtons = listOf(
                    "MORNING" to btnMorning,
                    "NOON" to btnNoon,
                    "EVENING" to btnEvening,
                    "CUSTOM" to btnCustom
                )

                timeButtons.forEach { (label, button) ->
                    button.isEnabled = enabled && label != selected
                }
            }

            swPN.setOnCheckedChangeListener { _, isChecked ->
                item.pushEnabled = isChecked
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
