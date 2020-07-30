import android.content.Context
import androidx.appcompat.widget.SearchView
import android.util.AttributeSet

class EmptySubmitSearchView : SearchView {
    private var mSearchSrcTextView : SearchAutoComplete = SearchAutoComplete(context)
    private var listener: OnQueryTextListener? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun setOnQueryTextListener(listener: OnQueryTextListener?) {
        super.setOnQueryTextListener(listener)
        this.listener = listener
        mSearchSrcTextView = this.findViewById(androidx.appcompat.R.id.search_src_text)
        mSearchSrcTextView.setOnEditorActionListener { _, _, _ ->
            listener?.onQueryTextSubmit(query.toString())
            true
        }
    }
}