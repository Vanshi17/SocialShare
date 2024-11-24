import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class GridSpacingItemDecoration(private val spanCount: Int, private val spacing: Int) :
    RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view) // Get position of the item
        val column = position % spanCount // Get column index

        // Add spacing on the left and right of the items, depending on the column
        outRect.left = spacing - column * spacing / spanCount
        outRect.right = (column + 1) * spacing / spanCount

        // Add spacing at the top of the first row only
        if (position >= spanCount) {
            outRect.top = spacing // Remove any gap above rows except the first one
        }
        outRect.bottom = spacing // Add spacing at the bottom of every item
    }
}
